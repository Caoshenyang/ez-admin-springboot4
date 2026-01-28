package com.ez.admin.common.framework.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.ez.admin.common.model.annotation.OperationLog;
import com.ez.admin.modules.system.entity.SysOperationLog;
import com.ez.admin.modules.system.mapper.SysOperationLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * 操作日志 AOP 切面
 * <p>
 * 自动拦截标注了 @OperationLog 注解的方法，记录操作日志
 * </p>
 * <p>
 * 记录内容：
 * <ul>
 *   <li>模块名称、操作类型、操作描述</li>
 *   <li>用户信息（用户ID、用户名）</li>
 *   <li>请求信息（请求方法、URL、IP、参数）</li>
 *   <li>执行信息（执行时长、状态、错误信息）</li>
 * </ul>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final SysOperationLogMapper operationLogMapper;
    private final ObjectMapper objectMapper;

    /**
     * 定义切点：拦截所有标注了 @OperationLog 注解的方法
     */
    @Pointcut("@annotation(com.ez.admin.common.model.annotation.OperationLog)")
    public void operationLogPointcut() {
    }

    /**
     * 环绕通知：记录操作日志
     */
    @Around("operationLogPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();

        // 获取注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        OperationLog annotation = signature.getMethod().getAnnotation(OperationLog.class);

        // 构建日志对象
        SysOperationLog logEntity = new SysOperationLog();
        logEntity.setModule(annotation.module());
        logEntity.setOperation(annotation.operation());
        logEntity.setDescription(annotation.description());
        logEntity.setRequestMethod(request.getMethod());
        logEntity.setRequestUrl(request.getRequestURI());
        logEntity.setRequestIp(getClientIp(request));

        // 获取当前用户信息
        try {
            if (StpUtil.isLogin()) {
                Object loginId = StpUtil.getLoginIdDefaultNull();
                if (loginId != null) {
                    logEntity.setUserId(Long.parseLong(loginId.toString()));
                    // 从 Session 中获取用户名
                    String username = (String) StpUtil.getSession().get("username");
                    if (username != null && !username.isEmpty()) {
                        logEntity.setUsername(username);
                    } else {
                        // 如果 Session 中没有，使用 loginId 作为备用
                        logEntity.setUsername(loginId.toString());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("获取当前登录用户信息失败：{}", e.getMessage());
        }

        // 记录请求参数
        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                // 过滤掉 HttpServletRequest 和 HttpServletResponse 对象
                Object[] filteredArgs = filterArgs(args);
                if (filteredArgs.length > 0) {
                    String params = objectMapper.writeValueAsString(filteredArgs);
                    // 限制参数长度，避免过长
                    if (params.length() > 2000) {
                        params = params.substring(0, 2000) + "...";
                    }
                    logEntity.setRequestParams(params);
                }
            }
        } catch (Exception e) {
            log.warn("序列化请求参数失败：{}", e.getMessage());
            logEntity.setRequestParams("参数序列化失败");
        }

        // 执行目标方法
        Object result = null;
        try {
            result = joinPoint.proceed();
            logEntity.setStatus(1); // 成功
        } catch (Throwable throwable) {
            logEntity.setStatus(0); // 失败
            // 限制错误信息长度
            String errorMsg = throwable.getMessage();
            if (errorMsg != null && errorMsg.length() > 1000) {
                errorMsg = errorMsg.substring(0, 1000) + "...";
            }
            logEntity.setErrorMsg(errorMsg);
            throw throwable;
        } finally {
            // 计算执行时长
            long executeTime = System.currentTimeMillis() - startTime;
            logEntity.setExecuteTime(executeTime);
            logEntity.setCreateTime(LocalDateTime.now());

            // 异步保存日志（避免影响业务性能）
            saveLogAsync(logEntity);
        }

        return result;
    }

    /**
     * 异步保存操作日志
     *
     * @param logEntity 日志实体
     */
    @Async
    public void saveLogAsync(SysOperationLog logEntity) {
        try {
            operationLogMapper.insert(logEntity);
            log.debug("操作日志保存成功：{} - {}", logEntity.getModule(), logEntity.getDescription());
        } catch (Exception e) {
            log.error("保存操作日志失败：{} - {} - {}", logEntity.getModule(), logEntity.getDescription(), e.getMessage());
        }
    }

    /**
     * 过滤请求参数，排除不需要序列化的对象
     *
     * @param args 原始参数数组
     * @return 过滤后的参数数组
     */
    private Object[] filterArgs(Object[] args) {
        return java.util.Arrays.stream(args)
                .filter(arg -> !(arg instanceof HttpServletRequest)
                        && !(arg instanceof jakarta.servlet.http.HttpServletResponse)
                        && !(arg instanceof org.springframework.web.multipart.MultipartFile))
                .toArray();
    }

    /**
     * 获取客户端 IP 地址
     * <p>
     * 优先级：X-Forwarded-For > X-Real-IP > Proxy-Client-IP > WL-Proxy-Client-IP > RemoteAddr
     * </p>
     *
     * @param request 请求对象
     * @return IP 地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多级代理的情况，取第一个 IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}

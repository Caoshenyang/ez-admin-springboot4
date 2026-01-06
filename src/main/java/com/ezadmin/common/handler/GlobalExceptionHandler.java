package com.ezadmin.common.handler;

import cn.dev33.satoken.exception.*;
import com.ezadmin.common.exception.ExceptionEnum;
import com.ezadmin.common.exception.EzAdminException;
import com.ezadmin.common.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * <p>
 * 全局异常处理类
 * 统一处理所有异常，返回标准的Result格式
 * </p>
 *
 * @author 曹申阳
 * @since 2025-12-15 10:47:59
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(EzAdminException.class)
    public Result<?> handleBusinessException(EzAdminException e) {
        log.error("业务异常: {}", e.getMessage(), e);
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理请求参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("请求参数验证异常: {}", e.getMessage(), e);
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
            .findFirst()
            .orElse("请求参数验证失败");
        return Result.error(400, message);
    }

    /**
     * 处理缺少请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("缺少请求参数异常: {}", e.getMessage(), e);
        String message = "缺少必要参数：" + e.getParameterName();
        return Result.error(400, message);
    }

    /**
     * 处理请求参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("请求参数类型不匹配异常: {}", e.getMessage(), e);
        String message = "参数类型不匹配：" + e.getName() + " 应类型为 " + (e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "未知类型");
        return Result.error(400, message);
    }

    // 拦截：未登录异常
    @ExceptionHandler(NotLoginException.class)
    public Result<?> handlerException(NotLoginException e) {
        log.error("未登录异常: {}", e.getMessage(), e);
        
        // 根据不同的异常类型返回更具体的提示
        String type = e.getType();
        if (NotLoginException.TOKEN_TIMEOUT.equals(type)) {
            // token过期
            return Result.error(ExceptionEnum.TOKEN_EXPIRED.getCode(), ExceptionEnum.TOKEN_EXPIRED.getMessage());
        } else if (NotLoginException.TOKEN_FREEZE.equals(type)) {
            // token被冻结
            return Result.error(ExceptionEnum.TOKEN_FROZEN.getCode(), ExceptionEnum.TOKEN_FROZEN.getMessage());
        } else if (NotLoginException.NOT_TOKEN.equals(type) || NotLoginException.INVALID_TOKEN.equals(type) || NotLoginException.NO_PREFIX.equals(type)) {
            // 未提供token、token无效、未按指定前缀提交token
            return Result.error(ExceptionEnum.TOKEN_INVALID.getCode(), ExceptionEnum.TOKEN_INVALID.getMessage());
        } else if (NotLoginException.BE_REPLACED.equals(type) || NotLoginException.KICK_OUT.equals(type)) {
            // token被顶下线、被踢下线
            return Result.error(ExceptionEnum.LOGIN_STATUS_ERROR.getCode(), ExceptionEnum.LOGIN_STATUS_ERROR.getMessage());
        } else {
            // 其他未登录情况
            return Result.error(ExceptionEnum.USER_NOT_LOGGED_IN.getCode(), ExceptionEnum.USER_NOT_LOGGED_IN.getMessage());
        }
    }

    // 拦截：缺少权限异常
    @ExceptionHandler(NotPermissionException.class)
    public Result<?> handlerException(NotPermissionException e) {
        log.error("缺少权限异常: {}", e.getMessage(), e);
        return Result.error("缺少权限");
    }

    // 拦截：缺少角色异常
    @ExceptionHandler(NotRoleException.class)
    public Result<?> handlerException(NotRoleException e) {
        log.error("缺少角色异常: {}", e.getMessage(), e);
        return Result.error("缺少角色");
    }

    // 拦截：二级认证校验失败异常
    @ExceptionHandler(NotSafeException.class)
    public Result<?> handlerException(NotSafeException e) {
        log.error("二级认证校验失败异常: {}", e.getMessage(), e);
        return Result.error("二级认证校验失败");
    }

    // 拦截：服务封禁异常
    @ExceptionHandler(DisableServiceException.class)
    public Result<?> handlerException(DisableServiceException e) {
        log.error("服务封禁异常: {}", e.getMessage(), e);
        return Result.error("当前账号服务已被封禁");
    }

    // 拦截：Http Basic 校验失败异常
    @ExceptionHandler(NotHttpBasicAuthException.class)
    public Result<?> handlerException(NotHttpBasicAuthException e) {
        log.error("Http Basic 校验失败异常: {}", e.getMessage(), e);
        return Result.error(e.getMessage());
    }

    /**
     * 处理其他所有异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return Result.error(500, "系统内部错误，请联系管理员");
    }
}

package com.ez.admin.common.core.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.ez.admin.common.core.exception.ErrorCode;
import com.ez.admin.common.core.exception.EzBusinessException;
import com.ez.admin.common.model.model.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * <p>
 * 集中处理系统中的各类异常，统一封装成 {@link R} 格式返回给前端。
 * 使用 {@link RestControllerAdvice} 注解实现对所有 Controller 层异常的拦截处理。
 * </p>
 * <p>
 * 异常处理优先级（按方法定义顺序）：
 * <ol>
 *   <li>自定义业务异常 - 最先处理</li>
 *   <li>参数校验异常</li>
 *   <li>HTTP 相关异常</li>
 *   <li>其他未知异常 - 兜底处理</li>
 * </ol>
 * </p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义业务异常
     * <p>
     * 捕获 {@link EzBusinessException} 及其子类，直接使用异常中定义的错误码和消息构建响应。
     * 此类异常通常由业务代码主动抛出，表示可预期的业务错误场景。
     * </p>
     *
     * @param ex 业务异常
     * @return 统一错误响应
     */
    @ExceptionHandler(EzBusinessException.class)
    public R<Void> handleEzBusinessException(EzBusinessException ex) {
        log.warn("业务异常: code={}, message={}", ex.getCode(), ex.getMessage());
        return R.error(ex.getCode(), ex.getMessage());
    }

    /**
     * 处理 Sa-Token 未登录异常
     * <p>
     * 捕获 {@link NotLoginException}，当用户未登录或 token 无效时触发。
     * 常见场景：
     * <ul>
     *   <li>Token 已过期</li>
     *   <li>Token 被踢出</li>
     *   <li>未提供 Token</li>
     *   <li>Token 格式错误</li>
     * </ul>
     * </p>
     *
     * @param ex 未登录异常
     * @return 统一错误响应，提示用户登录
     */
    @ExceptionHandler(NotLoginException.class)
    public R<Void> handleNotLoginException(NotLoginException ex) {
        String errorMessage = "登录已过期，请重新登录";

        // 根据异常场景提供更友好的提示
        String message = ex.getMessage();
        if (message != null) {
            if (message.contains("Token 已过期") || message.contains("token 已过期")) {
                errorMessage = "登录已过期，请重新登录";
            } else if (message.contains("Token 已被踢下线") || message.contains("token 已被踢下线")) {
                errorMessage = "账号已在其他设备登录";
            } else if (message.contains("未提供 Token") || message.contains("token 无效")) {
                errorMessage = "请先登录";
            }
        }

        log.warn("未登录异常: {}, 详细: {}", errorMessage, message);
        return R.error(ErrorCode.UNAUTHORIZED.getCode(), errorMessage);
    }

    /**
     * 处理 Sa-Token 角色验证异常
     * <p>
     * 捕获 {@link NotRoleException}，当用户角色不足时触发。
     * </p>
     *
     * @param ex 角色验证异常
     * @return 统一错误响应
     */
    @ExceptionHandler(NotRoleException.class)
    public R<Void> handleNotRoleException(NotRoleException ex) {
        log.warn("角色权限不足: 需要角色: {}", ex.getRole());
        return R.error(ErrorCode.FORBIDDEN.getCode(), "角色权限不足");
    }

    /**
     * 处理 Sa-Token 权限验证异常
     * <p>
     * 捕获 {@link NotPermissionException}，当用户权限不足时触发。
     * </p>
     *
     * @param ex 权限验证异常
     * @return 统一错误响应
     */
    @ExceptionHandler(NotPermissionException.class)
    public R<Void> handleNotPermissionException(NotPermissionException ex) {
        log.warn("操作权限不足: 需要权限: {}", ex.getPermission());
        return R.error(ErrorCode.FORBIDDEN.getCode(), "操作权限不足");
    }

    /**
     * 处理请求参数校验异常（@Valid 注解触发）
     * <p>
     * 捕获 {@link MethodArgumentNotValidException}，通常在 Controller 方法参数使用
     * {@link org.springframework.validation.annotation.Validated}
     * </p>
     *
     * @param ex 参数校验异常
     * @return 统一错误响应，包含所有字段的校验错误信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败: {}", errorMessage);
        return R.error(ErrorCode.VALIDATION_ERROR.getCode(), errorMessage);
    }

    /**
     * 处理参数绑定异常
     * <p>
     * 捕获 {@link BindException}，通常在表单数据绑定到对象时触发。
     * 提取所有字段绑定错误信息并拼接返回。
     * </p>
     *
     * @param ex 参数绑定异常
     * @return 统一错误响应，包含所有字段的绑定错误信息
     */
    @ExceptionHandler(BindException.class)
    public R<Void> handleBindException(BindException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数绑定失败: {}", errorMessage);
        return R.error(ErrorCode.VALIDATION_ERROR.getCode(), errorMessage);
    }

    /**
     * 处理请求体读取异常
     * <p>
     * 捕获 {@link HttpMessageNotReadableException}，当请求体缺失或格式错误时触发。
     * 常见场景：
     * <ul>
     *   <li>POST/PUT 请求缺少请求体</li>
     *   <li>JSON 格式错误</li>
     *   <li>请求体类型与目标类型不匹配</li>
     * </ul>
     * </p>
     *
     * @param ex HTTP 消息不可读异常
     * @return 统一错误响应，提示请求体格式错误
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public R<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String errorMessage = "请求体格式错误或缺失";

        // 解析具体错误原因，提供更友好的提示
        String message = ex.getMessage();
        if (message != null) {
            if (message.contains("Required request body is missing")) {
                errorMessage = "请求体不能为空";
            } else if (message.contains("JSON parse error")) {
                errorMessage = "JSON 格式错误，请检查语法";
            } else if (message.contains("Invalid JSON")) {
                errorMessage = "无效的 JSON 格式";
            }
        }

        log.warn("请求体读取失败: {}, 详细信息: {}", errorMessage, message);
        return R.error(ErrorCode.BAD_REQUEST.getCode(), errorMessage);
    }

    /**
     * 处理参数类型不匹配异常
     * <p>
     * 捕获 {@link MethodArgumentTypeMismatchException}，当请求参数类型无法转换为目标类型时触发。
     * 例如：传递字符串 "abc" 到 Integer 类型参数。
     * </p>
     *
     * @param ex 参数类型不匹配异常
     * @return 统一错误响应，提示参数类型错误
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public R<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String errorMessage = String.format("参数 '%s' 类型错误，期望类型: %s",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "未知");
        log.warn("参数类型不匹配: {}", errorMessage);
        return R.error(ErrorCode.INVALID_PARAMETER_FORMAT.getCode(), errorMessage);
    }

    /**
     * 处理 404 异常（接口不存在）
     * <p>
     * 捕获 {@link NoHandlerFoundException}，当请求的 URL 没有对应的 Controller 方法处理时触发。
     * 需要在配置中设置 {@code spring.mvc.throw-exception-if-no-handler-found=true} 才能生效。
     * </p>
     *
     * @param ex 404 异常
     * @return 统一错误响应，提示接口不存在
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public R<Void> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        String errorMessage = String.format("接口 [%s] 不存在", ex.getRequestURL());
        log.warn("接口不存在: {}", errorMessage);
        return R.error(ErrorCode.NOT_FOUND.getCode(), errorMessage);
    }

    /**
     * 处理非法参数异常
     * <p>
     * 捕获 {@link IllegalArgumentException}，通常由方法参数校验失败时主动抛出。
     * </p>
     *
     * @param ex 非法参数异常
     * @return 统一错误响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public R<Void> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("非法参数: {}", ex.getMessage());
        return R.error(ErrorCode.BAD_REQUEST.getCode(), ex.getMessage());
    }

    /**
     * 处理非法状态异常
     * <p>
     * 捕获 {@link IllegalStateException}，通常表示对象状态不符合操作要求。
     * </p>
     *
     * @param ex 非法状态异常
     * @return 统一错误响应
     */
    @ExceptionHandler(IllegalStateException.class)
    public R<Void> handleIllegalStateException(IllegalStateException ex) {
        log.error("非法状态: {}", ex.getMessage(), ex);
        return R.error(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), ex.getMessage());
    }

    /**
     * 处理空指针异常
     * <p>
     * 捕获 {@link NullPointerException}，属于程序 Bug，需记录完整堆栈信息。
     * </p>
     *
     * @param ex 空指针异常
     * @return 统一错误响应
     */
    @ExceptionHandler(NullPointerException.class)
    public R<Void> handleNullPointerException(NullPointerException ex) {
        log.error("空指针异常", ex);
        return R.error(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), "系统内部错误");
    }

    /**
     * 兜底处理所有未被捕获的异常
     * <p>
     * 捕获 {@link Exception}，作为最后的异常处理兜底逻辑。
     * 记录完整异常堆栈以便问题排查，向前端返回通用错误提示避免泄露敏感信息。
     * </p>
     *
     * @param ex 未知异常
     * @return 统一错误响应
     */
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception ex) {
        log.error("系统异常", ex);
        return R.error(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
    }
}

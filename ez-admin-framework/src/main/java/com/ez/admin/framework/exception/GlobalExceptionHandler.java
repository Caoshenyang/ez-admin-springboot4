package com.ez.admin.framework.exception;

import com.ez.admin.common.exception.ErrorCode;
import com.ez.admin.common.exception.EzBusinessException;
import com.ez.admin.common.model.ApiResponse;
import lombok.extern.slf4j.Slf4j;
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
 * 集中处理系统中的各类异常，统一封装成 {@link ApiResponse} 格式返回给前端。
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
    public ApiResponse<Void> handleEzBusinessException(EzBusinessException ex) {
        log.warn("业务异常: code={}, message={}", ex.getCode(), ex.getMessage());
        return ApiResponse.error(ex.getCode(), ex.getMessage());
    }

    /**
     * 处理请求参数校验异常（@Valid 注解触发）
     * <p>
     * 捕获 {@link MethodArgumentNotValidException}，通常在 Controller 方法参数使用
     * {@link org.springframework.validation.annotation.Validated} 或 {@link jakarta.validation.Valid}
     * 注解时触发。提取所有字段校验错误信息并拼接返回。
     * </p>
     *
     * @param ex 参数校验异常
     * @return 统一错误响应，包含所有字段的校验错误信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败: {}", errorMessage);
        return ApiResponse.error(ErrorCode.VALIDATION_ERROR.getCode(), errorMessage);
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
    public ApiResponse<Void> handleBindException(BindException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数绑定失败: {}", errorMessage);
        return ApiResponse.error(ErrorCode.VALIDATION_ERROR.getCode(), errorMessage);
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
    public ApiResponse<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String errorMessage = String.format("参数 '%s' 类型错误，期望类型: %s",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "未知");
        log.warn("参数类型不匹配: {}", errorMessage);
        return ApiResponse.error(ErrorCode.INVALID_PARAMETER_FORMAT.getCode(), errorMessage);
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
    public ApiResponse<Void> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        String errorMessage = String.format("接口 [%s] 不存在", ex.getRequestURL());
        log.warn("接口不存在: {}", errorMessage);
        return ApiResponse.error(ErrorCode.NOT_FOUND.getCode(), errorMessage);
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
    public ApiResponse<Void> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("非法参数: {}", ex.getMessage());
        return ApiResponse.error(ErrorCode.BAD_REQUEST.getCode(), ex.getMessage());
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
    public ApiResponse<Void> handleIllegalStateException(IllegalStateException ex) {
        log.error("非法状态: {}", ex.getMessage(), ex);
        return ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), ex.getMessage());
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
    public ApiResponse<Void> handleNullPointerException(NullPointerException ex) {
        log.error("空指针异常", ex);
        return ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), "系统内部错误");
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
    public ApiResponse<Void> handleException(Exception ex) {
        log.error("系统异常", ex);
        return ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
    }
}

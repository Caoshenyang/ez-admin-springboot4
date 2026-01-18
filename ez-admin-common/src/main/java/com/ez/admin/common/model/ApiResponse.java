package com.ez.admin.common.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.Instant;

/**
 * 统一 API 返回体
 * <p>
 * 所有 RESTful 接口的响应均使用此结构，确保前端能够以统一方式解析响应数据。
 * 包含响应码、响应消息、业务数据和时间戳四个核心字段。
 * </p>
 *
 * @param <T> 业务数据的泛型类型，支持任意可序列化的数据结构
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务状态码
     * <p>
     * 表示业务逻辑层的处理结果，与 HTTP 状态码解耦。
     * 推荐的业务状态码约定：
     * <ul>
     *   <li>200: 操作成功</li>
     *   <li>400: 请求参数校验失败</li>
     *   <li>401: 未登录或登录已过期</li>
     *   <li>403: 无权限访问该资源</li>
     *   <li>404: 业务资源不存在</li>
     *   <li>500: 业务处理异常</li>
     *   <li>其他: 可扩展自定义业务状态码</li>
     * </ul>
     * </p>
     */
    private int code;

    /**
     * 响应消息
     * <p>
     * 用于描述请求处理结果的简要说明，成功时返回成功提示，失败时返回错误原因。
     * </p>
     */
    private String message;

    /**
     * 业务数据
     * <p>
     * 实际的业务返回数据，可以是任意可序列化的对象。
     * 对于无返回数据的接口（如删除操作），此字段可为 null。
     * </p>
     */
    private T data;

    /**
     * 响应时间戳
     * <p>
     * 使用 Unix 时间戳（毫秒级），记录服务器生成响应的时间。
     * 前端可用于计算请求耗时或处理时区相关的逻辑。
     * </p>
     */
    private Long timestamp;

    /**
     * 全参构造器
     *
     * @param code    响应状态码
     * @param message 响应消息
     * @param data    业务数据
     */
    private ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = Instant.now().toEpochMilli();
    }

    /**
     * 成功响应（无数据）
     *
     * @param <T> 数据泛型类型
     * @return 包含 200 状态码的响应对象
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "操作成功", null);
    }

    /**
     * 成功响应（带数据）
     *
     * @param data 业务数据
     * @param <T>  数据泛型类型
     * @return 包含 200 状态码和指定数据的响应对象
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "操作成功", data);
    }

    /**
     * 成功响应（自定义消息和数据）
     *
     * @param message 成功提示消息
     * @param data    业务数据
     * @param <T>     数据泛型类型
     * @return 包含 200 状态码的响应对象
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    /**
     * 失败响应（默认错误码 500）
     *
     * @param message 错误提示消息
     * @param <T>     数据泛型类型
     * @return 包含 500 状态码的响应对象
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(500, message, null);
    }

    /**
     * 失败响应（自定义状态码和消息）
     *
     * @param code    响应状态码
     * @param message 错误提示消息
     * @param <T>     数据泛型类型
     * @return 包含指定状态码的响应对象
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}

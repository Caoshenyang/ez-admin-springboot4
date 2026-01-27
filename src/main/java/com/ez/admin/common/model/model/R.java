package com.ez.admin.common.model.model;

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
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务状态码
     * <p>
     * 表示业务逻辑层的处理结果，与 HTTP 状态码解耦。
     * 采用 5 位数字分段式设计（ABBCC）：
     * <ul>
     *   <li>0: 操作成功</li>
     *   <li>1xxxx: 系统级错误（参数、权限、限流等）</li>
     *   <li>2xxxx: 业务级错误（用户、角色、菜单等业务逻辑）</li>
     *   <li>3xxxx: 三方服务错误（数据库、Redis、短信平台等）</li>
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
    private R(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = Instant.now().toEpochMilli();
    }

    /**
     * 成功响应（无数据）
     *
     * @param <T> 数据泛型类型
     * @return 包含成功状态码（0）的响应对象
     */
    public static <T> R<T> success() {
        return new R<>(0, "操作成功", null);
    }

    /**
     * 成功响应（带数据）
     *
     * @param data 业务数据
     * @param <T>  数据泛型类型
     * @return 包含成功状态码（0）和指定数据的响应对象
     */
    public static <T> R<T> success(T data) {
        return new R<>(0, "操作成功", data);
    }

    /**
     * 成功响应（自定义消息）
     *
     * @param message 成功提示消息
     * @param <T>     数据泛型类型
     * @return 包含成功状态码（0）的响应对象
     */
    public static <T> R<T> success(String message) {
        return new R<>(0, message, null);
    }

    /**
     * 成功响应（自定义消息和数据）
     *
     * @param message 成功提示消息
     * @param data    业务数据
     * @param <T>     数据泛型类型
     * @return 包含成功状态码（0）的响应对象
     */
    public static <T> R<T> success(String message, T data) {
        return new R<>(0, message, data);
    }

    /**
     * 失败响应（默认错误）
     *
     * @param <T> 数据泛型类型
     * @return 包含错误码的响应对象
     */
    public static <T> R<T> error() {
        return new R<>(10500, "操作失败", null);
    }

    /**
     * 失败响应（默认错误码 10500）
     *
     * @param message 错误提示消息
     * @param <T>     数据泛型类型
     * @return 包含 10500 状态码的响应对象
     */
    public static <T> R<T> error(String message) {
        return new R<>(10500, message, null);
    }

    /**
     * 失败响应（自定义状态码和消息）
     *
     * @param code    响应状态码
     * @param message 错误提示消息
     * @param <T>     数据泛型类型
     * @return 包含指定状态码的响应对象
     */
    public static <T> R<T> error(int code, String message) {
        return new R<>(code, message, null);
    }
}

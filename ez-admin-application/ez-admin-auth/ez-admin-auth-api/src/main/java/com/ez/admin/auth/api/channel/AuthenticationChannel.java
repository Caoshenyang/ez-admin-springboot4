package com.ez.admin.auth.api.channel;

import com.ez.admin.auth.api.dto.LoginRequest;
import com.ez.admin.auth.api.enums.ChannelType;

/**
 * 认证渠道适配器接口
 * <p>
 * 策略模式：不同渠道的认证逻辑不同，通过此接口实现扩展
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-19
 */
public interface AuthenticationChannel {

    /**
     * 获取支持的渠道类型
     *
     * @return 渠道类型
     */
    ChannelType getSupportedChannel();

    /**
     * 认证并返回用户ID
     * <p>
     * 认证成功返回用户ID，认证失败抛出 AuthenticationException
     * </p>
     *
     * @param request 登录请求
     * @return 用户ID
     * @throws com.ez.admin.auth.api.exception.AuthenticationException 认证失败时抛出
     */
    String authenticate(LoginRequest request);

    /**
     * 验证请求凭证是否完整
     * <p>
     * 在认证前调用，检查必要的凭证参数是否存在
     * </p>
     *
     * @param request 登录请求
     * @return true: 凭证完整，false: 凭证不完整
     */
    boolean validateCredentials(LoginRequest request);
}

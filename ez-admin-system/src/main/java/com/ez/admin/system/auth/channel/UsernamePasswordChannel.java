package com.ez.admin.system.auth.channel;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ez.admin.system.auth.dto.LoginRequest;
import com.ez.admin.system.auth.enums.ChannelType;
import com.ez.admin.common.exception.EzBusinessException;
import com.ez.admin.domain.system.entity.User;
import com.ez.admin.domain.system.service.IUserService;
import com.ez.admin.common.framework.security.config.SecurityConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 账号密码登录渠道
 * <p>
 * 支持用户名+密码登录
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-19
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UsernamePasswordChannel implements AuthenticationChannel {

    private final IUserService userService;
    private final SecurityConfig securityConfig;

    private static final String CREDENTIAL_USERNAME = "username";
    private static final String CREDENTIAL_PASSWORD = "password";

    @Override
    public ChannelType getSupportedChannel() {
        return ChannelType.USERNAME_PASSWORD;
    }

    @Override
    public boolean validateCredentials(LoginRequest request) {
        String username = request.getCredential(CREDENTIAL_USERNAME);
        String password = request.getCredential(CREDENTIAL_PASSWORD);
        return username != null && !username.isBlank() && password != null && !password.isBlank();
    }

    @Override
    public String authenticate(LoginRequest request) {
        String username = request.getCredential(CREDENTIAL_USERNAME);
        String password = request.getCredential(CREDENTIAL_PASSWORD);

        // 查询用户
        User user = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));

        if (user == null) {
            log.warn("账号密码登录失败：用户不存在 - {}", username);
            throw new EzBusinessException("用户名或密码错误");
        }

        // 验证密码
        if (!securityConfig.passwordEncoder().matches(password, user.getPassword())) {
            log.warn("账号密码登录失败：密码错误 - {}", username);
            throw new EzBusinessException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            log.warn("账号密码登录失败：用户已被禁用 - {}", username);
            throw new EzBusinessException("账号已被禁用");
        }

        log.info("账号密码登录成功 - username: {}, userId: {}", username, user.getUserId());
        return String.valueOf(user.getUserId());
    }
}

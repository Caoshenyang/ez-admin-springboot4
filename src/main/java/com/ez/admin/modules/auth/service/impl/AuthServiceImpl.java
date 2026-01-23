package com.ez.admin.modules.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.ez.admin.common.exception.ErrorCode;
import com.ez.admin.common.exception.EzBusinessException;
import com.ez.admin.modules.auth.dto.LoginRequest;
import com.ez.admin.modules.auth.dto.LoginResponse;
import com.ez.admin.modules.auth.service.AuthService;
import com.ez.admin.modules.system.entity.SysUser;
import com.ez.admin.modules.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现类
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper userMapper;

    /**
     * 密码编码器（用于验证密码）
     * 注意：实际项目中建议注入 Bean，这里为了简化直接创建
     */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应（包含 token）
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        // 1. 根据用户名查询用户
        SysUser user = userMapper.selectByUsername(request.getUsername());

        // 2. 用户不存在
        if (user == null) {
            throw new EzBusinessException(ErrorCode.USER_PASSWORD_ERROR);
        }

        // 3. 验证用户状态
        if (user.getStatus() == 0) {
            throw new EzBusinessException(ErrorCode.USER_ACCOUNT_DISABLED);
        }

        // 4. 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new EzBusinessException(ErrorCode.USER_PASSWORD_ERROR);
        }

        // 5. 使用 Sa-Token 进行登录，生成 token
        StpUtil.login(user.getUserId());

        // 6. 构造登录响应
        LoginResponse response = new LoginResponse();
        response.setToken(StpUtil.getTokenValue());
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setAvatar(user.getAvatar());

        log.info("用户登录成功：{}", user.getUsername());

        return response;
    }

    /**
     * 用户登出
     */
    @Override
    public void logout() {
        StpUtil.logout();
        log.info("用户登出成功");
    }
}

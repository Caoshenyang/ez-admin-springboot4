package com.ez.admin.service.auth;

import cn.dev33.satoken.stp.StpUtil;
import com.ez.admin.common.core.exception.ErrorCode;
import com.ez.admin.common.core.exception.EzBusinessException;
import com.ez.admin.dto.auth.req.LoginReq;
import com.ez.admin.dto.auth.vo.LoginVO;
import com.ez.admin.modules.system.entity.SysUser;
import com.ez.admin.modules.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserMapper userMapper;

    /**
     * 密码编码器（用于验证密码）
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应（包含 token）
     */
    public LoginVO login(LoginReq request) {
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
        LoginVO response = LoginVO.builder()
                .token(StpUtil.getTokenValue())
                .userId(user.getUserId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .build();

        log.info("用户登录成功：{}", user.getUsername());

        return response;
    }

    /**
     * 用户登出
     */
    public void logout() {
        StpUtil.logout();
        log.info("用户登出成功");
    }
}

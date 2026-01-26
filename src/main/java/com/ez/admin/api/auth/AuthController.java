package com.ez.admin.api.auth;

import com.ez.admin.common.model.R;
import com.ez.admin.dto.auth.req.LoginReq;
import com.ez.admin.dto.auth.vo.LoginVO;
import com.ez.admin.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证接口", description = "用户登录、登出等认证相关接口")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "使用用户名和密码登录，成功后返回 token")
    public R<LoginVO> login(@Valid @RequestBody LoginReq request) {
        log.info("用户登录请求：{}", request.getUsername());
        LoginVO response = authService.login(request);
        return R.success("登录成功", response);
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "退出当前登录状态")
    public R<String> logout() {
        authService.logout();
        return R.success("登出成功");
    }
}

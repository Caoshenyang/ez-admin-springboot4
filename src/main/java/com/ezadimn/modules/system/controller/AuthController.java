package com.ezadimn.modules.system.controller;

import com.ezadimn.core.response.Result;
import com.ezadimn.modules.system.dto.LoginDTO;
import com.ezadimn.modules.system.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 认证授权
 * </p>
 *
 * @author 曹申阳
 * @since 2025-12-04 15:52:09
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * 登录接口
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDTO loginDTO) {
        String token = authService.login(loginDTO);
        return Result.success("登录成功", token);
    }
}

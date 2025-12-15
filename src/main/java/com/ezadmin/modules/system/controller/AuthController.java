package com.ezadmin.modules.system.controller;

import com.ezadmin.core.response.Result;
import com.ezadmin.modules.system.dto.LoginDTO;
import com.ezadmin.modules.system.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Enumeration;

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
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * 获取用户信息
     */
    @PostMapping("/user-info")
    public Result<String> getUserInfo(HttpSession session)  {
        Enumeration<String> attributeNames = session.getAttributeNames();
        return Result.success("获取用户信息成功" );
    }
}

package com.ezadmin.modules.system.controller;

import com.ezadmin.core.response.Result;
import com.ezadmin.modules.system.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户管理接口
 * </p>
 *
 * @author 曹申阳
 * @since 2025-12-05 16:09:16
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/user")
public class UserController {

    private final UserService userService;

    /**
     * 初始化用户
     */
     @PostMapping("/init")
    public Result<Void> initUser() {
        userService.initUser();
        return Result.success("初始化用户成功 用户名：admin 密码：123456 ");
    }

}

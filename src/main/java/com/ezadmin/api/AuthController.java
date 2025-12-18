package com.ezadmin.api;

import com.ezadmin.common.response.Result;
import com.ezadmin.model.dto.LoginDTO;
import com.ezadmin.model.vo.UserInfoVO;
import com.ezadmin.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 认证接口
 * </p>
 *
 * @author 曹申阳
 * @since 2025-12-16 15:23:25
 */
@Tag(name = "认证接口", description = "认证接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "初始化管理员账号", description = "初始化管理员账号,账号密码:admin/123456,请及时修改密码")
    @GetMapping("/init")
    public Result<Void> initAdmin() {
        authService.initAdmin();
        return Result.success("初始化管理员账号成功:admin/123456,请及时修改密码");
    }

    @Operation(summary = "用户登录", description = "用户登录")
    @PostMapping("/login")
    public Result<Void> login(@RequestBody LoginDTO loginDTO) {
        authService.login(loginDTO);
        return Result.success("登录成功");
    }


    @Operation(summary = "获取当前用户信息", description = "获取当前用户信息")
    @GetMapping("/user-info")
    public Result<UserInfoVO> getUserInfo() {
        UserInfoVO userInfoVO = authService.getUserInfo();
        return Result.success(userInfoVO);
    }

}

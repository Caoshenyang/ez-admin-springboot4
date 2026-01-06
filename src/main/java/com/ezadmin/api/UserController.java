package com.ezadmin.api;

import com.ezadmin.common.response.Result;
import com.ezadmin.common.response.page.PageQuery;
import com.ezadmin.common.response.page.PageVO;
import com.ezadmin.model.dto.UserCreateDTO;
import com.ezadmin.model.dto.UserUpdateDTO;
import com.ezadmin.model.query.UserQuery;
import com.ezadmin.model.vo.UserDetailVO;
import com.ezadmin.model.vo.UserListVO;
import com.ezadmin.service.UserManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户接口
 * </p>
 *
 * @author 曹申阳
 * @since 2025-12-16 17:54:43
 */
@Tag(name = "用户管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/user")
public class UserController {

    private final UserManagementService userManagementService;

    @Operation(summary = "新增用户")
    @PostMapping("/create")
    public Result<Void> createUser(@RequestBody UserCreateDTO userCreateDTO) {
        userManagementService.createUser(userCreateDTO);
        return Result.success("新增成功");
    }

    @Operation(summary = "修改用户")
    @PostMapping("/update")
    public Result<Void> updateUser(@RequestBody UserUpdateDTO userUpdateDTO) {
        userManagementService.updateUser(userUpdateDTO);
        return Result.success("修改成功");
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/delete/{userId}")
    public Result<Void> deleteUser(@PathVariable("userId") Long userId) {
        userManagementService.deleteUser(userId);
        return Result.success("删除成功");
    }

    @Operation(summary = "批量删除用户")
    @DeleteMapping("/batch-delete")
    public Result<Void> batchDeleteUsers(@RequestBody List<Long> userIds) {
        userManagementService.deleteUsers(userIds);
        return Result.success("批量删除成功");
    }


    @Operation(summary = "根据ID查询用户", description = "根据ID查询用户")
    @GetMapping("/getUserById/{userId}")
    public Result<UserDetailVO> findUserById(@PathVariable Long userId) {
        return Result.success(userManagementService.findUserById(userId));
    }

    @Operation(summary = "分页查询")
    @PostMapping("/page")
    public Result<PageVO<UserListVO>> findPage(@RequestBody PageQuery<UserQuery> userQuery) {
        PageVO<UserListVO> page = userManagementService.findPage(userQuery);
        return Result.success(page);
    }

}

package com.ez.admin.api.user;

import com.ez.admin.common.model.R;
import com.ez.admin.common.model.PageQuery;
import com.ez.admin.common.model.PageVO;
import com.ez.admin.common.permission.SaCheckPermission;
import com.ez.admin.dto.user.req.UserAssignRoleReq;
import com.ez.admin.dto.user.req.UserCreateReq;
import com.ez.admin.dto.user.req.UserUpdateReq;
import com.ez.admin.dto.user.vo.UserDetailVO;
import com.ez.admin.dto.user.vo.UserListVO;
import com.ez.admin.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户增删改查等管理操作")
public class UserController {

    private final UserService userService;

    @PostMapping
    @SaCheckPermission("system:user:create")
    @Operation(summary = "创建用户", description = "创建新用户，支持分配角色")
    public R<Void> create(@Valid @RequestBody UserCreateReq request) {
        log.info("创建用户请求，用户名：{}", request.getUsername());
        userService.createUser(request);
        return R.success("创建成功");
    }

    @PutMapping
    @SaCheckPermission("system:user:update")
    @Operation(summary = "更新用户", description = "更新用户信息，支持重新分配角色")
    public R<Void> update(@Valid @RequestBody UserUpdateReq request) {
        log.info("更新用户请求，用户ID：{}", request.getUserId());
        userService.updateUser(request);
        return R.success("更新成功");
    }

    @DeleteMapping("/{userId}")
    @SaCheckPermission("system:user:delete")
    @Operation(summary = "删除用户", description = "根据用户ID删除用户（逻辑删除）")
    public R<Void> delete(@PathVariable Long userId) {
        log.info("删除用户请求，用户ID：{}", userId);
        userService.deleteUser(userId);
        return R.success("删除成功");
    }

    @DeleteMapping("/batch")
    @SaCheckPermission("system:user:delete")
    @Operation(summary = "批量删除用户", description = "批量删除多个用户（逻辑删除）")
    public R<Void> batchDelete(@RequestBody List<Long> userIds) {
        log.info("批量删除用户请求，数量：{}", userIds.size());
        userService.deleteUsers(userIds);
        return R.success("批量删除成功");
    }

    @GetMapping("/{userId}")
    @SaCheckPermission("system:user:query")
    @Operation(summary = "查询用户详情", description = "根据用户ID查询用户完整信息，包括角色列表")
    public R<UserDetailVO> getById(@PathVariable Long userId) {
        UserDetailVO user = userService.getUserById(userId);
        return R.success(user);
    }

    @PostMapping("/page")
    @SaCheckPermission("system:user:query")
    @Operation(summary = "分页查询用户", description = "分页查询用户列表，支持多条件筛选和排序")
    public R<PageVO<UserListVO>> getPage(@RequestBody PageQuery query) {
        PageVO<UserListVO> page = userService.getUserPage(query);
        return R.success(page);
    }

    @PostMapping("/assign/roles")
    @SaCheckPermission("system:user:assign")
    @Operation(summary = "分配角色", description = "为用户分配角色（覆盖式，传入的角色ID列表将替换原有角色）")
    public R<Void> assignRoles(@Valid @RequestBody UserAssignRoleReq request) {
        log.info("用户分配角色请求，用户ID：{}，角色数量：{}", request.getUserId(), request.getRoleIds().size());
        userService.assignRoles(request.getUserId(), request.getRoleIds());
        return R.success("分配成功");
    }

    @GetMapping("/{userId}/roles")
    @SaCheckPermission("system:user:query")
    @Operation(summary = "获取用户角色", description = "获取用户已分配的角色ID列表")
    public R<List<Long>> getRoles(@PathVariable Long userId) {
        List<Long> roleIds = userService.getUserRoleIds(userId);
        return R.success(roleIds);
    }
}

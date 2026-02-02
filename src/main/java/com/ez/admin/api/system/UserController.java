package com.ez.admin.api.system;

import com.ez.admin.common.model.annotation.OperationLog;
import com.ez.admin.common.model.model.R;
import com.ez.admin.common.model.model.PageQuery;
import com.ez.admin.common.model.model.PageVO;
import com.ez.admin.dto.file.vo.FileUploadVO;
import com.ez.admin.dto.system.user.req.UserAssignRoleReq;
import com.ez.admin.dto.system.user.req.UserCreateReq;
import com.ez.admin.dto.system.user.req.UserPasswordChangeReq;
import com.ez.admin.dto.system.user.req.UserProfileUpdateReq;
import com.ez.admin.dto.system.user.req.UserStatusChangeReq;
import com.ez.admin.dto.system.user.req.UserUpdateReq;
import com.ez.admin.dto.system.user.vo.UserDetailVO;
import com.ez.admin.dto.system.user.vo.UserListVO;
import com.ez.admin.service.file.FileService;
import com.ez.admin.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import cn.dev33.satoken.stp.StpUtil;

import java.util.List;

/**
 * 用户管理控制器
 * <p>
 * 权限说明：本控制器的权限通过路由拦截式鉴权实现，无需使用 @SaCheckPermission 注解
 * </p>
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

    private final FileService fileService;

    @PostMapping
    @OperationLog(module = "用户管理", operation = "创建", description = "创建用户")
    @Operation(summary = "创建用户", description = "创建新用户，支持分配角色")
    public R<Void> create(@Valid @RequestBody UserCreateReq request) {
        log.info("创建用户请求，用户名：{}", request.getUsername());
        userService.createUser(request);
        return R.success("创建成功");
    }

    @PutMapping
    @OperationLog(module = "用户管理", operation = "更新", description = "更新用户信息")
    @Operation(summary = "更新用户", description = "更新用户信息，支持重新分配角色")
    public R<Void> update(@Valid @RequestBody UserUpdateReq request) {
        log.info("更新用户请求，用户ID：{}", request.getUserId());
        userService.updateUser(request);
        return R.success("更新成功");
    }

    @DeleteMapping("/{userId}")
    @OperationLog(module = "用户管理", operation = "删除", description = "删除用户")
    @Operation(summary = "删除用户", description = "根据用户ID删除用户（逻辑删除）")
    public R<Void> delete(@PathVariable Long userId) {
        log.info("删除用户请求，用户ID：{}", userId);
        userService.deleteUser(userId);
        return R.success("删除成功");
    }

    @DeleteMapping("/batch")
    @OperationLog(module = "用户管理", operation = "批量删除", description = "批量删除用户")
    @Operation(summary = "批量删除用户", description = "批量删除多个用户（逻辑删除）")
    public R<Void> batchDelete(@RequestBody List<Long> userIds) {
        log.info("批量删除用户请求，数量：{}", userIds.size());
        userService.deleteUsers(userIds);
        return R.success("批量删除成功");
    }

    @GetMapping("/{userId}")
    @Operation(summary = "查询用户详情", description = "根据用户ID查询用户完整信息，包括角色列表")
    public R<UserDetailVO> getById(@PathVariable Long userId) {
        UserDetailVO user = userService.getUserById(userId);
        return R.success(user);
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询用户", description = "分页查询用户列表，支持多条件筛选和排序")
    public R<PageVO<UserListVO>> getPage(@RequestBody PageQuery query) {
        PageVO<UserListVO> page = userService.getUserPage(query);
        return R.success(page);
    }

    @PostMapping("/assign/roles")
    @Operation(summary = "分配角色", description = "为用户分配角色（覆盖式，传入的角色ID列表将替换原有角色）")
    public R<Void> assignRoles(@Valid @RequestBody UserAssignRoleReq request) {
        log.info("用户分配角色请求，用户ID：{}，角色数量：{}", request.getUserId(), request.getRoleIds().size());
        userService.assignRoles(request.getUserId(), request.getRoleIds());
        return R.success("分配成功");
    }

    @GetMapping("/{userId}/roles")
    @Operation(summary = "获取用户角色", description = "获取用户已分配的角色ID列表")
    public R<List<Long>> getRoles(@PathVariable Long userId) {
        List<Long> roleIds = userService.getUserRoleIds(userId);
        return R.success(roleIds);
    }

    @PostMapping("/{userId}/avatar")
    @OperationLog(module = "用户管理", operation = "上传头像", description = "上传用户头像")
    @Operation(summary = "上传用户头像", description = "为指定用户上传头像")
    public R<String> uploadAvatar(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        log.info("上传用户头像请求，用户ID：{}", userId);
        FileUploadVO uploadResult = fileService.uploadImage(file);
        userService.uploadAvatar(userId, uploadResult.getUrl());
        return R.success("头像上传成功", uploadResult.getUrl());
    }

    @PostMapping("/avatar")
    @OperationLog(module = "用户管理", operation = "上传头像", description = "上传当前用户头像")
    @Operation(summary = "上传当前用户头像", description = "为当前登录用户上传头像")
    public R<String> uploadCurrentUserAvatar(@RequestParam("file") MultipartFile file) {
        Long userId = StpUtil.getLoginIdAsLong();
        log.info("上传当前用户头像请求，用户ID：{}", userId);
        FileUploadVO uploadResult = fileService.uploadImage(file);
        userService.uploadAvatar(userId, uploadResult.getUrl());
        return R.success("头像上传成功", uploadResult.getUrl());
    }

    @PutMapping("/profile")
    @OperationLog(module = "用户管理", operation = "修改个人信息", description = "修改当前用户个人信息")
    @Operation(summary = "修改个人信息", description = "修改当前登录用户的个人信息")
    public R<Void> updateProfile(@Valid @RequestBody UserProfileUpdateReq request) {
        Long userId = StpUtil.getLoginIdAsLong();
        log.info("修改个人信息请求，用户ID：{}", userId);
        userService.updateProfile(userId, request);
        return R.success("个人信息修改成功");
    }

    @PutMapping("/password")
    @OperationLog(module = "用户管理", operation = "修改密码", description = "修改当前用户密码")
    @Operation(summary = "修改密码", description = "修改当前登录用户的密码")
    public R<Void> changePassword(@Valid @RequestBody UserPasswordChangeReq request) {
        Long userId = StpUtil.getLoginIdAsLong();
        log.info("修改密码请求，用户ID：{}", userId);
        userService.changePassword(userId, request);
        return R.success("密码修改成功，请重新登录");
    }

    @PutMapping("/status")
    @OperationLog(module = "用户管理", operation = "切换状态", description = "切换用户状态")
    @Operation(summary = "切换用户状态", description = "切换指定用户的状态（启用/禁用）")
    public R<Void> changeStatus(@Valid @RequestBody UserStatusChangeReq request) {
        log.info("切换用户状态请求，用户ID：{}，状态：{}", request.getUserId(), request.getStatus());
        userService.changeStatus(request);
        return R.success("状态切换成功");
    }
}

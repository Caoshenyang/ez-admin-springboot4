package com.ez.admin.api.user;

import com.ez.admin.common.model.R;
import com.ez.admin.common.model.PageQuery;
import com.ez.admin.common.model.PageVO;
import com.ez.admin.dto.user.req.UserCreateReq;
import com.ez.admin.dto.user.req.UserQueryReq;
import com.ez.admin.dto.user.req.UserUpdateReq;
import com.ez.admin.dto.user.vo.PageVO;
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

    /**
     * 创建用户
     *
     * @param request 创建请求
     * @return 成功响应
     */
    @PostMapping
    @Operation(summary = "创建用户", description = "创建新用户，支持分配角色")
    public R<Void> create(@Valid @RequestBody UserCreateReq request) {
        log.info("创建用户请求，用户名：{}", request.getUsername());
        userService.createUser(request);
        return R.success("创建成功");
    }

    /**
     * 更新用户
     *
     * @param request 更新请求
     * @return 成功响应
     */
    @PutMapping
    @Operation(summary = "更新用户", description = "更新用户信息，支持重新分配角色")
    public R<Void> update(@Valid @RequestBody UserUpdateReq request) {
        log.info("更新用户请求，用户ID：{}", request.getUserId());
        userService.updateUser(request);
        return R.success("更新成功");
    }

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 成功响应
     */
    @DeleteMapping("/{userId}")
    @Operation(summary = "删除用户", description = "根据用户ID删除用户（逻辑删除）")
    public R<Void> delete(@PathVariable Long userId) {
        log.info("删除用户请求，用户ID：{}", userId);
        userService.deleteUser(userId);
        return R.success("删除成功");
    }

    /**
     * 批量删除用户
     *
     * @param userIds 用户ID列表
     * @return 成功响应
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除用户", description = "批量删除多个用户（逻辑删除）")
    public R<Void> batchDelete(@RequestBody List<Long> userIds) {
        log.info("批量删除用户请求，数量：{}", userIds.size());
        userService.deleteUsers(userIds);
        return R.success("批量删除成功");
    }

    /**
     * 根据ID查询用户详情
     *
     * @param userId 用户ID
     * @return 用户详情
     */
    @GetMapping("/{userId}")
    @Operation(summary = "查询用户详情", description = "根据用户ID查询用户完整信息，包括角色列表")
    public R<UserDetailVO> getById(@PathVariable Long userId) {
        UserDetailVO user = userService.getUserById(userId);
        return R.success(user);
    }

    /**
     * 分页查询用户列表
     *
     * @param query 分页查询请求
     * @return 分页结果
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询用户", description = "分页查询用户列表，支持多条件筛选和排序")
    public R<PageVO<UserListVO>> getPage(@RequestBody PageQuery<UserQueryReq> query) {
        PageVO<UserListVO> page = userService.getUserPage(query);
        return R.success(page);
    }
}

package com.ez.admin.api.role;

import com.ez.admin.common.model.PageQuery;
import com.ez.admin.common.model.PageVO;
import com.ez.admin.common.model.R;
import com.ez.admin.dto.role.req.RoleCreateReq;
import com.ez.admin.dto.role.req.RoleUpdateReq;
import com.ez.admin.dto.role.vo.RoleDetailVO;
import com.ez.admin.dto.role.vo.RoleListVO;
import com.ez.admin.service.role.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Slf4j
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
@Tag(name = "角色管理", description = "角色增删改查等管理操作")
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @Operation(summary = "创建角色", description = "创建新角色，支持分配菜单和数据权限")
    public R<Void> create(@Valid @RequestBody RoleCreateReq request) {
        log.info("创建角色请求，角色名称：{}", request.getRoleName());
        roleService.createRole(request);
        return R.success("创建成功");
    }

    @PutMapping
    @Operation(summary = "更新角色", description = "更新角色信息，支持重新分配菜单和数据权限")
    public R<Void> update(@Valid @RequestBody RoleUpdateReq request) {
        log.info("更新角色请求，角色ID：{}", request.getRoleId());
        roleService.updateRole(request);
        return R.success("更新成功");
    }

    @DeleteMapping("/{roleId}")
    @Operation(summary = "删除角色", description = "根据角色ID删除角色（逻辑删除）")
    public R<Void> delete(@PathVariable Long roleId) {
        log.info("删除角色请求，角色ID：{}", roleId);
        roleService.deleteRole(roleId);
        return R.success("删除成功");
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除角色", description = "批量删除多个角色（逻辑删除）")
    public R<Void> batchDelete(@RequestBody List<Long> roleIds) {
        log.info("批量删除角色请求，数量：{}", roleIds.size());
        roleService.deleteRoles(roleIds);
        return R.success("批量删除成功");
    }

    @GetMapping("/{roleId}")
    @Operation(summary = "查询角色详情", description = "根据角色ID查询角色完整信息，包括菜单和部门权限")
    public R<RoleDetailVO> getById(@PathVariable Long roleId) {
        RoleDetailVO role = roleService.getRoleById(roleId);
        return R.success(role);
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询角色", description = "分页查询角色列表，支持多条件筛选和排序")
    public R<PageVO<RoleListVO>> getPage(@RequestBody PageQuery query) {
        PageVO<RoleListVO> page = roleService.getRolePage(query);
        return R.success(page);
    }

    @GetMapping("/list")
    @Operation(summary = "查询所有角色", description = "查询所有角色列表（不分页）")
    public R<List<RoleListVO>> getList() {
        List<RoleListVO> list = roleService.getRoleList();
        return R.success(list);
    }
}

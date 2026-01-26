package com.ez.admin.api.role;

import com.ez.admin.common.annotation.OperationLog;
import com.ez.admin.common.model.PageQuery;
import com.ez.admin.common.model.PageVO;
import com.ez.admin.common.model.R;
import com.ez.admin.dto.role.req.RoleAssignDeptReq;
import com.ez.admin.dto.role.req.RoleAssignMenuReq;
import com.ez.admin.dto.role.req.RoleCreateReq;
import com.ez.admin.dto.role.req.RoleUpdateReq;
import com.ez.admin.dto.role.vo.RoleDetailVO;
import com.ez.admin.dto.role.vo.RoleListVO;
import com.ez.admin.common.permission.SaCheckPermission;
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
    @SaCheckPermission("system:role:create")
    @OperationLog(module = "角色管理", operation = "创建", description = "创建角色")
    @Operation(summary = "创建角色", description = "创建新角色，支持分配菜单和数据权限")
    public R<Void> create(@Valid @RequestBody RoleCreateReq request) {
        log.info("创建角色请求，角色名称：{}", request.getRoleName());
        roleService.createRole(request);
        return R.success("创建成功");
    }

    @PutMapping
    @SaCheckPermission("system:role:update")
    @OperationLog(module = "角色管理", operation = "更新", description = "更新角色信息")
    @Operation(summary = "更新角色", description = "更新角色信息，支持重新分配菜单和数据权限")
    public R<Void> update(@Valid @RequestBody RoleUpdateReq request) {
        log.info("更新角色请求，角色ID：{}", request.getRoleId());
        roleService.updateRole(request);
        return R.success("更新成功");
    }

    @DeleteMapping("/{roleId}")
    @SaCheckPermission("system:role:delete")
    @OperationLog(module = "角色管理", operation = "删除", description = "删除角色")
    @Operation(summary = "删除角色", description = "根据角色ID删除角色（逻辑删除）")
    public R<Void> delete(@PathVariable Long roleId) {
        log.info("删除角色请求，角色ID：{}", roleId);
        roleService.deleteRole(roleId);
        return R.success("删除成功");
    }

    @DeleteMapping("/batch")
    @SaCheckPermission("system:role:delete")
    @Operation(summary = "批量删除角色", description = "批量删除多个角色（逻辑删除）")
    public R<Void> batchDelete(@RequestBody List<Long> roleIds) {
        log.info("批量删除角色请求，数量：{}", roleIds.size());
        roleService.deleteRoles(roleIds);
        return R.success("批量删除成功");
    }

    @GetMapping("/{roleId}")
    @SaCheckPermission("system:role:query")
    @Operation(summary = "查询角色详情", description = "根据角色ID查询角色完整信息，包括菜单和部门权限")
    public R<RoleDetailVO> getById(@PathVariable Long roleId) {
        RoleDetailVO role = roleService.getRoleById(roleId);
        return R.success(role);
    }

    @PostMapping("/page")
    @SaCheckPermission("system:role:query")
    @Operation(summary = "分页查询角色", description = "分页查询角色列表，支持多条件筛选和排序")
    public R<PageVO<RoleListVO>> getPage(@RequestBody PageQuery query) {
        PageVO<RoleListVO> page = roleService.getRolePage(query);
        return R.success(page);
    }

    @GetMapping("/list")
    @SaCheckPermission("system:role:query")
    @Operation(summary = "查询所有角色", description = "查询所有角色列表（不分页）")
    public R<List<RoleListVO>> getList() {
        List<RoleListVO> list = roleService.getRoleList();
        return R.success(list);
    }

    @PostMapping("/assign/menus")
    @SaCheckPermission("system:role:assign")
    @Operation(summary = "分配菜单", description = "为角色分配菜单权限（覆盖式，传入的菜单ID列表将替换原有权限）")
    public R<Void> assignMenus(@Valid @RequestBody RoleAssignMenuReq request) {
        log.info("角色分配菜单请求，角色ID：{}，菜单数量：{}", request.getRoleId(), request.getMenuIds().size());
        roleService.assignMenus(request.getRoleId(), request.getMenuIds());
        return R.success("分配成功");
    }

    @GetMapping("/{roleId}/menus")
    @SaCheckPermission("system:role:query")
    @Operation(summary = "获取角色菜单", description = "获取角色已分配的菜单ID列表")
    public R<List<Long>> getMenus(@PathVariable Long roleId) {
        List<Long> menuIds = roleService.getRoleMenuIds(roleId);
        return R.success(menuIds);
    }

    @PostMapping("/assign/depts")
    @SaCheckPermission("system:role:assign")
    @Operation(summary = "分配部门", description = "为角色分配数据权限（覆盖式，传入的部门ID列表将替换原有权限）")
    public R<Void> assignDepts(@Valid @RequestBody RoleAssignDeptReq request) {
        log.info("角色分配部门请求，角色ID：{}，部门数量：{}", request.getRoleId(), request.getDeptIds().size());
        roleService.assignDepts(request.getRoleId(), request.getDeptIds());
        return R.success("分配成功");
    }

    @GetMapping("/{roleId}/depts")
    @SaCheckPermission("system:role:query")
    @Operation(summary = "获取角色部门", description = "获取角色已分配的部门ID列表")
    public R<List<Long>> getDepts(@PathVariable Long roleId) {
        List<Long> deptIds = roleService.getRoleDeptIds(roleId);
        return R.success(deptIds);
    }
}

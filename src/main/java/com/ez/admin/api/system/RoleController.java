package com.ez.admin.api.system;

import com.ez.admin.common.model.annotation.OperationLog;
import com.ez.admin.common.model.model.PageQuery;
import com.ez.admin.common.model.model.PageVO;
import com.ez.admin.common.model.model.R;
import com.ez.admin.dto.system.role.req.RoleAssignDeptReq;
import com.ez.admin.dto.system.role.req.RoleAssignMenuReq;
import com.ez.admin.dto.system.role.req.RoleBatchAssignMenuReq;
import com.ez.admin.dto.system.role.req.RoleCreateReq;
import com.ez.admin.dto.system.role.req.RoleStatusChangeReq;
import com.ez.admin.dto.system.role.req.RoleUpdateReq;
import com.ez.admin.dto.system.role.vo.RoleDetailVO;
import com.ez.admin.dto.system.role.vo.RoleListVO;
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
 * <p>
 * 权限说明：本控制器的权限通过路由拦截式鉴权实现，无需使用 @SaCheckPermission 注解
 * </p>
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
    @OperationLog(module = "角色管理", operation = "创建", description = "创建角色")
    @Operation(summary = "创建角色", description = "创建新角色，支持分配菜单和数据权限")
    public R<Void> create(@Valid @RequestBody RoleCreateReq request) {
        log.info("创建角色请求，角色名称：{}", request.getRoleName());
        roleService.createRole(request);
        return R.success("创建成功");
    }

    @PutMapping
    @OperationLog(module = "角色管理", operation = "更新", description = "更新角色信息")
    @Operation(summary = "更新角色", description = "更新角色信息，支持重新分配菜单和数据权限")
    public R<Void> update(@Valid @RequestBody RoleUpdateReq request) {
        log.info("更新角色请求，角色ID：{}", request.getRoleId());
        roleService.updateRole(request);
        return R.success("更新成功");
    }

    @DeleteMapping("/{roleId}")
    @OperationLog(module = "角色管理", operation = "删除", description = "删除角色")
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

    @PostMapping("/assign/menus")
    @Operation(summary = "分配菜单", description = "为角色分配菜单权限（覆盖式，传入的菜单ID列表将替换原有权限）")
    public R<Void> assignMenus(@Valid @RequestBody RoleAssignMenuReq request) {
        log.info("角色分配菜单请求，角色ID：{}，菜单数量：{}", request.getRoleId(), request.getMenuIds().size());
        roleService.assignMenus(request.getRoleId(), request.getMenuIds());
        return R.success("分配成功");
    }

    @GetMapping("/{roleId}/menus")
    @Operation(summary = "获取角色菜单", description = "获取角色已分配的菜单ID列表")
    public R<List<Long>> getMenus(@PathVariable Long roleId) {
        List<Long> menuIds = roleService.getRoleMenuIds(roleId);
        return R.success(menuIds);
    }

    @PostMapping("/assign/depts")
    @Operation(summary = "分配部门", description = "为角色分配数据权限（覆盖式，传入的部门ID列表将替换原有权限）")
    public R<Void> assignDepts(@Valid @RequestBody RoleAssignDeptReq request) {
        log.info("角色分配部门请求，角色ID：{}，部门数量：{}", request.getRoleId(), request.getDeptIds().size());
        roleService.assignDepts(request.getRoleId(), request.getDeptIds());
        return R.success("分配成功");
    }

    @GetMapping("/{roleId}/depts")
    @Operation(summary = "获取角色部门", description = "获取角色已分配的部门ID列表")
    public R<List<Long>> getDepts(@PathVariable Long roleId) {
        List<Long> deptIds = roleService.getRoleDeptIds(roleId);
        return R.success(deptIds);
    }

    @PutMapping("/status")
    @OperationLog(module = "角色管理", operation = "切换状态", description = "切换角色状态")
    @Operation(summary = "切换角色状态", description = "切换指定角色的状态（启用/禁用）")
    public R<Void> changeStatus(@Valid @RequestBody RoleStatusChangeReq request) {
        log.info("切换角色状态请求，角色ID：{}，状态：{}", request.getRoleId(), request.getStatus());
        roleService.changeStatus(request);
        return R.success("状态切换成功");
    }

    @PostMapping("/batch/assign/menus")
    @OperationLog(module = "角色管理", operation = "批量分配菜单", description = "批量分配菜单")
    @Operation(summary = "批量分配菜单", description = "为多个角色批量分配相同的菜单权限")
    public R<Void> batchAssignMenus(@Valid @RequestBody RoleBatchAssignMenuReq request) {
        log.info("批量分配菜单请求，角色数量：{}，菜单数量：{}", request.getRoleIds().size(), request.getMenuIds().size());
        roleService.batchAssignMenus(request.getRoleIds(), request.getMenuIds());
        return R.success("批量分配成功");
    }
}

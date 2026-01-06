package com.ezadmin.api;

import com.ezadmin.common.response.Result;
import com.ezadmin.common.response.page.PageQuery;
import com.ezadmin.common.response.page.PageVO;
import com.ezadmin.model.dto.RoleCreateDTO;
import com.ezadmin.model.dto.RoleUpdateDTO;
import com.ezadmin.model.query.RoleQuery;
import com.ezadmin.model.vo.RoleDetailVO;
import com.ezadmin.modules.system.entity.Role;
import com.ezadmin.service.RoleManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理接口
 */
@Tag(name = "角色管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/role")
public class RoleController {

    private final RoleManagementService roleManagementService;

    @Operation(summary = "角色分页查询")
    @PostMapping("/page")
    public Result<PageVO<Role>> page(@RequestBody PageQuery<RoleQuery> query) {
        return Result.success(roleManagementService.page(query));
    }

    @Operation(summary = "角色详情")
    @GetMapping("/{roleId}")
    public Result<RoleDetailVO> detail(@PathVariable Long roleId) {
        return Result.success(roleManagementService.detail(roleId));
    }

    @Operation(summary = "新增角色")
    @PostMapping("/create")
    public Result<Void> create(@RequestBody RoleCreateDTO dto) {
        roleManagementService.create(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "更新角色")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody RoleUpdateDTO dto) {
        roleManagementService.update(dto);
        return Result.success("更新成功");
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{roleId}")
    public Result<Void> delete(@PathVariable Long roleId) {
        roleManagementService.delete(roleId);
        return Result.success("删除成功");
    }

    @Operation(summary = "全部角色列表")
    @GetMapping("/list")
    public Result<List<Role>> listAll() {
        return Result.success(roleManagementService.listAll());
    }
}

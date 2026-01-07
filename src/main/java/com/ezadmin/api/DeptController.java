package com.ezadmin.api;

import com.ezadmin.common.response.Result;
import com.ezadmin.model.dto.DeptCreateDTO;
import com.ezadmin.model.dto.DeptParentTreeDTO;
import com.ezadmin.model.dto.DeptUpdateDTO;
import com.ezadmin.model.query.DeptQuery;
import com.ezadmin.model.vo.DeptTreeVO;
import com.ezadmin.modules.system.entity.Dept;
import com.ezadmin.service.DeptManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理接口
 */
@Tag(name = "部门管理")
@RestController
@RequestMapping("/system/dept")
@RequiredArgsConstructor
public class DeptController {

    private final DeptManagementService deptManagementService;

    @Operation(summary = "部门树查询")
    @PostMapping("/tree")
    public Result<List<DeptTreeVO>> tree(@RequestBody(required = false) DeptQuery query) {
        return Result.success(deptManagementService.tree(query));
    }

    @Operation(summary = "获取父节点树形结构（用于表单上级部门选择）")
    @PostMapping("/parent-tree")
    public Result<List<DeptTreeVO>> parentTree(@RequestBody(required = false) DeptParentTreeDTO dto) {
        return Result.success(deptManagementService.parentTree(dto));
    }

    @Operation(summary = "部门详情")
    @GetMapping("/{deptId}")
    public Result<Dept> detail(@PathVariable Long deptId) {
        return Result.success(deptManagementService.detail(deptId));
    }

    @Operation(summary = "新增部门")
    @PostMapping("/create")
    public Result<Void> create(@RequestBody DeptCreateDTO dto) {
        deptManagementService.create(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "更新部门")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody DeptUpdateDTO dto) {
        deptManagementService.update(dto);
        return Result.success("更新成功");
    }

    @Operation(summary = "删除部门")
    @DeleteMapping("/{deptId}")
    public Result<Void> delete(@PathVariable Long deptId) {
        deptManagementService.delete(deptId);
        return Result.success("删除成功");
    }
}

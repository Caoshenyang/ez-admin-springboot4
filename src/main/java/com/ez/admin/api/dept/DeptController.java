package com.ez.admin.api.dept;

import com.ez.admin.common.model.R;
import com.ez.admin.dto.dept.req.DeptCreateReq;
import com.ez.admin.dto.dept.req.DeptUpdateReq;
import com.ez.admin.dto.dept.vo.DeptVO;
import com.ez.admin.service.dept.DeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理控制器
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Slf4j
@RestController
@RequestMapping("/dept")
@RequiredArgsConstructor
@Tag(name = "部门管理", description = "部门增删改查等管理操作")
public class DeptController {

    private final DeptService deptService;

    /**
     * 创建部门
     *
     * @param request 创建请求
     * @return 成功响应
     */
    @PostMapping
    @Operation(summary = "创建部门", description = "创建新部门")
    public R<Void> create(@Valid @RequestBody DeptCreateReq request) {
        log.info("创建部门请求，部门名称：{}", request.getDeptName());
        deptService.createDept(request);
        return R.success("创建成功");
    }

    /**
     * 更新部门
     *
     * @param request 更新请求
     * @return 成功响应
     */
    @PutMapping
    @Operation(summary = "更新部门", description = "更新部门信息")
    public R<Void> update(@Valid @RequestBody DeptUpdateReq request) {
        log.info("更新部门请求，部门ID：{}", request.getDeptId());
        deptService.updateDept(request);
        return R.success("更新成功");
    }

    /**
     * 删除部门
     *
     * @param deptId 部门ID
     * @return 成功响应
     */
    @DeleteMapping("/{deptId}")
    @Operation(summary = "删除部门", description = "根据部门ID删除部门（逻辑删除）")
    public R<Void> delete(@PathVariable Long deptId) {
        log.info("删除部门请求，部门ID：{}", deptId);
        deptService.deleteDept(deptId);
        return R.success("删除成功");
    }

    /**
     * 根据ID查询部门详情
     *
     * @param deptId 部门ID
     * @return 部门详情
     */
    @GetMapping("/{deptId}")
    @Operation(summary = "查询部门详情", description = "根据部门ID查询部门完整信息")
    public R<DeptVO> getById(@PathVariable Long deptId) {
        DeptVO dept = deptService.getDeptById(deptId);
        return R.success(dept);
    }

    /**
     * 查询部门树
     *
     * @return 部门树（完整的树形结构）
     */
    @GetMapping("/tree")
    @Operation(summary = "查询部门树", description = "查询完整的部门树形结构（不分页）")
    public R<List<DeptVO>> getTree() {
        List<DeptVO> tree = deptService.getDeptTree();
        return R.success(tree);
    }
}

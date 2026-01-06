package com.ezadmin.api;

import com.ezadmin.common.response.Result;
import com.ezadmin.model.dto.MenuCreateDTO;
import com.ezadmin.model.dto.MenuUpdateDTO;
import com.ezadmin.model.query.MenuQuery;
import com.ezadmin.model.vo.MenuTreeVO;
import com.ezadmin.modules.system.entity.Menu;
import com.ezadmin.service.MenuManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理接口
 */
@Tag(name = "菜单管理")
@RestController
@RequestMapping("/system/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuManagementService menuManagementService;

    @Operation(summary = "菜单树查询")
    @PostMapping("/tree")
    public Result<List<MenuTreeVO>> tree(@RequestBody(required = false) MenuQuery query) {
        return Result.success(menuManagementService.tree(query));
    }

    @Operation(summary = "菜单详情")
    @GetMapping("/{menuId}")
    public Result<Menu> detail(@PathVariable Long menuId) {
        return Result.success(menuManagementService.detail(menuId));
    }

    @Operation(summary = "新增菜单")
    @PostMapping("/create")
    public Result<Void> create(@RequestBody MenuCreateDTO dto) {
        menuManagementService.create(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "更新菜单")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody MenuUpdateDTO dto) {
        menuManagementService.update(dto);
        return Result.success("更新成功");
    }

    @Operation(summary = "删除菜单")
    @DeleteMapping("/{menuId}")
    public Result<Void> delete(@PathVariable Long menuId) {
        menuManagementService.delete(menuId);
        return Result.success("删除成功");
    }

    @Operation(summary = "全部菜单列表")
    @GetMapping("/list")
    public Result<List<Menu>> listAll() {
        return Result.success(menuManagementService.listAll());
    }
}

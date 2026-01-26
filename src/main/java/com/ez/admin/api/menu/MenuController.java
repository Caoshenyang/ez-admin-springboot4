package com.ez.admin.api.menu;

import com.ez.admin.common.model.R;
import com.ez.admin.dto.menu.req.MenuCreateReq;
import com.ez.admin.dto.menu.req.MenuUpdateReq;
import com.ez.admin.dto.menu.vo.MenuDetailVO;
import com.ez.admin.dto.menu.vo.MenuTreeVO;
import com.ez.admin.service.menu.MenuService;
import com.ez.admin.common.permission.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Slf4j
@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
@Tag(name = "菜单管理", description = "菜单增删改查等管理操作")
public class MenuController {

    private final MenuService menuService;

    @PostMapping
    @SaCheckPermission("system:menu:create")
    @Operation(summary = "创建菜单", description = "创建新菜单（目录、菜单或按钮）")
    public R<Void> create(@Valid @RequestBody MenuCreateReq request) {
        log.info("创建菜单请求，菜单名称：{}", request.getMenuName());
        menuService.createMenu(request);
        return R.success("创建成功");
    }

    @PutMapping
    @SaCheckPermission("system:menu:update")
    @Operation(summary = "更新菜单", description = "更新菜单信息")
    public R<Void> update(@Valid @RequestBody MenuUpdateReq request) {
        log.info("更新菜单请求，菜单ID：{}", request.getMenuId());
        menuService.updateMenu(request);
        return R.success("更新成功");
    }

    @DeleteMapping("/{menuId}")
    @SaCheckPermission("system:menu:delete")
    @Operation(summary = "删除菜单", description = "根据菜单ID删除菜单（逻辑删除）")
    public R<Void> delete(@PathVariable Long menuId) {
        log.info("删除菜单请求，菜单ID：{}", menuId);
        menuService.deleteMenu(menuId);
        return R.success("删除成功");
    }

    @GetMapping("/{menuId}")
    @SaCheckPermission("system:menu:query")
    @Operation(summary = "查询菜单详情", description = "根据菜单ID查询菜单完整信息")
    public R<MenuDetailVO> getById(@PathVariable Long menuId) {
        MenuDetailVO menu = menuService.getMenuById(menuId);
        return R.success(menu);
    }

    @GetMapping("/tree")
    @SaCheckPermission("system:menu:query")
    @Operation(summary = "查询菜单树", description = "查询完整的菜单树形结构（不分页）")
    public R<List<MenuTreeVO>> getTree() {
        List<MenuTreeVO> tree = menuService.getMenuTree();
        return R.success(tree);
    }
}

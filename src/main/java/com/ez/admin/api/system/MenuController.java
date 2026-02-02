package com.ez.admin.api.system;

import com.ez.admin.common.model.annotation.OperationLog;
import com.ez.admin.common.model.model.R;
import com.ez.admin.dto.system.menu.req.MenuCreateReq;
import com.ez.admin.dto.system.menu.req.MenuUpdateReq;
import com.ez.admin.dto.system.menu.vo.MenuDetailVO;
import com.ez.admin.dto.system.menu.vo.MenuTreeVO;
import com.ez.admin.dto.system.menu.vo.RoutePermissionVO;
import com.ez.admin.service.system.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 * <p>
 * 权限说明：本控制器的权限通过路由拦截式鉴权实现，无需使用 @SaCheckPermission 注解
 * </p>
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
    @OperationLog(module = "菜单管理", operation = "创建", description = "创建菜单")
    @Operation(summary = "创建菜单", description = "创建新菜单（目录、菜单或按钮）")
    public R<Void> create(@Valid @RequestBody MenuCreateReq request) {
        log.info("创建菜单请求，菜单名称：{}", request.getMenuName());
        menuService.createMenu(request);
        return R.success("创建成功");
    }

    @PutMapping
    @OperationLog(module = "菜单管理", operation = "更新", description = "更新菜单")
    @Operation(summary = "更新菜单", description = "更新菜单信息")
    public R<Void> update(@Valid @RequestBody MenuUpdateReq request) {
        log.info("更新菜单请求，菜单ID：{}", request.getMenuId());
        menuService.updateMenu(request);
        return R.success("更新成功");
    }

    @DeleteMapping("/{menuId}")
    @OperationLog(module = "菜单管理", operation = "删除", description = "删除菜单")
    @Operation(summary = "删除菜单", description = "根据菜单ID删除菜单（逻辑删除）")
    public R<Void> delete(@PathVariable Long menuId) {
        log.info("删除菜单请求，菜单ID：{}", menuId);
        menuService.deleteMenu(menuId);
        return R.success("删除成功");
    }

    @GetMapping("/{menuId}")
    @Operation(summary = "查询菜单详情", description = "根据菜单ID查询菜单完整信息")
    public R<MenuDetailVO> getById(@PathVariable Long menuId) {
        MenuDetailVO menu = menuService.getMenuById(menuId);
        return R.success(menu);
    }

    @GetMapping("/tree")
    @Operation(summary = "查询菜单树", description = "查询完整的菜单树形结构（不分页）")
    public R<List<MenuTreeVO>> getTree() {
        List<MenuTreeVO> tree = menuService.getMenuTree();
        return R.success(tree);
    }

    @GetMapping("/routes")
    @Operation(summary = "查询路由权限配置", description = "查询所有配置了 API 路由的菜单，用于权限管理和展示")
    public R<List<RoutePermissionVO>> getRoutePermissions() {
        List<RoutePermissionVO> routes = menuService.getRoutePermissions();
        return R.success(routes);
    }
}

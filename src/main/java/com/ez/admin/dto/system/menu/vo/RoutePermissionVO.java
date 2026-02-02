package com.ez.admin.dto.system.menu.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 路由权限响应对象
 * <p>
 * 用于返回路由权限配置信息，供前端路由守卫使用
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-27
 */
@Getter
@Builder
@Schema(name = "RoutePermissionVO", description = "路由权限响应对象")
public class RoutePermissionVO {

    @Schema(description = "菜单ID")
    private Long menuId;

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "权限标识")
    private String menuPerm;

    @Schema(description = "后端API路由地址")
    private String apiRoute;

    @Schema(description = "HTTP方法（GET/POST/PUT/DELETE/PATCH）")
    private String apiMethod;

    @Schema(description = "菜单类型（1=目录，2=菜单，3=按钮）")
    private Integer menuType;

    @Schema(description = "路由地址")
    private String routePath;

    @Schema(description = "路由名称")
    private String routeName;

    @Schema(description = "组件路径")
    private String componentPath;
}

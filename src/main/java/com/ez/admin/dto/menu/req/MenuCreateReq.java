package com.ez.admin.dto.menu.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建菜单请求对象
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Data
@Schema(name = "MenuCreateReq", description = "创建菜单请求")
public class MenuCreateReq {

    @Schema(description = "菜单名称", example = "用户管理")
    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称长度不能超过 50 个字符")
    private String menuName;

    @Schema(description = "菜单图标", example = "user")
    @Size(max = 100, message = "菜单图标长度不能超过 100 个字符")
    private String menuIcon;

    @Schema(description = "菜单标识", example = "system:user:list")
    @Size(max = 100, message = "菜单标识长度不能超过 100 个字符")
    private String menuLabel;

    @Schema(description = "父级菜单ID（根菜单为 0）", example = "0")
    @NotNull(message = "父级菜单ID不能为空")
    private Long parentId;

    @Schema(description = "菜单排序", example = "1")
    @NotNull(message = "菜单排序不能为空")
    private Integer menuSort;

    @Schema(description = "菜单类型【1 目录 2 菜单 3 按钮】", example = "2")
    @NotNull(message = "菜单类型不能为空")
    private Integer menuType;

    @Schema(description = "权限标识", example = "system:user:list")
    @Size(max = 100, message = "权限标识长度不能超过 100 个字符")
    private String menuPerm;

    @Schema(description = "路由地址", example = "/system/user")
    @Size(max = 200, message = "路由地址长度不能超过 200 个字符")
    private String routePath;

    @Schema(description = "路由名称", example = "UserList")
    @Size(max = 100, message = "路由名称长度不能超过 100 个字符")
    private String routeName;

    @Schema(description = "组件路径", example = "system/user/index")
    @Size(max = 200, message = "组件路径长度不能超过 200 个字符")
    private String componentPath;

    @Schema(description = "后端API路由地址", example = "/api/user")
    @Size(max = 255, message = "API路由地址长度不能超过 255 个字符")
    private String apiRoute;

    @Schema(description = "HTTP方法", example = "POST")
    @Size(max = 20, message = "HTTP方法长度不能超过 20 个字符")
    private String apiMethod;

    @Schema(description = "菜单状态【0 停用 1 正常】", example = "1")
    @NotNull(message = "菜单状态不能为空")
    private Integer status;

    @Schema(description = "描述信息", example = "用户管理菜单")
    @Size(max = 200, message = "描述长度不能超过 200 个字符")
    private String description;
}

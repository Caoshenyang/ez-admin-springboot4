package com.ez.admin.dto.menu.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 菜单详情响应对象
 * <p>
 * 用于菜单详情展示（getById 接口），包含完整字段
 * </p>
 * <p>
 * 字段说明：
 * <ul>
 *   <li>基础信息：菜单ID、名称、图标、标识</li>
 *   <li>层级信息：父级菜单ID、排序</li>
 *   <li>类型信息：菜单类型（目录/菜单/按钮）、权限标识</li>
 *   <li>路由信息：路由地址、路由名称、组件路径</li>
 *   <li>状态信息：菜单状态、描述</li>
 *   <li>审计信息：创建者、创建时间、更新者、更新时间</li>
 * </ul>
 * </p>
 * <p>
 * 使用场景：
 * <ul>
 *   <li>菜单详情查询（getById）</li>
 *   <li>菜单编辑回显</li>
 * </ul>
 * </p>
 * <p>
 * 其他 VO 类型：
 * <ul>
 *   <li>{@link MenuTreeVO} - 菜单树形结构（getTree）</li>
 *   <li>MenuListVO - 菜单列表（暂未实现）</li>
 * </ul>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Getter
@Builder
@Schema(name = "MenuDetailVO", description = "菜单详情响应对象")
public class MenuDetailVO {

    @Schema(description = "菜单ID")
    private Long menuId;

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "菜单图标")
    private String menuIcon;

    @Schema(description = "菜单标识")
    private String menuLabel;

    @Schema(description = "父级菜单ID")
    private Long parentId;

    @Schema(description = "菜单排序")
    private Integer menuSort;

    @Schema(description = "菜单类型【1 目录 2 菜单 3 按钮】")
    private Integer menuType;

    @Schema(description = "权限标识")
    private String menuPerm;

    @Schema(description = "路由地址")
    private String routePath;

    @Schema(description = "路由名称")
    private String routeName;

    @Schema(description = "组件路径")
    private String componentPath;

    @Schema(description = "菜单状态【0 停用 1 正常】")
    private Integer status;

    @Schema(description = "描述信息")
    private String description;

    @Schema(description = "创建者")
    private String createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新者")
    private String updateBy;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}

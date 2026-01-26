package com.ez.admin.dto.menu.vo;

import com.ez.admin.common.tree.TreeNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单响应对象（树形结构）
 * <p>
 * 继承 TreeNode 以支持树形结构构建，包含以下特性：
 * <ul>
 *   <li>自动管理子节点列表（children）</li>
 *   <li>支持树形遍历、查找、统计等操作</li>
 *   <li>可使用 TreeBuilder 快速构建树形结构</li>
 * </ul>
 * </p>
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 从数据库查询菜单列表
 * List<SysMenu> menuList = menuMapper.selectList(null);
 *
 * // 转换为 VO
 * List<MenuVO> menuVOList = MenuConverter.INSTANCE.toVOList(menuList);
 *
 * // 构建树形结构（自动识别根节点、排序）
 * List<MenuVO> menuTree = TreeBuilder.of(menuVOList)
 *     .enableSort()  // 按 menuSort 排序
 *     .filter(menu -> menu.getStatus() == 1)  // 只保留正常状态的菜单
 *     .build();
 * }</pre>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "MenuVO", description = "菜单响应（树形结构）")
public class MenuVO extends TreeNode {

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

    /**
     * 获取节点 ID（TreeNode 抽象方法实现）
     *
     * @return 菜单 ID
     */
    @Override
    public Long getNodeId() {
        return this.menuId;
    }

    /**
     * 获取父节点 ID（TreeNode 抽象方法实现）
     *
     * @return 父菜单 ID
     */
    @Override
    public Long getParentId() {
        return this.parentId;
    }

    /**
     * 获取排序字段（TreeNode 方法重写）
     *
     * @return 菜单排序值
     */
    @Override
    public Integer getSort() {
        return this.menuSort;
    }

    /**
     * 获取子菜单列表（类型安全的 getter）
     * <p>
     * 注意：children 字段已在父类 TreeNode 中定义，
     * 这里只提供类型安全的访问方法
     * </p>
     *
     * @return 子菜单列表
     */
    @SuppressWarnings("unchecked")
    public List<MenuVO> getChildren() {
        return (List<MenuVO>) super.getChildren();
    }
}

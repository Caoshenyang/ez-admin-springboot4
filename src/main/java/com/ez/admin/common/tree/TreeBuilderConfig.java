package com.ez.admin.common.tree;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * <p>
 * TreeBuilder 配置类（流式编程）
 * </p>
 * <p>
 * 用于链式调用配置树形结构构建参数，支持：
 * <ul>
 *   <li>自定义根节点 ID</li>
 *   <li>排序配置（默认排序或自定义排序）</li>
 *   <li>过滤条件</li>
 * </ul>
 * </p>
 * <p>
 * 使用示例：
 * <pre>{@code
 * List<MenuVO> tree = TreeBuilder.of(menuList)
 *     .rootNodeId(0L)
 *     .enableSort()
 *     .filter(menu -> menu.getStatus() == 1)
 *     .build();
 * }</pre>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Schema(description = "树形结构构建配置类")
public class TreeBuilderConfig<T extends TreeNode> {

    private final List<T> dataList;
    private Long rootNodeId;
    private boolean needSort = false;
    private Predicate<T> filter;
    private Function<T, Integer> sorter;

    /**
     * 构造函数（私有，由 TreeBuilder.of() 创建）
     *
     * @param dataList 包含树形数据的列表
     */
    TreeBuilderConfig(List<T> dataList) {
        this.dataList = dataList;
    }

    /**
     * 设置根节点 ID
     * <p>
     * 指定哪个节点是根节点，例如：
     * <ul>
     *   <li>{@code rootNodeId(0L)} - parentId 为 0 的节点是根节点</li>
     *   <li>{@code rootNodeId(-1L)} - parentId 为 -1 的节点是根节点</li>
     * </ul>
     * </p>
     *
     * @param rootNodeId 根节点 ID
     * @return this
     */
    public TreeBuilderConfig<T> rootNodeId(Long rootNodeId) {
        this.rootNodeId = rootNodeId;
        return this;
    }

    /**
     * 启用排序（使用 TreeNode 的 getSort 方法）
     * <p>
     * 会调用每个节点的 {@link TreeNode#getSort()} 方法获取排序值，
     * 按照升序排列（null 值排在最后）
     * </p>
     *
     * @return this
     */
    public TreeBuilderConfig<T> enableSort() {
        this.needSort = true;
        this.sorter = TreeNode::getSort;
        return this;
    }

    /**
     * 设置自定义排序规则
     * <p>
     * 自定义排序函数，例如：
     * <pre>{@code
     * // 先按类型排序，再按排序字段排序
     * .sortBy(menu -> menu.getMenuType() * 1000 + menu.getMenuSort())
     * }</pre>
     * </p>
     *
     * @param sorter 排序规则函数，返回节点的排序值
     * @return this
     */
    public TreeBuilderConfig<T> sortBy(Function<T, Integer> sorter) {
        this.needSort = true;
        this.sorter = sorter;
        return this;
    }

    /**
     * 设置过滤条件
     * <p>
     * 只保留符合条件的节点，例如：
     * <pre>{@code
     * // 只保留正常状态的菜单
     * .filter(menu -> menu.getStatus() == 1)
     *
     * // 只保留目录和菜单类型
     * .filter(menu -> menu.getMenuType() != 3)
     *
     * // 多条件过滤
     * .filter(menu -> menu.getStatus() == 1 && menu.getMenuType() != 3)
     * }</pre>
     * </p>
     *
     * @param filter 过滤条件，返回 true 表示保留该节点
     * @return this
     */
    public TreeBuilderConfig<T> filter(Predicate<T> filter) {
        this.filter = filter;
        return this;
    }

    /**
     * 构建树形结构
     * <p>
     * 根据配置的参数构建树形结构，返回根节点列表
     * </p>
     *
     * @return 树形结构的根节点列表
     */
    public List<T> build() {
        return TreeBuilder.buildTree(dataList, rootNodeId, needSort, filter, sorter);
    }
}

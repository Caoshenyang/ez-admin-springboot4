package com.ez.admin.common.tree;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <p>
 * 树形结构构建器
 * </p>
 * <p>
 * 设计思路：
 * <ul>
 *   <li>泛型支持：支持任意继承 TreeNode 的类型</li>
 *   <li>高性能：使用 LinkedHashMap 缓存节点，时间复杂度 O(n)</li>
 *   <li>灵活配置：支持自定义根节点 ID、排序、过滤等</li>
 *   <li>链式调用：支持流式编程，提高代码可读性</li>
 * </ul>
 * </p>
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 简单用法
 * List<MenuVO> tree = TreeBuilder.build(menuList);
 *
 * // 高级用法：指定根节点 ID、排序、过滤
 * List<MenuVO> tree = TreeBuilder.of(menuList)
 *     .rootNodeId(0L)
 *     .sortBy(MenuVO::getMenuSort)
 *     .filter(node -> node.getStatus() == 1)
 *     .build();
 * }</pre>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Schema(description = "树形结构构建器")
public class TreeBuilder {

    /**
     * 构建树形结构（简单用法）
     * <p>
     * 默认规则：
     * <ul>
     *   <li>根节点：parentId == null 或 parentId == 0 或在列表中找不到父节点</li>
     *   <li>顺序：保持原始顺序（LinkedHashMap）</li>
     *   <li>不过滤：所有节点都会被包含在树中</li>
     * </ul>
     * </p>
     *
     * @param dataList 包含树形数据的列表
     * @param <T>      树形数据对象的类型，必须继承 TreeNode
     * @return 树形结构的根节点列表
     */
    public static <T extends TreeNode<T>> List<T> build(List<T> dataList) {
        return buildTree(dataList, null, false, null, null);
    }

    /**
     * 构建树形结构（指定根节点 ID）
     *
     * @param dataList   包含树形数据的列表
     * @param rootNodeId 根节点 ID（如 0L、-1L），null 表示自动识别
     * @param <T>        树形数据对象的类型
     * @return 树形结构的根节点列表
     */
    public static <T extends TreeNode<T>> List<T> build(List<T> dataList, Long rootNodeId) {
        return buildTree(dataList, rootNodeId, false, null, null);
    }

    /**
     * 构建树形结构（带过滤）
     *
     * @param dataList 包含树形数据的列表
     * @param filter   过滤条件，返回 true 表示保留该节点
     * @param <T>      树形数据对象的类型
     * @return 树形结构的根节点列表
     */
    public static <T extends TreeNode<T>> List<T> build(List<T> dataList, Predicate<T> filter) {
        return buildTree(dataList, null, false, filter, null);
    }

    /**
     * 构建树形结构（带排序）
     *
     * @param dataList 包含树形数据的列表
     * @param sorter   排序规则，返回节点的排序值
     * @param <T>      树形数据对象的类型
     * @return 树形结构的根节点列表
     */
    public static <T extends TreeNode<T>> List<T> build(List<T> dataList, Function<T, Integer> sorter) {
        return buildTree(dataList, null, true, null, sorter);
    }

    /**
     * 创建 TreeBuilder 实例（流式编程）
     *
     * @param dataList 包含树形数据的列表
     * @param <T>      树形数据对象的类型
     * @return TreeBuilder 实例
     */
    public static <T extends TreeNode> TreeBuilderConfig<T> of(List<T> dataList) {
        return new TreeBuilderConfig<>(dataList);
    }

    /**
     * 核心构建逻辑
     *
     * @param dataList   原始数据列表
     * @param rootNodeId 根节点 ID（null 表示自动识别）
     * @param needSort   是否需要排序
     * @param filter     过滤条件
     * @param sorter     排序规则
     * @param <T>        树形数据对象的类型
     * @return 树形结构的根节点列表
     */
    public static <T extends TreeNode<T>> List<T> buildTree(List<T> dataList, Long rootNodeId, boolean needSort, Predicate<T> filter, Function<T, Integer> sorter) {

        if (dataList == null || dataList.isEmpty()) {
            return new ArrayList<>();
        }

        // 1. 过滤
        List<T> filteredList = filter != null ? dataList.stream().filter(filter).toList() : dataList;

        if (filteredList.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 构建 nodeMap（缓存节点，提高查找效率）
        // k - nodeId, v - treeNode
        Map<Long, T> nodeMap = filteredList.stream().collect(Collectors.toMap(TreeNode::getNodeId, Function.identity(), (existing, replacement) -> existing,  // 键冲突时保留原有值
                LinkedHashMap::new  // 保持顺序
        ));

        // 3. 识别并收集根节点 ID
        List<Long> rootIds = new ArrayList<>();
        filteredList.forEach(node -> {
            Long parentId = node.getParentId();

            // 判断是否为根节点
            boolean isRoot = false;
            if (rootNodeId != null) {
                // 明确指定了根节点 ID
                isRoot = rootNodeId.equals(parentId);
            } else {
                // 自动识别：parentId == null、parentId == 0 或在列表中找不到父节点
                isRoot = parentId == null || parentId == 0L || !nodeMap.containsKey(parentId);
            }

            if (isRoot) {
                rootIds.add(node.getNodeId());
                // 确保根节点在 nodeMap 中
                nodeMap.computeIfAbsent(node.getNodeId(), k -> node);
            } else {
                // 非根节点，将其添加到父节点的子节点列表中
                nodeMap.computeIfPresent(parentId, (key, parentNode) -> {
                    parentNode.addChild(node);
                    return parentNode;
                });
            }
        });

        // 4. 获取根节点列表
        List<T> roots = nodeMap.values().stream().filter(node -> rootIds.contains(node.getNodeId())).collect(Collectors.toList());

        // 5. 排序（如果需要）
        if (needSort && sorter != null) {
            sortTree(roots, sorter);
        }

        return roots;
    }

    /**
     * 递归排序整棵树
     *
     * @param nodes  节点列表
     * @param sorter 排序规则
     * @param <T>    节点类型
     */
    @SuppressWarnings("unchecked")
    private static <T extends TreeNode<T>> void sortTree(List<T> nodes, Function<T, Integer> sorter) {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }

        // 对当前层排序
        nodes.sort((a, b) -> {
            Integer sortA = sorter.apply(a);
            Integer sortB = sorter.apply(b);
            if (sortA == null && sortB == null) {
                return 0;
            }
            if (sortA == null) {
                return 1;  // null 值排在后面
            }
            if (sortB == null) {
                return -1;
            }
            return sortA.compareTo(sortB);
        });

        // 递归排序子节点
        nodes.forEach(node -> {
            if (node.hasChildren()) {
                sortTree(node.getChildren(), sorter);
            }
        });
    }

    /**
     * 将树形结构平铺为列表（深度优先遍历）
     *
     * @param tree 树形结构
     * @param <T>  节点类型
     * @return 平铺后的列表
     */
    public static <T extends TreeNode<T>> List<T> flatten(List<T> tree) {
        List<T> result = new ArrayList<>();
        if (tree == null || tree.isEmpty()) {
            return result;
        }
        flatten(tree, result);
        return result;
    }

    /**
     * 递归平铺
     */
    @SuppressWarnings("unchecked")
    private static <T extends TreeNode<T>> void flatten(List<T> tree, List<T> result) {
        for (T node : tree) {
            result.add(node);
            if (node.hasChildren()) {
                flatten(node.getChildren(), result);
            }
        }
    }

    /**
     * 过滤树形结构（保留符合条件的节点及其子孙节点）
     *
     * @param tree   树形结构
     * @param filter 过滤条件
     * @param <T>    节点类型
     * @return 过滤后的树形结构
     */
    @SuppressWarnings("unchecked")
    public static <T extends TreeNode<T>> List<T> filterTree(List<T> tree, Predicate<T> filter) {
        if (tree == null || tree.isEmpty()) {
            return new ArrayList<>();
        }

        return tree.stream().filter(node -> filterNode(node, filter)).peek(node -> {
            if (node.hasChildren()) {
                List<T> filteredChildren = node.getChildren();
                node.setChildren(new ArrayList<>(filterTree(filteredChildren, filter)));
            }
        }).collect(Collectors.toList());
    }

    /**
     * 递归检查节点是否符合条件（或其子孙节点符合条件）
     */
    @SuppressWarnings("unchecked")
    private static <T extends TreeNode> boolean filterNode(T node, Predicate<T> filter) {
        // 当前节点符合条件
        if (filter.test(node)) {
            return true;
        }

        // 检查子孙节点
        if (node.hasChildren()) {
            for (TreeNode<T> child : node.getChildren()) {
                if (filterNode(child, filter)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 遍历树形结构（深度优先）
     *
     * @param tree   树形结构
     * @param action 对每个节点执行的操作
     * @param <T>    节点类型
     */
    @SuppressWarnings("unchecked")
    public static <T extends TreeNode> void forEach(List<T> tree, Consumer<T> action) {
        if (tree == null || tree.isEmpty()) {
            return;
        }
        tree.forEach(node -> {
            action.accept(node);
            if (node.hasChildren()) {
                forEach(node.getChildren(), action);
            }
        });
    }

    /**
     * 查找符合条件的节点
     *
     * @param tree   树形结构
     * @param filter 匹配条件
     * @param <T>    节点类型
     * @return 第一个符合条件的节点，未找到返回 null
     */
    @SuppressWarnings("unchecked")
    public static <T extends TreeNode> T find(List<T> tree, Predicate<T> filter) {
        if (tree == null || tree.isEmpty()) {
            return null;
        }

        for (T node : tree) {
            if (filter.test(node)) {
                return node;
            }
            if (node.hasChildren()) {
                T found = find(node.getChildren(), filter);
                if (found != null) {
                    return found;
                }
            }
        }

        return null;
    }

    /**
     * 统计树形结构中的节点总数
     *
     * @param tree 树形结构
     * @param <T>  节点类型
     * @return 节点总数
     */
    public static <T extends TreeNode> int count(List<T> tree) {
        if (tree == null || tree.isEmpty()) {
            return 0;
        }

        int count = tree.size();
        for (T node : tree) {
            if (node.hasChildren()) {
                count += count(node.getChildren());
            }
        }

        return count;
    }

    /**
     * 获取树形结构的最大深度
     *
     * @param tree 树形结构
     * @param <T>  节点类型
     * @return 最大深度（根节点深度为 1）
     */
    public static <T extends TreeNode> int getDepth(List<T> tree) {
        if (tree == null || tree.isEmpty()) {
            return 0;
        }

        int maxDepth = 0;
        for (T node : tree) {
            maxDepth = Math.max(maxDepth, node.getDepth() + 1);
        }

        return maxDepth;
    }
}

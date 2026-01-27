package com.ez.admin.common.data.tree;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * <p>
 * 树形节点抽象基类（泛型支持）
 * </p>
 * <p>
 * 设计思路：
 * <ul>
 *   <li>使用泛型：支持类型安全的 children 列表，避免强制类型转换</li>
 *   <li>使用抽象类而非接口：可以直接包含 children 字段和默认实现，避免每个子类重复定义</li>
 *   <li>提供抽象方法：由子类实现 getNodeId 和 getParentId，支持不同的 ID 字段名（如 menuId、deptId）</li>
 *   <li>提供便利方法：addChild、removeChild、clearChildren 等，方便操作子节点</li>
 *   <li>支持遍历：forEach、forEachChildren 等方法，支持递归遍历整棵树</li>
 * </ul>
 * </p>
 * <p>
 * 使用示例：
 * <pre>{@code
 * public class MenuTreeVO extends TreeNode<MenuTreeVO> {
 *     @Override
 *     public Long getNodeId() {
 *         return this.menuId;
 *     }
 *
 *     @Override
 *     public Long getParentId() {
 *         return this.parentId;
 *     }
 *
 *     // 类型安全的 getter
 *     public List<MenuTreeVO> getChildren() {
 *         return super.getChildren();
 *     }
 * }
 * }</pre>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-26
 * @param <T> 子类类型（递归泛型）
 */
@Data
@Schema(description = "树形节点基类")
public abstract class TreeNode<T extends TreeNode<T>> {

    /**
     * 子节点列表（类型安全）
     */
    @Schema(description = "子节点列表")
    private List<T> children = new ArrayList<>();

    /**
     * 获取节点 ID
     * <p>
     * 由子类实现，支持不同的 ID 字段名，如：
     * <ul>
     *   <li>SysMenu: return menuId</li>
     *   <li>SysDept: return deptId</li>
     * </ul>
     * </p>
     *
     * @return 节点 ID
     */
    public abstract Long getNodeId();

    /**
     * 获取父节点 ID
     * <p>
     * 由子类实现，通常直接返回 parentId 字段
     * </p>
     *
     * @return 父节点 ID，如果为 null 或 0 表示根节点
     */
    public abstract Long getParentId();

    /**
     * 获取排序字段
     * <p>
     * 用于子节点排序，默认实现返回 null（不排序）
     * 子类可以重写此方法以支持自定义排序字段
     * </p>
     *
     * @return 排序值，越小越靠前
     */
    public Integer getSort() {
        return null;
    }

    /**
     * 判断是否为根节点
     *
     * @return true-根节点，false-非根节点
     */
    public boolean isRoot() {
        Long parentId = getParentId();
        return parentId == null || parentId == 0L;
    }

    /**
     * 判断是否有子节点
     *
     * @return true-有子节点，false-无子节点
     */
    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    /**
     * 添加子节点
     *
     * @param child 子节点
     */
    public void addChild(T child) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(child);
    }

    /**
     * 移除指定索引的子节点
     *
     * @param index 子节点索引
     * @return 被移除的子节点
     */
    public T removeChild(int index) {
        if (this.children == null || index < 0 || index >= this.children.size()) {
            return null;
        }
        return this.children.remove(index);
    }

    /**
     * 清空子节点
     */
    public void clearChildren() {
        if (this.children != null) {
            this.children.clear();
        }
    }

    /**
     * 遍历当前节点及所有子孙节点（深度优先）
     *
     * @param action 对每个节点执行的操作
     */
    @SuppressWarnings("unchecked")
    public void forEach(Consumer<T> action) {
        action.accept((T) this);
        if (hasChildren()) {
            children.forEach(child -> child.forEach(action));
        }
    }

    /**
     * 仅遍历子节点（不包括当前节点）
     *
     * @param action 对每个子节点执行的操作
     */
    public void forEachChildren(Consumer<T> action) {
        if (hasChildren()) {
            children.forEach(action);
        }
    }

    /**
     * 查找符合条件的节点（深度优先）
     *
     * @param predicate 匹配条件
     * @return 第一个符合条件的节点，未找到返回 null
     */
    @SuppressWarnings("unchecked")
    public T find(Predicate<T> predicate) {
        if (predicate.test((T) this)) {
            return (T) this;
        }
        if (hasChildren()) {
            for (T child : children) {
                T found = child.find(predicate);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    /**
     * 统计所有子孙节点数量（不包括当前节点）
     *
     * @return 子孙节点总数
     */
    public int countDescendants() {
        if (!hasChildren()) {
            return 0;
        }
        int count = children.size();
        for (TreeNode<T> child : children) {
            count += child.countDescendants();
        }
        return count;
    }

    /**
     * 获取树的深度（当前节点为 0）
     *
     * @return 树的深度
     */
    public int getDepth() {
        if (!hasChildren()) {
            return 0;
        }
        int maxDepth = 0;
        for (TreeNode<T> child : children) {
            maxDepth = Math.max(maxDepth, child.getDepth());
        }
        return maxDepth + 1;
    }
}

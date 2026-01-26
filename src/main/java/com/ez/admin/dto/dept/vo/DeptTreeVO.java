package com.ez.admin.dto.dept.vo;

import com.ez.admin.common.tree.TreeNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门树形响应对象
 * <p>
 * 继承 TreeNode 以支持树形结构构建，包含以下特性：
 * <ul>
 *   <li>类型安全的子节点列表（无需强制转换）</li>
 *   <li>支持树形遍历、查找、统计等操作</li>
 *   <li>可使用 TreeBuilder 快速构建树形结构</li>
 * </ul>
 * </p>
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 从数据库查询部门列表
 * List<SysDept> deptList = deptMapper.selectList(null);
 *
 * // 转换为 TreeVO
 * List<DeptTreeVO> deptVOList = DeptConverter.INSTANCE.toTreeVOList(deptList);
 *
 * // 构建树形结构（自动识别根节点、排序）
 * List<DeptTreeVO> deptTree = TreeBuilder.of(deptVOList)
 *     .enableSort()  // 按 deptSort 排序
 *     .filter(dept -> dept.getStatus() == 1)  // 只保留正常状态的部门
 *     .build();
 * }</pre>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "DeptTreeVO", description = "部门树形响应对象")
public class DeptTreeVO extends TreeNode<DeptTreeVO> {

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "排序")
    private Integer deptSort;

    @Schema(description = "祖级路径")
    private String ancestors;

    @Schema(description = "父级部门ID")
    private Long parentId;

    @Schema(description = "部门状态【0 停用 1 正常】")
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
     * @return 部门 ID
     */
    @Override
    public Long getNodeId() {
        return this.deptId;
    }

    /**
     * 获取父节点 ID（TreeNode 抽象方法实现）
     *
     * @return 父部门 ID
     */
    @Override
    public Long getParentId() {
        return this.parentId;
    }

    /**
     * 获取排序字段（TreeNode 方法重写）
     *
     * @return 部门排序值
     */
    @Override
    public Integer getSort() {
        return this.deptSort;
    }

    /**
     * 获取子部门列表（类型安全的 getter）
     * <p>
     * 由于 TreeNode 已是泛型，这里直接返回类型安全的 List
     * </p>
     *
     * @return 子部门列表
     */
    @Override
    public List<DeptTreeVO> getChildren() {
        return super.getChildren();
    }
}

package com.ez.admin.dto.dept.vo;

import com.ez.admin.common.tree.TreeNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门响应对象（树形结构）
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
 * // 从数据库查询部门列表
 * List<SysDept> deptList = deptMapper.selectList(null);
 *
 * // 转换为 VO
 * List<DeptVO> deptVOList = DeptConverter.INSTANCE.toVOList(deptList);
 *
 * // 构建树形结构（自动识别根节点、排序）
 * List<DeptVO> deptTree = TreeBuilder.of(deptVOList)
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
@Schema(name = "DeptVO", description = "部门响应（树形结构）")
public class DeptVO extends TreeNode {

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
     * 注意：children 字段已在父类 TreeNode 中定义，
     * 这里只提供类型安全的访问方法
     * </p>
     *
     * @return 子部门列表
     */
    @SuppressWarnings("unchecked")
    public List<DeptVO> getChildren() {
        return (List<DeptVO>) super.getChildren();
    }
}

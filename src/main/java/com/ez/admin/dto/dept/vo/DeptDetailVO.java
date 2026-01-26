package com.ez.admin.dto.dept.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 部门详情响应对象
 * <p>
 * 用于部门详情展示（getById 接口），包含完整字段
 * </p>
 * <p>
 * 字段说明：
 * <ul>
 *   <li>基础信息：部门ID、名称、排序</li>
 *   <li>层级信息：父级ID、祖级路径</li>
 *   <li>状态信息：部门状态、描述</li>
 *   <li>审计信息：创建者、创建时间、更新者、更新时间</li>
 * </ul>
 * </p>
 * <p>
 * 使用场景：
 * <ul>
 *   <li>部门详情查询（getById）</li>
 *   <li>部门编辑回显</li>
 * </ul>
 * </p>
 * <p>
 * 其他 VO 类型：
 * <ul>
 *   <li>{@link DeptTreeVO} - 部门树形结构（getTree）</li>
 *   <li>DeptListVO - 部门列表（暂未实现）</li>
 * </ul>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Getter
@Builder
@Schema(name = "DeptDetailVO", description = "部门详情响应对象")
public class DeptDetailVO {

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
}

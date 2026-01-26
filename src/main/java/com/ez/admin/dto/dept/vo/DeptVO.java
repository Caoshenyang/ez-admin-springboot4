package com.ez.admin.dto.dept.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门响应对象（树形结构）
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Getter
@Builder
@Schema(name = "DeptVO", description = "部门响应（树形结构）")
public class DeptVO {

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

    @Schema(description = "子部门列表（树形结构）")
    private List<DeptVO> children;
}

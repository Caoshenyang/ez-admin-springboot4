package com.ezadmin.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 部门更新
 */
@Data
public class DeptUpdateDTO implements Serializable {

    @Schema(description = "部门ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long deptId;

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "排序")
    private Integer deptSort;

    @Schema(description = "父级部门ID")
    private Long parentId;

    @Schema(description = "部门状态【0 停用 1 正常】")
    private Integer status;

    @Schema(description = "描述")
    private String description;
}

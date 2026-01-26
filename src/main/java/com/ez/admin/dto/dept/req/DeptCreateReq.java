package com.ez.admin.dto.dept.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建部门请求对象
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Data
@Schema(name = "DeptCreateReq", description = "创建部门请求")
public class DeptCreateReq {

    @Schema(description = "部门名称", example = "研发部")
    @NotBlank(message = "部门名称不能为空")
    @Size(max = 50, message = "部门名称长度不能超过 50 个字符")
    private String deptName;

    @Schema(description = "排序", example = "1")
    @NotNull(message = "排序不能为空")
    private Integer deptSort;

    @Schema(description = "父级部门ID（根部门为 0）", example = "0")
    @NotNull(message = "父级部门ID不能为空")
    private Long parentId;

    @Schema(description = "部门状态【0 停用 1 正常】", example = "1")
    @NotNull(message = "部门状态不能为空")
    private Integer status;

    @Schema(description = "描述信息", example = "负责产品研发")
    @Size(max = 200, message = "描述长度不能超过 200 个字符")
    private String description;
}

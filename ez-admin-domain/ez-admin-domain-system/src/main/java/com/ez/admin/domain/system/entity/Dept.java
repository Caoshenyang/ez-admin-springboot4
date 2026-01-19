package com.ez.admin.system.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 部门信息表
 *
 * @author ez-admin
 * @since 2026-01-19
 */
@Schema(description = "部门信息表")
@Getter
@Setter
@ToString
@TableName("ez_admin_sys_dept")
public class Dept implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 部门ID
     */
    @Schema(description = "部门ID", example = "1")
    @TableId(value = "dept_id", type = IdType.ASSIGN_ID)
    private Long deptId;

    /**
     * 部门名称
     */
    @Schema(description = "部门名称", example = "技术部")
    @TableField("dept_name")
    private String deptName;

    /**
     * 排序
     */
    @Schema(description = "排序", example = "1")
    @TableField("dept_sort")
    private Integer deptSort;

    /**
     * 祖级路径，格式：/1/2/
     */
    @Schema(description = "祖级路径（格式：/1/2/）", example = "/0/1/")
    @TableField("ancestors")
    private String ancestors;

    /**
     * 父级菜单ID
     */
    @Schema(description = "父级部门ID（0表示根部门）", example = "0")
    @TableField("parent_id")
    private Long parentId;

    /**
     * 部门状态【0 停用 1 正常】
     */
    @Schema(description = "部门状态（0=停用，1=正常）", example = "1")
    @TableField("status")
    private Integer status;

    /**
     * 描述
     */
    @Schema(description = "描述", example = "技术研发部门")
    @TableField("description")
    private String description;

    /**
     * 创建者
     */
    @Schema(description = "创建者", example = "admin")
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新者
     */
    @Schema(description = "更新者", example = "admin")
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2026-01-19T10:00:00")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间", example = "2026-01-19T10:00:00")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否删除【0 正常 1 已删除】
     */
    @Schema(description = "是否删除（0=正常，1=已删除）", example = "0")
    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;
}

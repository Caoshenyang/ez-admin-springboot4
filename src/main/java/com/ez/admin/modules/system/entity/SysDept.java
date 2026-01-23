package com.ez.admin.modules.system.entity;

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
 * <p>
 * 部门信息表
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Getter
@Setter
@ToString
@TableName("ez_admin_sys_dept")
@Schema(name = "SysDept", description = "部门信息表")
public class SysDept implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "部门ID")
    @TableId(value = "dept_id", type = IdType.ASSIGN_ID)
    private Long deptId;

    @TableField("dept_name")
    @Schema(description = "部门名称")
    private String deptName;

    @TableField("dept_sort")
    @Schema(description = "排序")
    private Integer deptSort;

    @TableField("ancestors")
    @Schema(description = "祖级路径，格式：/1/2/")
    private String ancestors;

    @TableField("parent_id")
    @Schema(description = "父级菜单ID")
    private Long parentId;

    @TableField("status")
    @Schema(description = "部门状态【0 停用 1 正常】")
    private Integer status;

    @TableField("description")
    @Schema(description = "描述")
    private String description;

    @Schema(description = "创建者")
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    @Schema(description = "更新者")
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("is_deleted")
    @Schema(description = "是否删除【0 正常 1 已删除】")
    private Integer isDeleted;
}

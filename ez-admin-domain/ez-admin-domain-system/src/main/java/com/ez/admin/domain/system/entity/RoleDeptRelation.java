package com.ez.admin.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 角色部门关联表
 *
 * @author ez-admin
 * @since 2026-01-19
 */
@Schema(description = "角色部门关联表")
@Getter
@Setter
@ToString
@TableName("ez_admin_sys_role_dept_relation")
public class RoleDeptRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID", example = "1")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 角色ID
     */
    @Schema(description = "角色ID", example = "1")
    @TableField("role_id")
    private Long roleId;

    /**
     * 部门ID
     */
    @Schema(description = "部门ID", example = "1")
    @TableField("dept_id")
    private Long deptId;
}

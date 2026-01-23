package com.ez.admin.modules.admin.entity;

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
 * <p>
 * 角色部门关联表
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Getter
@Setter
@ToString
@TableName("ez_admin_sys_role_dept_relation")
@Schema(name = "SysRoleDeptRelation", description = "角色部门关联表")
public class SysRoleDeptRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("role_id")
    @Schema(description = "角色ID")
    private Long roleId;

    @TableField("dept_id")
    @Schema(description = "部门ID")
    private Long deptId;
}

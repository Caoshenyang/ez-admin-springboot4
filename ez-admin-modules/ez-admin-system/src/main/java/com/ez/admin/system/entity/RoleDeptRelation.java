package com.ez.admin.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2026-01-19
 */
@Getter
@Setter
@ToString
@TableName("ez_admin_sys_role_dept_relation")
public class RoleDeptRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 角色ID
     */
    @TableField("role_id")
    private Long roleId;

    /**
     * 部门ID
     */
    @TableField("dept_id")
    private Long deptId;
}

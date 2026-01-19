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
 * 角色信息表
 *
 * @author ez-admin
 * @since 2026-01-19
 */
@Schema(description = "角色信息表")
@Getter
@Setter
@ToString
@TableName("ez_admin_sys_role")
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @Schema(description = "角色ID", example = "1")
    @TableId(value = "role_id", type = IdType.ASSIGN_ID)
    private Long roleId;

    /**
     * 角色名称
     */
    @Schema(description = "角色名称", example = "管理员")
    @TableField("role_name")
    private String roleName;

    /**
     * 角色权限字符标识
     */
    @Schema(description = "角色权限字符标识", example = "admin")
    @TableField("role_label")
    private String roleLabel;

    /**
     * 排序
     */
    @Schema(description = "排序", example = "1")
    @TableField("role_sort")
    private Integer roleSort;

    /**
     * 数据范围【1 仅本人数据权限 2 本部门数据权限 3 本部门及以下数据权限 4 自定义数据权限 5 全部数据权限】
     */
    @Schema(description = "数据范围（1=仅本人，2=本部门，3=本部门及以下，4=自定义，5=全部）", example = "5")
    @TableField("data_scope")
    private Integer dataScope;

    /**
     * 角色状态【0 停用 1 正常】
     */
    @Schema(description = "角色状态（0=停用，1=正常）", example = "1")
    @TableField("status")
    private Integer status;

    /**
     * 创建者
     */
    @Schema(description = "创建者", example = "admin")
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2026-01-19T10:00:00")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    @Schema(description = "更新者", example = "admin")
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间", example = "2026-01-19T10:00:00")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 描述信息
     */
    @Schema(description = "描述信息", example = "系统管理员角色")
    @TableField("description")
    private String description;

    /**
     * 是否删除【0 正常 1 已删除】
     */
    @Schema(description = "是否删除（0=正常，1=已删除）", example = "0")
    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;
}

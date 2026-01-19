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
 * 用户信息表
 *
 * @author ez-admin
 * @since 2026-01-19
 */
@Schema(description = "用户信息表")
@Getter
@Setter
@ToString
@TableName("ez_admin_sys_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID", example = "1")
    @TableId(value = "user_id", type = IdType.ASSIGN_ID)
    private Long userId;

    @Schema(description = "部门ID", example = "1")
    @TableField("dept_id")
    private Long deptId;

    @Schema(description = "用户账号", example = "admin")
    @TableField("username")
    private String username;

    @Schema(description = "密码（加密存储）", example = "******")
    @TableField("password")
    private String password;

    @Schema(description = "用户昵称", example = "管理员")
    @TableField("nickname")
    private String nickname;

    @Schema(description = "用户邮箱", example = "admin@example.com")
    @TableField("email")
    private String email;

    @Schema(description = "用户手机号码", example = "13800138000")
    @TableField("phone_number")
    private String phoneNumber;

    @Schema(description = "性别（0=保密，1=男，2=女）", example = "0")
    @TableField("gender")
    private Integer gender;

    @Schema(description = "用户头像URL", example = "https://example.com/avatar.jpg")
    @TableField("avatar")
    private String avatar;

    @Schema(description = "用户状态（0=禁用，1=正常）", example = "1")
    @TableField("status")
    private Integer status;

    @Schema(description = "最后登录IP", example = "192.168.1.1")
    @TableField("login_ip")
    private String loginIp;

    @Schema(description = "最后登录时间", example = "2026-01-19T10:00:00")
    @TableField("login_date")
    private LocalDateTime loginDate;

    @Schema(description = "创建者", example = "admin")
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    @Schema(description = "创建时间", example = "2026-01-19T10:00:00")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新者", example = "admin")
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @Schema(description = "更新时间", example = "2026-01-19T10:00:00")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Schema(description = "描述信息", example = "系统管理员")
    @TableField("description")
    private String description;

    @Schema(description = "是否删除（0=正常，1=已删除）", example = "0")
    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;
}

package com.ez.admin.modules.admin.entity;

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
 * 用户信息表
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Getter
@Setter
@ToString
@TableName("ez_admin_sys_user")
@Schema(name = "SysUser", description = "用户信息表")
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    @TableId(value = "user_id", type = IdType.ASSIGN_ID)
    private Long userId;

    @TableField("dept_id")
    @Schema(description = "部门ID")
    private Long deptId;

    @TableField("username")
    @Schema(description = "用户账号")
    private String username;

    @TableField("password")
    @Schema(description = "密码")
    private String password;

    @TableField("nickname")
    @Schema(description = "用户昵称")
    private String nickname;

    @TableField("email")
    @Schema(description = "用户邮箱")
    private String email;

    @TableField("phone_number")
    @Schema(description = "用户手机号码")
    private String phoneNumber;

    @TableField("gender")
    @Schema(description = "性别【0 保密 1 男 2 女】")
    private Integer gender;

    @TableField("avatar")
    @Schema(description = "用户头像")
    private String avatar;

    @TableField("status")
    @Schema(description = "用户状态【0 禁用 1 正常】")
    private Integer status;

    @TableField("login_ip")
    @Schema(description = "最后登录IP")
    private String loginIp;

    @TableField("login_date")
    @Schema(description = "最后登录时间")
    private LocalDateTime loginDate;

    @Schema(description = "创建者")
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新者")
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @Schema(description = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField("description")
    @Schema(description = "描述信息")
    private String description;

    @TableLogic
    @TableField("is_deleted")
    @Schema(description = "是否删除【0 正常 1 已删除】")
    private Integer isDeleted;
}

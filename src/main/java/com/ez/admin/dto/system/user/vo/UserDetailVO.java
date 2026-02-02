package com.ez.admin.dto.system.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户详情响应对象
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Getter
@Builder
@Schema(name = "UserDetailVO", description = "用户详情响应")
public class UserDetailVO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phoneNumber;

    @Schema(description = "性别（0=保密，1=男，2=女）")
    private Integer gender;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "状态（0=禁用，1=正常）")
    private Integer status;

    @Schema(description = "最后登录IP")
    private String loginIp;

    @Schema(description = "最后登录时间")
    private LocalDateTime loginDate;

    @Schema(description = "创建者ID")
    private Long createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新者ID")
    private Long updateBy;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "描述信息")
    private String description;

    @Schema(description = "角色列表")
    private List<RoleVO> roles;
}

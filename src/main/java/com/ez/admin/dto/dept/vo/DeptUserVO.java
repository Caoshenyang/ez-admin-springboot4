package com.ez.admin.dto.dept.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 部门用户响应对象
 *
 * @author ez-admin
 * @since 2026-01-27
 */
@Getter
@Builder
@Schema(name = "DeptUserVO", description = "部门用户响应对象")
public class DeptUserVO {

    @Schema(description = "用户ID")
    private Long userId;

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

    @Schema(description = "用户状态（0=禁用，1=正常）")
    private Integer status;

    @Schema(description = "最后登录IP")
    private String loginIp;

    @Schema(description = "最后登录时间")
    private LocalDateTime loginDate;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}

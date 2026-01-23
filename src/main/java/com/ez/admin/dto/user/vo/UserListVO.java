package com.ez.admin.dto.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 用户列表响应对象
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Getter
@Builder
@Schema(name = "UserListVO", description = "用户列表响应")
public class UserListVO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "手机号")
    private String phoneNumber;

    @Schema(description = "状态（0=禁用，1=正常）")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}

package com.ez.admin.dto.install.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 系统初始化响应对象
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Getter
@Builder
@Schema(name = "InstallVO", description = "系统初始化响应")
public class InstallVO {

    @Schema(description = "管理员用户ID")
    private Long userId;

    @Schema(description = "管理员用户名")
    private String username;

    @Schema(description = "管理员昵称")
    private String nickname;

    @Schema(description = "初始化时间")
    private LocalDateTime initTime;
}

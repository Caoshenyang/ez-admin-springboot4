package com.ez.admin.dto.install.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 系统初始化状态响应对象
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Getter
@Builder
@Schema(name = "InstallStatusVO", description = "系统初始化状态响应")
public class InstallStatusVO {

    @Schema(description = "系统是否已初始化")
    private boolean initialized;

    @Schema(description = "提示信息")
    private String message;
}

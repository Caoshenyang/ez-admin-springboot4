package com.ez.admin.dto.user.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用户状态切换请求对象
 *
 * @author ez-admin
 * @since 2026-01-27
 */
@Data
@Schema(name = "UserStatusChangeReq", description = "用户状态切换请求")
public class UserStatusChangeReq {

    @Schema(description = "用户ID", example = "1")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "状态（0=禁用，1=正常）", example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;
}

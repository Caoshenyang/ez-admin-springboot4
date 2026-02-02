package com.ez.admin.dto.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 登录响应对象（视图对象）
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Getter
@Builder
@Schema(name = "LoginVO", description = "登录响应")
public class LoginVO {

    @Schema(description = "访问令牌")
    private String token;
}

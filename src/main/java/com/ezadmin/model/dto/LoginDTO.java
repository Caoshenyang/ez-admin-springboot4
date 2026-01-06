package com.ezadmin.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 登录DTO
 * </p>
 *
 * @author 曹申阳
 * @since 2025-12-16 17:01:24
 */
@Data
public class LoginDTO {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

}

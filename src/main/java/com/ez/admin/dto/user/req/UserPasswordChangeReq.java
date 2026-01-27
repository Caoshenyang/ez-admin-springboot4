package com.ez.admin.dto.user.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户密码修改请求对象
 *
 * @author ez-admin
 * @since 2026-01-27
 */
@Data
@Schema(name = "UserPasswordChangeReq", description = "用户密码修改请求")
public class UserPasswordChangeReq {

    @Schema(description = "旧密码", example = "123456")
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    @Schema(description = "新密码", example = "654321")
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 32, message = "新密码长度为 6-32 个字符")
    private String newPassword;
}

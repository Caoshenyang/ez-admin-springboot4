package com.ez.admin.dto.install.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 系统初始化请求对象
 * <p>
 * 用于创建超级管理员账号，简化为只包含必要字段
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-28
 */
@Data
@Schema(name = "InstallReq", description = "系统初始化请求")
public class InstallReq {

    @Schema(description = "超级管理员用户名", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度为 3-20 个字符")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    @Schema(description = "超级管理员密码", example = "admin123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度为 6-32 个字符")
    private String password;

    @Schema(description = "超级管理员昵称", example = "超级管理员", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "昵称不能为空")
    @Size(max = 30, message = "昵称长度不能超过 30 个字符")
    private String nickname;
}

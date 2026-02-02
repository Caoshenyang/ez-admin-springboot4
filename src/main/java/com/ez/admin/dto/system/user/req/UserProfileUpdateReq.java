package com.ez.admin.dto.system.user.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户个人信息修改请求对象
 *
 * @author ez-admin
 * @since 2026-01-27
 */
@Data
@Schema(name = "UserProfileUpdateReq", description = "用户个人信息修改请求")
public class UserProfileUpdateReq {

    @Schema(description = "昵称", example = "张三")
    @Size(max = 50, message = "昵称长度不能超过 50 个字符")
    private String nickname;

    @Schema(description = "邮箱", example = "zhangsan@example.com")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "手机号", example = "13800000000")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phoneNumber;

    @Schema(description = "性别（0=保密，1=男，2=女）", example = "0")
    private Integer gender;

    @Schema(description = "描述信息", example = "这是用户描述")
    @Size(max = 200, message = "描述长度不能超过 200 个字符")
    private String description;
}

package com.ez.admin.dto.user.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建用户请求对象
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Data
@Schema(name = "UserCreateReq", description = "创建用户请求")
public class UserCreateReq {

    @Schema(description = "用户名", example = "zhangsan")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度为 3-20 个字符")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    @Schema(description = "密码", example = "123456")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度为 6-32 个字符")
    private String password;

    @Schema(description = "昵称", example = "张三")
    @NotBlank(message = "昵称不能为空")
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

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "部门ID", example = "1")
    private Long deptId;

    @Schema(description = "角色ID列表", example = "[1, 2]")
    private java.util.List<Long> roleIds;

    @Schema(description = "描述信息", example = "这是用户描述")
    @Size(max = 200, message = "描述长度不能超过 200 个字符")
    private String description;
}

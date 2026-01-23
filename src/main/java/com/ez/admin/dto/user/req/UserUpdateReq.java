package com.ez.admin.dto.user.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新用户请求对象
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Data
@Schema(name = "UserUpdateReq", description = "更新用户请求")
public class UserUpdateReq {

    @Schema(description = "用户ID", example = "1")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

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

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "部门ID", example = "1")
    private Long deptId;

    @Schema(description = "状态（0=禁用，1=正常）", example = "1")
    private Integer status;

    @Schema(description = "角色ID列表", example = "[1, 2]")
    private java.util.List<Long> roleIds;

    @Schema(description = "描述信息", example = "这是用户描述")
    @Size(max = 200, message = "描述长度不能超过 200 个字符")
    private String description;
}

package com.ezadmin.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 类名: UserCreateDTO
 * 功能描述: 用户新建
 *
 * @author shenyang
 * @since 2025/3/19 14:01
 */
@Data
public class UserCreateDTO {

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "用户账号")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户邮箱")
    private String email;

    @Schema(description = "用户手机号码")
    private String phoneNumber;

    @Schema(description = "性别【0 保密 1 男 2 女】")
    private Integer gender;

    @Schema(description = "用户头像")
    private String avatar;


}

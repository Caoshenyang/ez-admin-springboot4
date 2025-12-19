package com.ezadmin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * 用户VO
 * </p>
 *
 * @author 曹申阳
 * @since 2025-12-17 10:40:08
 */
@Data
public class UserInfoVO {

    @Schema(description = "主键ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "权限信息")
    private List<String> permissions;

    @Schema(description = "路由信息")
    private List<MenuTreeVO> menus;

}

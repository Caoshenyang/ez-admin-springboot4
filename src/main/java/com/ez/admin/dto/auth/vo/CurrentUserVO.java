package com.ez.admin.dto.auth.vo;

import com.ez.admin.dto.system.menu.vo.MenuTreeVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 当前登录用户信息（视图对象）
 * <p>
 * 包含用户基本信息、角色、权限标识、菜单树等信息
 * 用于前端动态路由、动态菜单、权限控制
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Getter
@Builder
@Schema(name = "CurrentUserVO", description = "当前登录用户信息")
public class CurrentUserVO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phoneNumber;

    @Schema(description = "性别（0=保密 1=男 2=女）")
    private Integer gender;

    @Schema(description = "角色标识列表（如：[SUPER_ADMIN, ADMIN]）")
    private List<String> roleLabels;

    @Schema(description = "权限标识列表（用于前端按钮级权限控制）")
    private List<String> permissions;

    @Schema(description = "菜单树（用于前端动态路由和菜单渲染）")
    private List<MenuTreeVO> menus;
}

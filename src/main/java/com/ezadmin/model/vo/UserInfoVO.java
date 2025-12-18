package com.ezadmin.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
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

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（加密后）- 不返回
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 权限信息
     */
    private List<String> permissions;

    /**
     * 路由信息
     */
    private List<MenuTreeVO> menus;


}

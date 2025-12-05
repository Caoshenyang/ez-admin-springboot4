package com.ezadimn.modules.system.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 登录DTO
 * </p>
 *
 * @author 曹申阳
 * @since 2025-12-05 15:41:23
 */
@Data
public class LoginDTO{

    private String username;
    private String password;

}

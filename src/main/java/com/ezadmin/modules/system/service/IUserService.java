package com.ezadmin.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ezadmin.modules.system.entity.User;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author shenyang
 * @since 2025-12-17
 */
public interface IUserService extends IService<User> {

    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return 用户信息
     */
    User selectUserByUsername(String username);
}

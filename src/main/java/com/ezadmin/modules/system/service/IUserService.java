package com.ezadmin.modules.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ezadmin.model.query.UserQuery;
import com.ezadmin.model.vo.UserListVO;
import com.ezadmin.modules.system.entity.User;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author shenyang
 * @since 2025-12-18
 */
public interface IUserService extends IService<User> {

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户实体
     */
    User selectUserByUsername(String username);

    /**
     * 分页查询用户列表
     *
     * @param page   分页信息
     * @param search 查询参数
     */
    void findPage(Page<UserListVO> page, UserQuery search);
}

package com.ez.admin.system.service.impl;

import com.ez.admin.system.entity.User;
import com.ez.admin.system.mapper.UserMapper;
import com.ez.admin.system.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-19
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}

package com.ezadmin.modules.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezadmin.model.query.UserQuery;
import com.ezadmin.model.vo.UserListVO;
import com.ezadmin.modules.system.entity.User;
import com.ezadmin.modules.system.mapper.UserMapper;
import com.ezadmin.modules.system.service.IUserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author shenyang
 * @since 2025-12-18
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public User selectUserByUsername(String username) {
        return lambdaQuery()
            .select(User::getUserId, User::getUsername, User::getPassword, User::getNickname, User::getAvatar)
            .eq(User::getUsername, username).one();
    }

    @Override
    public void findPage(Page<UserListVO> page, UserQuery search) {
        baseMapper.findPage(page, search);
    }
}

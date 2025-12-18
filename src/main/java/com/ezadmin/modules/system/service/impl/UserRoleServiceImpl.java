package com.ezadmin.modules.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezadmin.modules.system.entity.UserRole;
import com.ezadmin.modules.system.mapper.UserRoleMapper;
import com.ezadmin.modules.system.service.IUserRoleService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户角色关联表 服务实现类
 * </p>
 *
 * @author shenyang
 * @since 2025-12-16
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {

}

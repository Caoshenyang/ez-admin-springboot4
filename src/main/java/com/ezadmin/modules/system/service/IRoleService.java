package com.ezadmin.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ezadmin.modules.system.entity.Role;

import java.util.List;

/**
 * <p>
 * 角色信息表 服务类
 * </p>
 *
 * @author shenyang
 * @since 2025-12-17
 */
public interface IRoleService extends IService<Role> {

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> selectRoleListByUserId(Long userId);
}

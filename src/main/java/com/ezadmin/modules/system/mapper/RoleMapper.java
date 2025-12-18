package com.ezadmin.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ezadmin.modules.system.entity.Role;

import java.util.List;

/**
 * <p>
 * 角色信息表 Mapper 接口
 * </p>
 *
 * @author shenyang
 * @since 2025-12-17
 */
public interface RoleMapper extends BaseMapper<Role> {

    List<Role> selectRoleListByUserId(Long userId);
}

package com.ez.admin.modules.system.service;

import com.ez.admin.dto.system.vo.RoleInfo;
import com.ez.admin.modules.system.entity.SysUserRoleRelation;
import com.ez.admin.modules.system.mapper.SysUserRoleRelationMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户角色关联表 服务实现类
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Service
public class SysUserRoleRelationService extends ServiceImpl<SysUserRoleRelationMapper, SysUserRoleRelation> {

    /**
     * 获取用户的角色信息列表
     *
     * @param userId 用户ID
     * @return 角色信息列表
     */
    public List<RoleInfo> getRoleInfoByUserId(Long userId) {
        return baseMapper.selectRoleInfoByUserId(userId);
    }
}

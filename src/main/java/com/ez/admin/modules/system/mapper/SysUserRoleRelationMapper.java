package com.ez.admin.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ez.admin.dto.system.vo.RoleInfo;
import com.ez.admin.modules.system.entity.SysUserRoleRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户角色关联表 Mapper 接口
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
public interface SysUserRoleRelationMapper extends BaseMapper<SysUserRoleRelation> {

    /**
     * 根据用户ID查询角色信息列表
     *
     * @param userId 用户ID
     * @return 角色信息列表
     */
    List<RoleInfo> selectRoleInfoByUserId(@Param("userId") Long userId);
}

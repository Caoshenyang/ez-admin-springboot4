package com.ez.admin.modules.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ez.admin.modules.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户信息表 Mapper 接口
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户（登录时使用）
     *
     * @param username 用户名
     * @return 用户实体，不存在则返回 null
     */
    default SysUser selectByUsername(String username) {
        return this.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                // 只查询未删除的用户
                .eq(SysUser::getIsDeleted, 0));
    }
}

package com.ez.admin.modules.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ez.admin.common.data.mapper.MapperHelper;
import com.ez.admin.common.core.constant.SystemConstants;
import com.ez.admin.common.model.model.PageQuery;
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
                .eq(SysUser::getIsDeleted, SystemConstants.NOT_DELETED));
    }

    /**
     * 统计未删除的用户数量
     *
     * @return 用户数量
     */
    default Long countActiveUsers() {
        return this.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getIsDeleted, SystemConstants.NOT_DELETED));
    }

    /**
     * 检查手机号是否存在
     *
     * @param phoneNumber 手机号
     * @return 是否存在
     */
    default boolean existsByPhoneNumber(String phoneNumber) {
        return this.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getPhoneNumber, phoneNumber)
                .eq(SysUser::getIsDeleted, SystemConstants.NOT_DELETED)) > 0;
    }

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @return 是否存在
     */
    default boolean existsByEmail(String email) {
        return this.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getEmail, email)
                .eq(SysUser::getIsDeleted, SystemConstants.NOT_DELETED)) > 0;
    }

    /**
     * 检查手机号是否被其他用户占用
     *
     * @param phoneNumber 手机号
     * @param excludeUserId 排除的用户ID
     * @return 是否存在
     */
    default boolean existsByPhoneNumberExclude(String phoneNumber, Long excludeUserId) {
        return this.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getPhoneNumber, phoneNumber)
                .ne(SysUser::getUserId, excludeUserId)
                .eq(SysUser::getIsDeleted, SystemConstants.NOT_DELETED)) > 0;
    }

    /**
     * 检查邮箱是否被其他用户占用
     *
     * @param email 邮箱
     * @param excludeUserId 排除的用户ID
     * @return 是否存在
     */
    default boolean existsByEmailExclude(String email, Long excludeUserId) {
        return this.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getEmail, email)
                .ne(SysUser::getUserId, excludeUserId)
                .eq(SysUser::getIsDeleted, SystemConstants.NOT_DELETED)) > 0;
    }

    /**
     * 分页查询用户列表
     * <p>
     * 查询策略：
     * 1. keyword：快捷模糊搜索（自动搜索标记为 keywordSearch=true 的字段）
     * 2. conditions：高级查询（前端控制任意字段组合）
     * </p>
     *
     * @param page  分页对象
     * @param query 查询条件
     * @return 分页结果
     */
    default Page<SysUser> selectUserPage(Page<SysUser> page, PageQuery query) {
        return MapperHelper.selectPage(this, page, query, SysUser.class);
    }
}

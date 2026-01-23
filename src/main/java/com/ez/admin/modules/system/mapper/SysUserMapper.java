package com.ez.admin.modules.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ez.admin.common.constant.SystemConstants;
import com.ez.admin.dto.common.Filter;
import com.ez.admin.dto.common.Operator;
import com.ez.admin.dto.common.UserField;
import com.ez.admin.dto.user.req.UserQueryReq;
import com.ez.admin.modules.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.util.StringUtils;

import java.util.List;

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
     * 支持固定条件查询和动态过滤条件组合
     * </p>
     *
     * @param page  分页对象
     * @param query 查询条件
     * @return 分页结果
     */
    default Page<SysUser> selectUserPage(Page<SysUser> page, UserQueryReq query) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>();

        if (query != null) {
            // 应用固定查询条件
            wrapper.like(StringUtils.hasText(query.getUsername()), SysUser::getUsername, query.getUsername())
                    .like(StringUtils.hasText(query.getNickname()), SysUser::getNickname, query.getNickname())
                    .eq(StringUtils.hasText(query.getPhoneNumber()), SysUser::getPhoneNumber, query.getPhoneNumber())
                    .eq(query.getStatus() != null, SysUser::getStatus, query.getStatus())
                    .eq(query.getDeptId() != null, SysUser::getDeptId, query.getDeptId())
                    .ge(StringUtils.hasText(query.getStartTime()), SysUser::getCreateTime, query.getStartTime())
                    .le(StringUtils.hasText(query.getEndTime()), SysUser::getCreateTime, query.getEndTime());

            // 应用动态过滤条件（前端控制）
            applyDynamicFilters(wrapper, query.getFilters());
        }

        return this.selectPage(page, wrapper);
    }

    /**
     * 应用动态过滤条件
     *
     * @param wrapper  LambdaQueryWrapper
     * @param filters  动态过滤条件列表
     */
    private default void applyDynamicFilters(LambdaQueryWrapper<SysUser> wrapper, List<Filter> filters) {
        if (filters == null || filters.isEmpty()) {
            return;
        }

        for (Filter filter : filters) {
            // 解析字段枚举
            UserField field = UserField.fromFieldName(filter.getField());
            if (field == null) {
                continue; // 忽略无效字段
            }

            // 解析操作符枚举
            Operator operator = Operator.fromOperator(filter.getOperator());
            if (operator == null) {
                continue; // 忽略无效操作符
            }

            // 应用条件
            field.apply(wrapper, operator, filter.getValue());
        }
    }
}


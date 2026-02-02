package com.ez.admin.modules.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ez.admin.common.data.mapper.MapperHelper;
import com.ez.admin.common.core.constant.SystemConstants;
import com.ez.admin.common.model.model.PageQuery;
import com.ez.admin.modules.system.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 角色信息表 Mapper 接口
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 检查角色名称是否存在
     *
     * @param roleName 角色名称
     * @return 是否存在
     */
    default boolean existsByRoleName(String roleName) {
        return this.selectCount(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleName, roleName)
                .eq(SysRole::getIsDeleted, SystemConstants.NOT_DELETED)) > 0;
    }

    /**
     * 检查角色标识是否存在
     *
     * @param roleLabel 角色标识
     * @return 是否存在
     */
    default boolean existsByRoleLabel(String roleLabel) {
        return this.selectCount(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleLabel, roleLabel)
                .eq(SysRole::getIsDeleted, SystemConstants.NOT_DELETED)) > 0;
    }

    /**
     * 检查角色名称是否被其他角色占用
     *
     * @param roleName      角色名称
     * @param excludeRoleId 排除的角色ID
     * @return 是否存在
     */
    default boolean existsByRoleNameExclude(String roleName, Long excludeRoleId) {
        return this.selectCount(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleName, roleName)
                .ne(SysRole::getRoleId, excludeRoleId)
                .eq(SysRole::getIsDeleted, SystemConstants.NOT_DELETED)) > 0;
    }

    /**
     * 检查角色标识是否被其他角色占用
     *
     * @param roleLabel     角色标识
     * @param excludeRoleId 排除的角色ID
     * @return 是否存在
     */
    default boolean existsByRoleLabelExclude(String roleLabel, Long excludeRoleId) {
        return this.selectCount(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleLabel, roleLabel)
                .ne(SysRole::getRoleId, excludeRoleId)
                .eq(SysRole::getIsDeleted, SystemConstants.NOT_DELETED)) > 0;
    }

    /**
     * 检查角色是否已分配给用户
     *
     * @param roleId 角色ID
     * @return 是否已分配
     */
    default boolean isAssignedToUsers(Long roleId) {
        // 注意：这里需要通过 SysUserRoleRelationMapper 来查询
        // 为了简化，我们暂时返回 false，实际使用时需要在 UserRoleRelationMapper 中添加方法
        return false;
    }

    /**
     * 分页查询角色列表
     *
     * @param page  分页对象
     * @param query 查询条件
     * @return 分页结果
     */
    default Page<SysRole> selectRolePage(Page<SysRole> page, PageQuery query) {
        return MapperHelper.selectPage(this, page, query, SysRole.class);
    }

    /**
     * 根据用户ID查询角色标识列表
     *
     * @param userId 用户ID
     * @return 角色标识列表
     */
    List<String> selectRoleLabelsByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 根据菜单ID列表查询权限标识列表
     *
     * @param menuIds 菜单ID列表
     * @return 权限标识列表
     */
    List<String> selectMenuPermsByIds(@Param("menuIds") List<Long> menuIds);

    /**
     * 根据 roleLabel 查询角色
     *
     * @param roleLabel 角色标识
     * @return 角色实体，不存在则返回 null
     */
    default SysRole selectByRoleLabel(String roleLabel) {
        return this.selectOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleLabel, roleLabel)
                .eq(SysRole::getIsDeleted, SystemConstants.NOT_DELETED));
    }

    /**
     * 根据角色标识查询菜单权限列表
     * <p>
     * 用于权限缓存，当缓存未命中时从数据库加载角色的菜单权限
     * </p>
     *
     * @param roleLabel 角色标识
     * @return 菜单权限列表
     */
    List<com.ez.admin.dto.system.menu.vo.MenuPermissionVO> selectMenuPermissionsByRoleLabel(@Param("roleLabel") String roleLabel);
}

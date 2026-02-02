package com.ez.admin.common.data.mapstruct;

import com.ez.admin.dto.auth.vo.LoginVO;
import com.ez.admin.dto.system.user.vo.RoleVO;
import com.ez.admin.dto.system.user.vo.UserDetailVO;
import com.ez.admin.dto.system.user.vo.UserListVO;
import com.ez.admin.modules.system.entity.SysRole;
import com.ez.admin.modules.system.entity.SysUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 用户对象转换器
 * <p>
 * 使用 MapStruct 自动生成转换实现类，简化 Entity 与 VO 之间的转换
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Mapper(componentModel = "spring")
public interface UserConverter {

    /**
     * 获取转换器实例（非 Spring 环境使用）
     */
    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

    /**
     * SysUser 转 LoginVO
     *
     * @param user 用户实体
     * @return 登录响应 VO
     */
    @Mapping(target = "token", ignore = true)
    LoginVO toLoginVO(SysUser user);

    /**
     * SysUser 转 UserListVO
     *
     * @param user 用户实体
     * @return 用户列表 VO
     */
    @Mapping(target = "deptName", ignore = true)
    UserListVO toListVO(SysUser user);

    /**
     * SysUser 列表转 UserListVO 列表
     *
     * @param users 用户实体列表
     * @return 用户列表 VO
     */
    List<UserListVO> toListVOList(List<SysUser> users);

    /**
     * SysUser 转 UserDetailVO（不包含角色）
     *
     * @param user 用户实体
     * @return 用户详情 VO
     */
    @Mapping(target = "deptName", ignore = true)
    @Mapping(target = "roles", ignore = true)
    UserDetailVO toDetailVO(SysUser user);

    /**
     * SysUser 转 UserDetailVO（包含角色）
     * <p>
     * 使用参数传递角色列表，避免转换后调用 set 方法
     * </p>
     *
     * @param user  用户实体
     * @param roles 角色 VO 列表
     * @return 用户详情 VO
     */
    @Mapping(target = "deptName", ignore = true)
    @Mapping(target = "roles", source = "roles")
    UserDetailVO toDetailVOWithRoles(SysUser user, List<RoleVO> roles);

    /**
     * SysRole 转 RoleVO
     *
     * @param role 角色实体
     * @return 角色 VO
     */
    RoleVO toRoleVO(SysRole role);

    /**
     * SysRole 列表转 RoleVO 列表
     *
     * @param roles 角色实体列表
     * @return 角色 VO 列表
     */
    List<RoleVO> toRoleVOList(List<SysRole> roles);
}

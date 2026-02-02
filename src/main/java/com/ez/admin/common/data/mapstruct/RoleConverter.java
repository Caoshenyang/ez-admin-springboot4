package com.ez.admin.common.data.mapstruct;

import com.ez.admin.dto.system.role.vo.RoleDetailVO;
import com.ez.admin.dto.system.role.vo.RoleListVO;
import com.ez.admin.modules.system.entity.SysRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 角色对象转换器
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Mapper(componentModel = "spring")
public interface RoleConverter {

    /**
     * 获取转换器实例（非 Spring 环境使用）
     */
    RoleConverter INSTANCE = Mappers.getMapper(RoleConverter.class);

    /**
     * SysRole 转 RoleListVO
     *
     * @param role 角色实体
     * @return 角色列表 VO
     */
    RoleListVO toListVO(SysRole role);

    /**
     * SysRole 列表转 RoleListVO 列表
     *
     * @param roles 角色实体列表
     * @return 角色 VO 列表
     */
    List<RoleListVO> toListVOList(List<SysRole> roles);

    /**
     * SysRole 转 RoleDetailVO（不包含菜单和部门）
     *
     * @param role 角色实体
     * @return 角色详情 VO
     */
    @Mapping(target = "menuIds", ignore = true)
    @Mapping(target = "deptIds", ignore = true)
    RoleDetailVO toDetailVO(SysRole role);

    /**
     * SysRole 转 RoleDetailVO（包含菜单和部门）
     *
     * @param role    角色实体
     * @param menuIds 菜单 ID 列表
     * @param deptIds 部门 ID 列表
     * @return 角色详情 VO
     */
    @Mapping(target = "menuIds", source = "menuIds")
    @Mapping(target = "deptIds", source = "deptIds")
    RoleDetailVO toDetailVOWithMenusAndDepts(SysRole role, List<Long> menuIds, List<Long> deptIds);
}

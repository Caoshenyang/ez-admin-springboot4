package com.ez.admin.common.mapstruct;

import com.ez.admin.dto.menu.vo.MenuTreeVO;
import com.ez.admin.dto.menu.vo.MenuVO;
import com.ez.admin.modules.system.entity.SysMenu;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 菜单对象转换器
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Mapper(componentModel = "spring")
public interface MenuConverter {

    /**
     * 获取转换器实例（非 Spring 环境使用）
     */
    MenuConverter INSTANCE = Mappers.getMapper(MenuConverter.class);

    /**
     * SysMenu 转 MenuVO（列表展示）
     *
     * @param menu 菜单实体
     * @return 菜单 VO
     */
    MenuVO toVO(SysMenu menu);

    /**
     * SysMenu 列表转 MenuVO 列表（列表展示）
     *
     * @param menus 菜单实体列表
     * @return 菜单 VO 列表
     */
    List<MenuVO> toVOList(List<SysMenu> menus);

    /**
     * SysMenu 转 MenuTreeVO（树形结构）
     *
     * @param menu 菜单实体
     * @return 菜单 TreeVO
     */
    MenuTreeVO toTreeVO(SysMenu menu);

    /**
     * SysMenu 列表转 MenuTreeVO 列表（树形结构）
     *
     * @param menus 菜单实体列表
     * @return 菜单 TreeVO 列表
     */
    List<MenuTreeVO> toTreeVOList(List<SysMenu> menus);
}

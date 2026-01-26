package com.ez.admin.common.mapstruct;

import com.ez.admin.dto.dept.vo.DeptDetailVO;
import com.ez.admin.dto.dept.vo.DeptTreeVO;
import com.ez.admin.modules.system.entity.SysDept;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 部门对象转换器
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Mapper(componentModel = "spring")
public interface DeptConverter {

    /**
     * 获取转换器实例（非 Spring 环境使用）
     */
    DeptConverter INSTANCE = Mappers.getMapper(DeptConverter.class);

    /**
     * SysDept 转 DeptDetailVO（详情）
     *
     * @param dept 部门实体
     * @return 部门详情 VO
     */
    DeptDetailVO toDetailVO(SysDept dept);

    /**
     * SysDept 列表转 DeptDetailVO 列表（详情）
     *
     * @param depts 部门实体列表
     * @return 部门详情 VO 列表
     */
    List<DeptDetailVO> toDetailVOList(List<SysDept> depts);

    /**
     * SysDept 转 DeptTreeVO（树形结构）
     *
     * @param dept 部门实体
     * @return 部门 TreeVO
     */
    DeptTreeVO toTreeVO(SysDept dept);

    /**
     * SysDept 列表转 DeptTreeVO 列表（树形结构）
     *
     * @param depts 部门实体列表
     * @return 部门 TreeVO 列表
     */
    List<DeptTreeVO> toTreeVOList(List<SysDept> depts);
}

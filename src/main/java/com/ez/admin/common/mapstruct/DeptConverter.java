package com.ez.admin.common.mapstruct;

import com.ez.admin.dto.dept.vo.DeptVO;
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
     * SysDept 转 DeptVO
     *
     * @param dept 部门实体
     * @return 部门 VO
     */
    DeptVO toVO(SysDept dept);

    /**
     * SysDept 列表转 DeptVO 列表
     *
     * @param depts 部门实体列表
     * @return 部门 VO 列表
     */
    List<DeptVO> toVOList(List<SysDept> depts);
}

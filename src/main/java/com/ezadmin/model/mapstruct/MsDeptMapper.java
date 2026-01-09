package com.ezadmin.model.mapstruct;

import com.ezadmin.model.dto.DeptCreateDTO;
import com.ezadmin.model.dto.DeptUpdateDTO;
import com.ezadmin.model.vo.DeptTreeVO;
import com.ezadmin.modules.system.entity.Dept;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * 类名: MsDeptMapper
 * 功能描述: 部门实体转化类
 *
 * @author shenyang
 * @since 2025/3/17 13:41
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MsDeptMapper {

    MsDeptMapper INSTANCE = Mappers.getMapper(MsDeptMapper.class);

    Dept deptCreateDTO2Dept(DeptCreateDTO deptCreateDTO);

    Dept deptUpdateDTO2Dept(DeptUpdateDTO deptUpdateDTO);

    DeptTreeVO dept2DeptTreeVO(Dept dept);
}

package com.ez.admin.common.data.mapstruct;

import com.ez.admin.dto.dict.vo.DictDataListVO;
import com.ez.admin.dto.dict.vo.DictTypeDetailVO;
import com.ez.admin.dto.dict.vo.DictTypeListVO;
import com.ez.admin.modules.system.entity.SysDictData;
import com.ez.admin.modules.system.entity.SysDictType;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 字典对象转换器
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Mapper(componentModel = "spring")
public interface DictConverter {

    /**
     * 获取转换器实例（非 Spring 环境使用）
     */
    DictConverter INSTANCE = Mappers.getMapper(DictConverter.class);

    // ==================== 字典类型 ====================

    /**
     * SysDictType 转 DictTypeListVO
     *
     * @param dictType 字典类型实体
     * @return 字典类型列表 VO
     */
    DictTypeListVO toListVO(SysDictType dictType);

    /**
     * SysDictType 列表转 DictTypeListVO 列表
     *
     * @param dictTypes 字典类型实体列表
     * @return 字典类型列表 VO
     */
    List<DictTypeListVO> toListVOList(List<SysDictType> dictTypes);

    /**
     * SysDictType 转 DictTypeDetailVO
     *
     * @param dictType 字典类型实体
     * @return 字典类型详情 VO
     */
    DictTypeDetailVO toDetailVO(SysDictType dictType);

    // ==================== 字典数据 ====================

    /**
     * SysDictData 转 DictDataListVO
     *
     * @param dictData 字典数据实体
     * @return 字典数据列表 VO
     */
    DictDataListVO toDataListVO(SysDictData dictData);

    /**
     * SysDictData 列表转 DictDataListVO 列表
     *
     * @param dictDataList 字典数据实体列表
     * @return 字典数据列表 VO
     */
    List<DictDataListVO> toDataListVOList(List<SysDictData> dictDataList);
}

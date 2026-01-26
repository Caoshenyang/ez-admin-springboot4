package com.ez.admin.service.dict;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ez.admin.common.exception.EzBusinessException;
import com.ez.admin.common.exception.ErrorCode;
import com.ez.admin.common.mapstruct.DictConverter;
import com.ez.admin.common.model.PageQuery;
import com.ez.admin.common.model.PageVO;
import com.ez.admin.dto.dict.req.*;
import com.ez.admin.dto.dict.vo.DictDataListVO;
import com.ez.admin.dto.dict.vo.DictTypeDetailVO;
import com.ez.admin.dto.dict.vo.DictTypeListVO;
import com.ez.admin.modules.system.entity.SysDictData;
import com.ez.admin.modules.system.entity.SysDictType;
import com.ez.admin.modules.system.mapper.SysDictDataMapper;
import com.ez.admin.modules.system.mapper.SysDictTypeMapper;
import com.ez.admin.modules.system.service.SysDictDataService;
import com.ez.admin.modules.system.service.SysDictTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 字典管理服务
 * <p>
 * 业务聚合层，组合原子服务实现字典管理的复杂业务逻辑
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DictService {

    private final SysDictTypeMapper dictTypeMapper;
    private final SysDictDataMapper dictDataMapper;
    private final SysDictTypeService sysDictTypeService;
    private final SysDictDataService sysDictDataService;
    private final DictConverter dictConverter;

    // ==================== 字典类型 ====================

    /**
     * 创建字典类型
     *
     * @param request 创建请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void createDictType(DictTypeCreateReq request) {
        // 1. 检查字典类型是否已存在
        if (dictTypeMapper.existsByDictType(request.getDictType())) {
            throw new EzBusinessException(ErrorCode.DICT_TYPE_ALREADY_EXISTS);
        }

        // 2. 创建字典类型
        SysDictType dictType = buildDictType(request);
        dictTypeMapper.insert(dictType);

        log.info("创建字典类型成功，字典名称：{}", request.getDictName());
    }

    /**
     * 更新字典类型
     *
     * @param request 更新请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateDictType(DictTypeUpdateReq request) {
        // 1. 检查字典类型是否存在
        SysDictType existDictType = dictTypeMapper.selectById(request.getDictId());
        if (existDictType == null) {
            throw new EzBusinessException(ErrorCode.DICT_TYPE_NOT_FOUND);
        }

        // 2. 检查字典类型是否被其他字典占用
        if (dictTypeMapper.existsByDictTypeExclude(request.getDictType(), request.getDictId())) {
            throw new EzBusinessException(ErrorCode.DICT_TYPE_ALREADY_EXISTS);
        }

        // 3. 更新字典类型信息
        SysDictType dictType = new SysDictType();
        dictType.setDictId(request.getDictId());
        dictType.setDictName(request.getDictName());
        dictType.setDictType(request.getDictType());
        dictType.setStatus(request.getStatus());
        dictType.setDescription(request.getDescription());
        dictTypeMapper.updateById(dictType);

        log.info("更新字典类型成功，字典ID：{}", request.getDictId());
    }

    /**
     * 删除字典类型
     *
     * @param dictId 字典ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictType(Long dictId) {
        // 1. 检查字典类型是否存在
        SysDictType dictType = dictTypeMapper.selectById(dictId);
        if (dictType == null) {
            throw new EzBusinessException(ErrorCode.DICT_TYPE_NOT_FOUND);
        }

        // 2. 删除字典类型（级联删除字典数据）
        sysDictTypeService.removeById(dictId);

        // 3. 删除该字典类型下的所有字典数据
        dictDataMapper.delete(new LambdaQueryWrapper<SysDictData>()
                .eq(SysDictData::getDictId, dictId));

        log.info("删除字典类型成功，字典ID：{}", dictId);
    }

    /**
     * 根据ID查询字典类型详情
     *
     * @param dictId 字典ID
     * @return 字典类型详情
     */
    public DictTypeDetailVO getDictTypeById(Long dictId) {
        SysDictType dictType = dictTypeMapper.selectById(dictId);
        if (dictType == null) {
            throw new EzBusinessException(ErrorCode.DICT_TYPE_NOT_FOUND);
        }

        return dictConverter.toDetailVO(dictType);
    }

    /**
     * 分页查询字典类型列表
     *
     * @param query 分页查询请求
     * @return 分页结果
     */
    public PageVO<DictTypeListVO> getDictTypePage(PageQuery query) {
        // 执行分页查询
        Page<SysDictType> result = dictTypeMapper.selectDictTypePage(query.toMpPage(), query);

        return PageVO.of(result, dictConverter::toListVO);
    }

    /**
     * 查询所有字典类型列表
     *
     * @return 字典类型列表
     */
    public List<DictTypeListVO> getDictTypeList() {
        List<SysDictType> dictTypes = dictTypeMapper.selectList(new LambdaQueryWrapper<SysDictType>()
                .orderByDesc(SysDictType::getCreateTime));
        return dictConverter.toListVOList(dictTypes);
    }

    // ==================== 字典数据 ====================

    /**
     * 创建字典数据
     *
     * @param request 创建请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void createDictData(DictDataCreateReq request) {
        // 1. 检查字典类型是否存在
        SysDictType dictType = dictTypeMapper.selectById(request.getDictId());
        if (dictType == null) {
            throw new EzBusinessException(ErrorCode.DICT_TYPE_NOT_FOUND);
        }

        // 2. 创建字典数据
        SysDictData dictData = buildDictData(request);
        dictDataMapper.insert(dictData);

        log.info("创建字典数据成功，字典标签：{}", request.getDictLabel());
    }

    /**
     * 更新字典数据
     *
     * @param request 更新请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateDictData(DictDataUpdateReq request) {
        // 1. 检查字典数据是否存在
        SysDictData existDictData = dictDataMapper.selectById(request.getDictDataId());
        if (existDictData == null) {
            throw new EzBusinessException(ErrorCode.DICT_DATA_NOT_FOUND);
        }

        // 2. 检查字典类型是否存在
        SysDictType dictType = dictTypeMapper.selectById(request.getDictId());
        if (dictType == null) {
            throw new EzBusinessException(ErrorCode.DICT_TYPE_NOT_FOUND);
        }

        // 3. 更新字典数据信息
        SysDictData dictData = new SysDictData();
        dictData.setDictDataId(request.getDictDataId());
        dictData.setDictId(request.getDictId());
        dictData.setDictLabel(request.getDictLabel());
        dictData.setDictValue(request.getDictValue());
        dictData.setDictSort(request.getDictSort());
        dictData.setListClass(request.getListClass());
        dictData.setIsDefault(request.getIsDefault());
        dictData.setStatus(request.getStatus());
        dictData.setDescription(request.getDescription());
        dictDataMapper.updateById(dictData);

        log.info("更新字典数据成功，字典数据ID：{}", request.getDictDataId());
    }

    /**
     * 删除字典数据
     *
     * @param dictDataId 字典数据ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictData(Long dictDataId) {
        // 1. 检查字典数据是否存在
        SysDictData dictData = dictDataMapper.selectById(dictDataId);
        if (dictData == null) {
            throw new EzBusinessException(ErrorCode.DICT_DATA_NOT_FOUND);
        }

        // 2. 逻辑删除字典数据
        sysDictDataService.removeById(dictDataId);

        log.info("删除字典数据成功，字典数据ID：{}", dictDataId);
    }

    /**
     * 根据字典类型ID查询字典数据列表
     *
     * @param dictId 字典类型ID
     * @return 字典数据列表
     */
    public List<DictDataListVO> getDictDataListByDictId(Long dictId) {
        List<SysDictData> dictDataList = dictDataMapper.selectList(new LambdaQueryWrapper<SysDictData>()
                .eq(SysDictData::getDictId, dictId)
                .orderByAsc(SysDictData::getDictSort));
        return dictConverter.toDataListVOList(dictDataList);
    }

    /**
     * 根据字典类型编码查询字典数据列表
     *
     * @param dictType 字典类型编码
     * @return 字典数据列表
     */
    public List<DictDataListVO> getDictDataListByDictType(String dictType) {
        // 1. 根据字典类型编码查询字典类型
        SysDictType dictTypeEnum = dictTypeMapper.selectOne(new LambdaQueryWrapper<SysDictType>()
                .eq(SysDictType::getDictType, dictType));

        if (dictTypeEnum == null) {
            throw new EzBusinessException(ErrorCode.DICT_TYPE_NOT_FOUND);
        }

        // 2. 根据字典类型ID查询字典数据
        return getDictDataListByDictId(dictTypeEnum.getDictId());
    }

    // ==================== 私有方法 ====================

    /**
     * 构建字典类型实体
     */
    private SysDictType buildDictType(DictTypeCreateReq request) {
        SysDictType dictType = new SysDictType();
        dictType.setDictName(request.getDictName());
        dictType.setDictType(request.getDictType());
        dictType.setStatus(request.getStatus());
        dictType.setDescription(request.getDescription());
        return dictType;
    }

    /**
     * 构建字典数据实体
     */
    private SysDictData buildDictData(DictDataCreateReq request) {
        SysDictData dictData = new SysDictData();
        dictData.setDictId(request.getDictId());
        dictData.setDictLabel(request.getDictLabel());
        dictData.setDictValue(request.getDictValue());
        dictData.setDictSort(request.getDictSort());
        dictData.setListClass(request.getListClass());
        dictData.setIsDefault(request.getIsDefault());
        dictData.setStatus(request.getStatus());
        dictData.setDescription(request.getDescription());
        return dictData;
    }
}

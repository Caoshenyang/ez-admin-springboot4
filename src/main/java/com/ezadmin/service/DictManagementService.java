package com.ezadmin.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ezadmin.common.response.page.PageQuery;
import com.ezadmin.common.response.page.PageVO;
import com.ezadmin.model.dto.DictDataCreateDTO;
import com.ezadmin.model.dto.DictDataUpdateDTO;
import com.ezadmin.model.dto.DictTypeCreateDTO;
import com.ezadmin.model.dto.DictTypeUpdateDTO;
import com.ezadmin.model.query.DictDataQuery;
import com.ezadmin.model.query.DictTypeQuery;
import com.ezadmin.modules.system.entity.DictData;
import com.ezadmin.modules.system.entity.DictType;
import com.ezadmin.modules.system.service.IDictDataService;
import com.ezadmin.modules.system.service.IDictTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 字典管理
 */
@Service
@RequiredArgsConstructor
public class DictManagementService {

    private final IDictTypeService dictTypeService;
    private final IDictDataService dictDataService;

    /**
     * 字典类型分页
     */
    public PageVO<DictType> pageType(PageQuery<DictTypeQuery> query) {
        Page<DictType> page = query.toMpPage();
        LambdaQueryWrapper<DictType> wrapper = new LambdaQueryWrapper<>();
        DictTypeQuery search = query.getSearch();
        if (search != null) {
            wrapper = search.buildWrapper();
            if (search.getStatus() != null) {
                wrapper.eq(DictType::getStatus, search.getStatus());
            }
        }
        dictTypeService.page(page, wrapper);
        return PageVO.of(page, DictType.class);
    }

    /**
     * 字典数据分页
     */
    public PageVO<DictData> pageData(PageQuery<DictDataQuery> query) {
        Page<DictData> page = query.toMpPage();
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        DictDataQuery search = query.getSearch();
        if (search != null) {
            wrapper = search.buildWrapper();
            if (search.getDictId() != null) {
                wrapper.eq(DictData::getDictId, search.getDictId());
            }
            if (search.getStatus() != null) {
                wrapper.eq(DictData::getStatus, search.getStatus());
            }
        }
        dictDataService.page(page, wrapper);
        return PageVO.of(page, DictData.class);
    }

    public DictType typeDetail(Long dictId) {
        return dictTypeService.getById(dictId);
    }

    public DictData dataDetail(Long dictDataId) {
        return dictDataService.getById(dictDataId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createType(DictTypeCreateDTO dto) {
        DictType dictType = BeanUtil.copyProperties(dto, DictType.class);
        dictTypeService.save(dictType);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateType(DictTypeUpdateDTO dto) {
        DictType dictType = BeanUtil.copyProperties(dto, DictType.class);
        dictTypeService.updateById(dictType);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteType(Long dictId) {
        // 先删除类型下数据
        dictDataService.remove(new LambdaQueryWrapper<DictData>().eq(DictData::getDictId, dictId));
        dictTypeService.removeById(dictId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createData(DictDataCreateDTO dto) {
        DictData dictData = BeanUtil.copyProperties(dto, DictData.class);
        dictDataService.save(dictData);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateData(DictDataUpdateDTO dto) {
        DictData dictData = BeanUtil.copyProperties(dto, DictData.class);
        dictDataService.updateById(dictData);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteData(Long dictDataId) {
        dictDataService.removeById(dictDataId);
    }

    public List<DictType> listAllTypes() {
        return dictTypeService.lambdaQuery().orderByAsc(DictType::getDictName).list();
    }

    public List<DictData> listDataByType(Long dictId) {
        return dictDataService.lambdaQuery()
            .eq(DictData::getDictId, dictId)
            .orderByAsc(DictData::getDictSort)
            .list();
    }
}

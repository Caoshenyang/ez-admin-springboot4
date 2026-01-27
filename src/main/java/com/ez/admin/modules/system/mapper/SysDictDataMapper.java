package com.ez.admin.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ez.admin.common.data.mapper.MapperHelper;
import com.ez.admin.common.model.model.PageQuery;
import com.ez.admin.modules.system.entity.SysDictData;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 字典详情表 Mapper 接口
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Mapper
public interface SysDictDataMapper extends BaseMapper<SysDictData> {

    /**
     * 分页查询字典数据列表
     *
     * @param page  分页对象
     * @param query 查询条件
     * @return 分页结果
     */
    default Page<SysDictData> selectDictDataPage(Page<SysDictData> page, PageQuery query) {
        return MapperHelper.selectPage(this, page, query, SysDictData.class);
    }
}

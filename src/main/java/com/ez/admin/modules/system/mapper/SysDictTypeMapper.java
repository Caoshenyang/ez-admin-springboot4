package com.ez.admin.modules.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ez.admin.common.condition.QueryConditionSupport;
import com.ez.admin.common.constant.SystemConstants;
import com.ez.admin.common.model.PageQuery;
import com.ez.admin.dto.dict.metadata.DictTypeQueryMetadata;
import com.ez.admin.modules.system.entity.SysDictType;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 字典类型表 Mapper 接口
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Mapper
public interface SysDictTypeMapper extends BaseMapper<SysDictType> {

    /**
     * 主动触发查询元数据注册
     * <p>
     * 解决类懒加载问题：确保 DictTypeQueryMetadata 的静态块在 Mapper 使用前执行
     * </p>
     */
    DictTypeQueryMetadata TRIGGER_METADATA_REGISTRATION = DictTypeQueryMetadata.DICT_NAME;

    /**
     * 检查字典类型是否存在
     *
     * @param dictType 字典类型
     * @return 是否存在
     */
    default boolean existsByDictType(String dictType) {
        return this.selectCount(new LambdaQueryWrapper<SysDictType>()
                .eq(SysDictType::getDictType, dictType)) > 0;
    }

    /**
     * 检查字典类型是否被其他字典占用
     *
     * @param dictType     字典类型
     * @param excludeDictId 排除的字典ID
     * @return 是否存在
     */
    default boolean existsByDictTypeExclude(String dictType, Long excludeDictId) {
        return this.selectCount(new LambdaQueryWrapper<SysDictType>()
                .eq(SysDictType::getDictType, dictType)
                .ne(SysDictType::getDictId, excludeDictId)) > 0;
    }

    /**
     * 分页查询字典类型列表
     *
     * @param page  分页对象
     * @param query 查询条件
     * @return 分页结果
     */
    default Page<SysDictType> selectDictTypePage(Page<SysDictType> page, PageQuery query) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();

        if (query != null) {
            // 1. 快捷模糊搜索：自动搜索标记为 keywordSearch=true 的字段
            QueryConditionSupport.applyKeywordSearch(wrapper, query.getKeyword(), SysDictType.class);

            // 2. 高级查询：动态应用 conditions
            if (query.getConditions() != null && !query.getConditions().isEmpty()) {
                QueryConditionSupport.applyConditions(wrapper, query.getConditions(), SysDictType.class);
            }
        }

        return this.selectPage(page, wrapper);
    }
}

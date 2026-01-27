package com.ez.admin.common.data.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ez.admin.common.data.metadata.condition.QueryConditionSupport;
import com.ez.admin.common.model.model.PageQuery;

/**
 * Mapper 通用工具类
 * <p>
 * 提供通用的分页查询逻辑，减少各 Mapper 中的重复代码
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-27
 */
public class MapperHelper {

    /**
     * 通用分页查询方法
     * <p>
     * 支持：
     * 1. 快捷模糊搜索（keyword）：自动搜索标记为 @KeywordSearch 的字段
     * 2. 高级查询（conditions）：支持任意字段、操作符、值的组合查询
     * </p>
     *
     * @param mapper Mapper 实例
     * @param page   分页对象
     * @param query  查询条件
     * @param <PO>   实体类型
     * @return 分页结果
     */
    public static <PO> Page<PO> selectPage(BaseMapper<PO> mapper, Page<PO> page, PageQuery query) {
        @SuppressWarnings("unchecked")
        LambdaQueryWrapper<PO> wrapper = new LambdaQueryWrapper<>();

        if (query != null) {
            // 1. 快捷模糊搜索：自动搜索标记为 keywordSearch=true 的字段
            QueryConditionSupport.applyKeywordSearch(wrapper, query.getKeyword(), (Class<PO>) page.getRecords().getClass().getComponentType());

            // 2. 高级查询：动态应用 conditions
            if (query.getConditions() != null && !query.getConditions().isEmpty()) {
                QueryConditionSupport.applyConditions(wrapper, query.getConditions(), (Class<PO>) page.getRecords().getClass().getComponentType());
            }
        }

        return mapper.selectPage(page, wrapper);
    }

    /**
     * 通用分页查询方法（重载，需要传入实体 Class）
     * <p>
     * 此方法避免了通过 Page 对象获取 Class 类型的问题，调用更直观
     * </p>
     *
     * @param mapper     Mapper 实例
     * @param page       分页对象
     * @param query      查询条件
     * @param entityClass 实体类型
     * @param <PO>       实体类型
     * @return 分页结果
     */
    public static <PO> Page<PO> selectPage(BaseMapper<PO> mapper, Page<PO> page, PageQuery query, Class<PO> entityClass) {
        LambdaQueryWrapper<PO> wrapper = new LambdaQueryWrapper<>();

        if (query != null) {
            // 1. 快捷模糊搜索：自动搜索标记为 keywordSearch=true 的字段
            QueryConditionSupport.applyKeywordSearch(wrapper, query.getKeyword(), entityClass);

            // 2. 高级查询：动态应用 conditions
            if (query.getConditions() != null && !query.getConditions().isEmpty()) {
                QueryConditionSupport.applyConditions(wrapper, query.getConditions(), entityClass);
            }
        }

        return mapper.selectPage(page, wrapper);
    }
}

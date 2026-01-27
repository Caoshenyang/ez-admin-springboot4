package com.ez.admin.modules.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ez.admin.common.data.metadata.condition.QueryConditionSupport;
import com.ez.admin.common.core.constant.SystemConstants;
import com.ez.admin.common.model.model.PageQuery;
import com.ez.admin.modules.system.entity.SysOperationLog;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

/**
 * 操作日志表 Mapper 接口
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Mapper
public interface SysOperationLogMapper extends BaseMapper<SysOperationLog> {

    /**
     * 分页查询操作日志列表
     *
     * @param page  分页对象
     * @param query 查询条件
     * @return 分页结果
     */
    default Page<SysOperationLog> selectLogPage(Page<SysOperationLog> page, PageQuery query) {
        LambdaQueryWrapper<SysOperationLog> wrapper = new LambdaQueryWrapper<>();

        if (query != null) {
            // 快捷模糊搜索：搜索用户名、模块、操作描述
            if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
                wrapper.like(SysOperationLog::getUsername, query.getKeyword())
                        .or().like(SysOperationLog::getModule, query.getKeyword())
                        .or().like(SysOperationLog::getDescription, query.getKeyword());
            }

            // 高级查询：动态应用 conditions
            if (query.getConditions() != null && !query.getConditions().isEmpty()) {
                QueryConditionSupport.applyConditions(wrapper, query.getConditions(), SysOperationLog.class);
            }
        }

        // 按创建时间倒序
        wrapper.orderByDesc(SysOperationLog::getCreateTime);

        return this.selectPage(page, wrapper);
    }

    /**
     * 清理指定天数之前的日志
     *
     * @param days 天数
     * @return 删除条数
     */
    default Integer deleteLogsBeforeDays(int days) {
        LocalDateTime dateTime = LocalDateTime.now().minusDays(days);
        return this.delete(new LambdaQueryWrapper<SysOperationLog>()
                .lt(SysOperationLog::getCreateTime, dateTime));
    }
}

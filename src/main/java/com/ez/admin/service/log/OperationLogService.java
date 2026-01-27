package com.ez.admin.service.log;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ez.admin.common.data.mapstruct.OperationLogConverter;
import com.ez.admin.common.model.model.PageQuery;
import com.ez.admin.common.model.model.PageVO;
import com.ez.admin.dto.log.vo.OperationLogListVO;
import com.ez.admin.modules.system.entity.SysOperationLog;
import com.ez.admin.modules.system.mapper.SysOperationLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 操作日志服务
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OperationLogService {

    private final SysOperationLogMapper operationLogMapper;
    private final OperationLogConverter operationLogConverter;

    /**
     * 分页查询操作日志
     *
     * @param query 查询条件
     * @return 分页结果
     */
    public PageVO<OperationLogListVO> getOperationLogPage(PageQuery query) {
        Page<SysOperationLog> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<SysOperationLog> logPage = operationLogMapper.selectLogPage(page, query);

        List<OperationLogListVO> voList = operationLogConverter.toListVOList(logPage.getRecords());

        return PageVO.<OperationLogListVO>builder()
                .records(voList)
                .total(logPage.getTotal())
                .pageNum(logPage.getCurrent())
                .pageSize(logPage.getSize())
                .build();
    }

    /**
     * 清理指定天数之前的日志
     *
     * @param days 天数
     * @return 删除条数
     */
    public Integer cleanLogs(int days) {
        log.info("开始清理 {} 天之前的操作日志", days);
        Integer count = operationLogMapper.deleteLogsBeforeDays(days);
        log.info("操作日志清理完成，删除条数：{}", count);
        return count;
    }
}

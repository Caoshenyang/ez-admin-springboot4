package com.ez.admin.api.log;

import com.ez.admin.common.annotation.OperationLog;
import com.ez.admin.common.model.PageQuery;
import com.ez.admin.common.model.PageVO;
import com.ez.admin.common.model.R;
import com.ez.admin.common.permission.SaCheckPermission;
import com.ez.admin.dto.log.vo.OperationLogListVO;
import com.ez.admin.service.log.OperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志控制器
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Slf4j
@RestController
@RequestMapping("/log/operation")
@RequiredArgsConstructor
@Tag(name = "操作日志管理", description = "操作日志查询和管理")
public class OperationLogController {

    private final OperationLogService operationLogService;

    @PostMapping("/page")
    @SaCheckPermission("system:log:query")
    @Operation(summary = "分页查询操作日志", description = "分页查询操作日志列表，支持多条件筛选")
    public R<PageVO<OperationLogListVO>> getPage(@RequestBody PageQuery query) {
        PageVO<OperationLogListVO> page = operationLogService.getOperationLogPage(query);
        return R.success(page);
    }

    @DeleteMapping("/clean/{days}")
    @SaCheckPermission("system:log:delete")
    @OperationLog(module = "操作日志", operation = "清理", description = "清理历史操作日志")
    @Operation(summary = "清理历史日志", description = "清理指定天数之前的操作日志")
    public R<Integer> cleanLogs(
            @Parameter(description = "保留天数，如 30 表示保留最近 30 天的日志")
            @PathVariable Integer days) {
        Integer count = operationLogService.cleanLogs(days);
        return R.success(count, "清理完成，共删除 " + count + " 条日志");
    }
}

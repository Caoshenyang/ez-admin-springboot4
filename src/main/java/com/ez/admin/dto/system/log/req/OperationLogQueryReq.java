package com.ez.admin.dto.system.log.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志查询请求对象
 *
 * @author ez-admin
 * @since 2026-01-27
 */
@Data
@Schema(name = "OperationLogQueryReq", description = "操作日志查询请求对象")
public class OperationLogQueryReq {

    @Schema(description = "模块名称", example = "用户管理")
    private String module;

    @Schema(description = "操作类型", example = "创建")
    private String operation;

    @Schema(description = "操作用户名", example = "admin")
    private String username;

    @Schema(description = "请求方法", example = "POST")
    private String requestMethod;

    @Schema(description = "操作状态（0=失败，1=成功）", example = "1")
    private Integer status;

    @Schema(description = "开始时间", example = "2026-01-01 00:00:00")
    private LocalDateTime startTime;

    @Schema(description = "结束时间", example = "2026-01-31 23:59:59")
    private LocalDateTime endTime;

    @Schema(description = "关键词（操作描述或请求URL模糊查询）", example = "用户")
    private String keyword;

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;
}

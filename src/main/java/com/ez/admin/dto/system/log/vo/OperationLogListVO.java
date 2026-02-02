package com.ez.admin.dto.system.log.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 操作日志列表 VO
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Getter
@Builder
@Schema(name = "OperationLogListVO", description = "操作日志列表响应")
public class OperationLogListVO {

    @Schema(description = "日志ID")
    private Long logId;

    @Schema(description = "模块名称")
    private String module;

    @Schema(description = "操作类型")
    private String operation;

    @Schema(description = "操作描述")
    private String description;

    @Schema(description = "操作用户ID")
    private Long userId;

    @Schema(description = "操作用户名")
    private String username;

    @Schema(description = "请求方法")
    private String requestMethod;

    @Schema(description = "请求URL")
    private String requestUrl;

    @Schema(description = "请求IP")
    private String requestIp;

    @Schema(description = "执行时长（毫秒）")
    private Long executeTime;

    @Schema(description = "操作状态【0 失败 1 成功】")
    private Integer status;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}

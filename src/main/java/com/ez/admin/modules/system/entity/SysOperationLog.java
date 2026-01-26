package com.ez.admin.modules.system.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志表
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Getter
@Setter
@ToString
@TableName("ez_admin_sys_operation_log")
@Schema(name = "SysOperationLog", description = "操作日志表")
public class SysOperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "日志ID")
    @TableId(value = "log_id", type = IdType.ASSIGN_ID)
    private Long logId;

    @TableField("module")
    @Schema(description = "模块名称")
    private String module;

    @TableField("operation")
    @Schema(description = "操作类型")
    private String operation;

    @TableField("description")
    @Schema(description = "操作描述")
    private String description;

    @TableField("user_id")
    @Schema(description = "操作用户ID")
    private Long userId;

    @TableField("username")
    @Schema(description = "操作用户名")
    private String username;

    @TableField("request_method")
    @Schema(description = "请求方法")
    private String requestMethod;

    @TableField("request_url")
    @Schema(description = "请求URL")
    private String requestUrl;

    @TableField("request_ip")
    @Schema(description = "请求IP")
    private String requestIp;

    @TableField("request_params")
    @Schema(description = "请求参数")
    private String requestParams;

    @TableField("execute_time")
    @Schema(description = "执行时长（毫秒）")
    private Long executeTime;

    @TableField("status")
    @Schema(description = "操作状态【0 失败 1 成功】")
    private Integer status;

    @TableField("error_msg")
    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

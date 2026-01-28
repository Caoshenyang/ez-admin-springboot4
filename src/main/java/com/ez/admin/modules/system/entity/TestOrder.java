package com.ez.admin.modules.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 测试订单实体
 * <p>
 * 用于验证数据权限功能的测试表
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-28
 */
@Data
@TableName("ez_admin_test_order")
@Schema(description = "测试订单实体")
public class TestOrder {

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "订单金额")
    private BigDecimal orderAmount;

    @Schema(description = "订单状态【0 待支付 1 已支付 2 已完成 3 已取消】")
    private Integer orderStatus;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "备注")
    private String remark;

    @TableField("created_by")
    @Schema(description = "创建人ID（用于数据权限过滤）")
    private Long createdBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField("update_by")
    @Schema(description = "更新人ID")
    private Long updateBy;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @TableField("is_deleted")
    @Schema(description = "是否删除【0 正常 1 已删除】")
    private Integer isDeleted;
}

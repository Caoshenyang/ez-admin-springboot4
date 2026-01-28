package com.ez.admin.dto.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单列表 VO
 *
 * @author ez-admin
 * @since 2026-01-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "订单列表 VO")
public class OrderListVO {

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

    @Schema(description = "创建人ID")
    private Long createdBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}

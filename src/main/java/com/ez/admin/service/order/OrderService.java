package com.ez.admin.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ez.admin.common.core.constant.SystemConstants;
import com.ez.admin.dto.order.vo.OrderListVO;
import com.ez.admin.modules.system.entity.TestOrder;
import com.ez.admin.modules.system.mapper.TestOrderMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 测试订单服务
 * <p>
 * 用于验证数据权限功能
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final TestOrderMapper orderMapper;

    /**
     * 分页查询订单列表
     * <p>
     * 注意：此方法会自动应用数据权限过滤
     * </p>
     *
     * @param pageNum  当前页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    public Page<OrderListVO> getOrderPage(Integer pageNum, Integer pageSize) {
        log.info("查询订单列表，页码：{}，每页条数：{}", pageNum, pageSize);

        // 构建分页对象
        Page<TestOrder> page = Page.of(pageNum, pageSize);
        page.addOrder(OrderItem.desc("create_time"));

        // 查询订单列表（数据权限拦截器会自动添加过滤条件）
        Page<TestOrder> resultPage = orderMapper.selectPage(page, null);

        // 转换为 VO
        Page<OrderListVO> voPage = new Page<>();
        voPage.setCurrent(resultPage.getCurrent());
        voPage.setSize(resultPage.getSize());
        voPage.setTotal(resultPage.getTotal());
        voPage.setPages(resultPage.getPages());
        voPage.setRecords(convertToVOList(resultPage.getRecords()));

        log.info("查询订单列表成功，共查询到 {} 条数据", resultPage.getTotal());

        return voPage;
    }

    /**
     * 查询所有订单列表
     * <p>
     * 注意：此方法会自动应用数据权限过滤
     * </p>
     *
     * @return 订单列表
     */
    public List<OrderListVO> getOrderList() {
        log.info("查询所有订单列表（带数据权限过滤）");

        // 查询订单列表（数据权限拦截器会自动添加过滤条件）
        List<TestOrder> orders = orderMapper.selectList(
                new LambdaQueryWrapper<TestOrder>()
                        .eq(TestOrder::getIsDeleted, SystemConstants.NOT_DELETED)
                        .orderByDesc(TestOrder::getCreateTime)
        );

        log.info("查询订单列表成功，共查询到 {} 条数据", orders.size());

        return convertToVOList(orders);
    }

    /**
     * 创建测试订单
     *
     * @param orderAmount  订单金额
     * @param customerName 客户名称
     * @param createdBy    创建人ID
     * @return 订单ID
     */
    public Long createOrder(Double orderAmount, String customerName, Long createdBy) {
        TestOrder order = new TestOrder();
        order.setOrderId(System.currentTimeMillis());
        order.setOrderNo("ORD" + System.currentTimeMillis());
        order.setOrderAmount(java.math.BigDecimal.valueOf(orderAmount));
        order.setOrderStatus(1); // 已支付
        order.setCustomerName(customerName);
        order.setRemark("测试订单");
        order.setCreatedBy(createdBy);
        order.setCreateTime(java.time.LocalDateTime.now());
        order.setUpdateBy(createdBy);
        order.setUpdateTime(java.time.LocalDateTime.now());
        order.setIsDeleted(SystemConstants.NOT_DELETED);

        orderMapper.insert(order);

        log.info("创建测试订单成功：订单ID={}，订单号={}，金额={}，创建人={}",
                order.getOrderId(), order.getOrderNo(), order.getOrderAmount(), createdBy);

        return order.getOrderId();
    }

    /**
     * 转换为 VO 列表
     *
     * @param orders 订单实体列表
     * @return VO 列表
     */
    private List<OrderListVO> convertToVOList(List<TestOrder> orders) {
        return orders.stream()
                .map(order -> OrderListVO.builder()
                        .orderId(order.getOrderId())
                        .orderNo(order.getOrderNo())
                        .orderAmount(order.getOrderAmount())
                        .orderStatus(order.getOrderStatus())
                        .customerName(order.getCustomerName())
                        .remark(order.getRemark())
                        .createdBy(order.getCreatedBy())
                        .createTime(order.getCreateTime())
                        .build())
                .toList();
    }
}

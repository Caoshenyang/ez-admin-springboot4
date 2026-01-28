package com.ez.admin.api.order;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ez.admin.common.model.model.R;
import com.ez.admin.dto.order.vo.OrderListVO;
import com.ez.admin.service.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测试订单控制器
 * <p>
 * 用于验证数据权限功能
 * </p>
 * <p>
 * 测试说明：
 * <ul>
 *   <li>登录用户的数据权限范围会影响能看到的订单数量</li>
 *   <li>数据权限作用在 created_by 字段上</li>
 *   <li>通过查询订单列表，可以验证数据权限是否生效</li>
 * </ul>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-28
 */
@Slf4j
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Tag(name = "测试订单管理", description = "用于验证数据权限功能的测试接口")
public class OrderController {

    private final OrderService orderService;

    /**
     * 分页查询订单列表
     * <p>
     * 注意：此接口会自动应用数据权限过滤
     * </p>
     *
     * @param pageNum  当前页码（默认 1）
     * @param pageSize 每页条数（默认 10）
     * @return 分页结果
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询订单列表", description = "会根据当前用户的数据权限范围自动过滤数据")
    public R<Page<OrderListVO>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<OrderListVO> page = orderService.getOrderPage(pageNum, pageSize);
        log.info("分页查询订单列表成功，共 {} 条数据", page.getTotal());
        return R.success(page);
    }

    /**
     * 查询所有订单列表
     * <p>
     * 注意：此接口会自动应用数据权限过滤
     * </p>
     *
     * @return 订单列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询所有订单列表", description = "会根据当前用户的数据权限范围自动过滤数据")
    public R<List<OrderListVO>> list() {
        List<OrderListVO> list = orderService.getOrderList();
        log.info("查询订单列表成功，共 {} 条数据", list.size());
        return R.success(list);
    }

    /**
     * 创建测试订单
     * <p>
     * 用于创建测试数据，验证数据权限功能
     * </p>
     *
     * @param orderAmount  订单金额
     * @param customerName 客户名称
     * @return 订单ID
     */
    @PostMapping("/create")
    @Operation(summary = "创建测试订单", description = "用于创建测试数据")
    public R<Long> create(
            @RequestParam Double orderAmount,
            @RequestParam String customerName) {
        // 从 Sa-Token 获取当前登录用户ID
        Long userId = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
        Long orderId = orderService.createOrder(orderAmount, customerName, userId);
        log.info("创建测试订单成功，订单ID：{}", orderId);
        return R.success(orderId);
    }
}

package com.ez.admin.common.model;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

/**
 * 分页查询对象
 * <p>
 * 统一分页查询请求，封装分页参数、排序条件和查询条件
 * </p>
 * <p>
 * 使用示例：
 * <pre>{@code
 * // Controller 中
 * @PostMapping("/page")
 * public R<PageVO<UserListVO>> page(@RequestBody PageQuery<UserQueryReq> query) {
 *     return R.success(userService.getPage(query));
 * }
 *
 * // Service 中
 * public PageVO<UserListVO> getPage(PageQuery<UserQueryReq> query) {
 *     Page<SysUser> page = userMapper.selectPage(query.toMpPage(), buildWrapper(query.getSearch()));
 *     ...
 * }
 * }</pre>
 * </p>
 *
 * @param <T> 查询条件类型
 * @author ez-admin
 * @since 2026-01-23
 */
@Data
public class PageQuery<T> {

    /**
     * 当前页码（从 1 开始）
     */
    private Integer current = 1;

    /**
     * 每页条数
     */
    private Integer size = 10;

    /**
     * 排序字段列表，支持多字段排序
     */
    private List<OrderItem> orders;

    /**
     * 查询条件对象
     */
    private T search;

    /**
     * 转换为 MyBatis-Plus 的 Page 对象
     * <p>
     * 如果未指定排序，默认按创建时间倒序
     * </p>
     *
     * @param <PO> 实体类型
     * @return Page<PO>
     */
    public <PO> Page<PO> toMpPage() {
        Page<PO> page = Page.of(current, size);
        if (orders != null && !orders.isEmpty()) {
            page.addOrder(orders);
        } else {
            // 默认按创建时间倒序
            page.addOrder(OrderItem.desc("create_time"));
        }
        return page;
    }
}

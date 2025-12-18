package com.ezadmin.common.response.page;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * 分页查询对象
 * </p>
 *
 * @author shenyang
 * @since 2024-10-16 16:24:56
 */
@Data
public class PageQuery<T> {


    /**
     * 当前页码，默认值为1
     */
    private Integer pageNum = 1;

    /**
     * 每页显示条数，默认值为10
     */
    private Integer pageSize = 10;
    /**
     * 排序对象，支持多字段排序
     */
    private List<OrderItem> orderItems;
    /**
     * 查询对象
     */
    private T search;


    /**
     * 将当前对象转换为 MybatisPlus 分页对象
     *
     * @param <PO> PO类型
     * @return Page<PO>
     */
    public <PO> Page<PO> toMpPage() {
        Page<PO> page = Page.of(pageNum, pageSize);
        if (orderItems != null && !orderItems.isEmpty()) {
            page.addOrder(orderItems);
        } else {
            // 如果不传默认根据创建时间倒序
            page.addOrder(OrderItem.desc("create_time"));
        }
        return page;
    }

}

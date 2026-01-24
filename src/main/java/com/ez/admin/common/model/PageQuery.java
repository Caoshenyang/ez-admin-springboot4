package com.ez.admin.common.model;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ez.admin.dto.common.Filter;
import lombok.Data;

import java.util.List;

/**
 * 通用分页查询对象
 * <p>
 * 统一分页查询请求，封装分页参数、排序条件和查询条件
 * </p>
 * <p>
 * 设计理念：
 * <ul>
 *   <li>keyword：快捷模糊搜索，适用于大部分场景（如用户名/昵称/手机号）</li>
 *   <li>filters：高级查询，前端控制任意字段组合（如 status=1, deptId=10）</li>
 * </ul>
 * </p>
 * <p>
 * 使用示例：
 * <pre>{@code
 * // Controller 中
 * @PostMapping("/page")
 * public R<PageVO<UserListVO>> page(@RequestBody PageQuery query) {
 *     return R.success(userService.getPage(query));
 * }
 *
 * // Service 中
 * public PageVO<UserListVO> getPage(PageQuery query) {
 *     Page<SysUser> page = userMapper.selectUserPage(query.toMpPage(), query);
 *     ...
 * }
 * }</pre>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Data
public class PageQuery {

    /**
     * 当前页码（从 1 开始）
     */
    private Integer pageNum = 1;

    /**
     * 每页条数
     */
    private Integer pageSize = 10;

    /**
     * 排序字段列表，支持多字段排序
     */
    private List<OrderItem> orders;

    /**
     * 快捷搜索关键词（模糊匹配）
     * <p>
     * 具体搜索字段由各 Mapper 自行定义
     * 例如：用户模块 → username/nickname/phoneNumber
     * </p>
     */
    private String keyword;

    /**
     * 高级查询条件列表（前端控制）
     * <p>
     * 支持任意字段、操作符、值的组合查询
     * 例如：[{"field":"STATUS","operator":"EQ","value":"1"}]
     * </p>
     */
    private List<Filter> filters;

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
        Page<PO> page = Page.of(pageNum, pageSize);
        if (orders != null && !orders.isEmpty()) {
            page.addOrder(orders);
        } else {
            // 默认按创建时间倒序
            page.addOrder(OrderItem.desc("create_time"));
        }
        return page;
    }
}

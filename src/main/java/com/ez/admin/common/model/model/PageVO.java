package com.ez.admin.common.model.model;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分页响应对象
 *
 * @param <T> 数据类型
 * @author ez-admin
 * @since 2026-01-23
 */
@Getter
@Builder
@Accessors(chain = true)
@Schema(name = "PageVO", description = "分页响应")
public class PageVO<T> {

    @Schema(description = "当前页码")
    private Long pageNum;

    @Schema(description = "每页条数")
    private Long pageSize;

    @Schema(description = "总记录数")
    private Long total;

    @Schema(description = "总页数")
    private Long pages;

    @Schema(description = "数据列表")
    private List<T> records;

    /**
     * 从 MyBatis-Plus 的 Page 对象构建分页响应
     *
     * @param page    MyBatis-Plus 分页对象
     * @param records 转换后的数据列表（通常是 Entity 转换后的 VO 列表）
     * @param <T>     数据类型
     * @return 分页响应对象
     */
    public static <T> PageVO<T> of(Page<?> page, List<T> records) {
        return PageVO.<T>builder()
                .pageNum(page.getCurrent())
                .pageSize(page.getSize())
                .total(page.getTotal())
                .pages(page.getPages())
                .records(records)
                .build();
    }

    /**
     * 从 MyBatis-Plus 的 Page 对象构建分页响应，并自动转换数据
     *
     * @param page     MyBatis-Plus 分页对象
     * @param function 数据转换函数（例如 Entity -> VO）
     * @param <E>      原始数据类型（通常是 Entity）
     * @param <T>      目标数据类型（通常是 VO）
     * @return 分页响应对象
     */
    public static <E, T> PageVO<T> of(Page<E> page, Function<E, T> function) {
        List<T> records = page.getRecords().stream()
                .map(function)
                .collect(Collectors.toList());

        return PageVO.<T>builder()
                .pageNum(page.getCurrent())
                .pageSize(page.getSize())
                .total(page.getTotal())
                .pages(page.getPages())
                .records(records)
                .build();
    }
}

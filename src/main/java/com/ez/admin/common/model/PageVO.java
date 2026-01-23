package com.ez.admin.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;

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
    private Long current;

    @Schema(description = "每页条数")
    private Long size;

    @Schema(description = "总记录数")
    private Long total;

    @Schema(description = "总页数")
    private Long pages;

    @Schema(description = "数据列表")
    private List<T> records;
}

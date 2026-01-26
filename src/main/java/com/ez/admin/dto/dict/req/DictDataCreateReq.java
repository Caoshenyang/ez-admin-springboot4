package com.ez.admin.dto.dict.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建字典数据请求对象
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Data
@Schema(name = "DictDataCreateReq", description = "创建字典数据请求")
public class DictDataCreateReq {

    @Schema(description = "字典类型ID", example = "1")
    @NotNull(message = "字典类型ID不能为空")
    private Long dictId;

    @Schema(description = "字典标签", example = "正常")
    @NotBlank(message = "字典标签不能为空")
    @Size(max = 100, message = "字典标签长度不能超过 100 个字符")
    private String dictLabel;

    @Schema(description = "字典键值", example = "1")
    @NotBlank(message = "字典键值不能为空")
    @Size(max = 100, message = "字典键值长度不能超过 100 个字符")
    private String dictValue;

    @Schema(description = "字典排序", example = "1")
    @NotNull(message = "字典排序不能为空")
    private Integer dictSort;

    @Schema(description = "表格回显样式", example = "default")
    @Size(max = 100, message = "表格回显样式长度不能超过 100 个字符")
    private String listClass;

    @Schema(description = "是否默认【0 否 1 是】", example = "0")
    private Integer isDefault;

    @Schema(description = "状态【0 停用 1 正常】", example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "描述信息", example = "正常状态")
    @Size(max = 200, message = "描述长度不能超过 200 个字符")
    private String description;
}

package com.ez.admin.dto.dict.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 字典数据批量删除请求对象
 *
 * @author ez-admin
 * @since 2026-01-27
 */
@Data
@Schema(name = "DictDataBatchDeleteReq", description = "字典数据批量删除请求")
public class DictDataBatchDeleteReq {

    @Schema(description = "字典数据ID列表", example = "[1, 2, 3]")
    @NotEmpty(message = "字典数据ID列表不能为空")
    private List<Long> dictDataIds;
}

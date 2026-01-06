package com.ezadmin.model.query;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.ezadmin.common.response.page.BaseQuery;
import com.ezadmin.modules.system.entity.DictData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 字典数据查询
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DictDataQuery extends BaseQuery<DictData> {

    @Schema(description = "字典类型ID")
    private Long dictId;

    @Schema(description = "状态【0 停用 1 正常】")
    private Integer status;

    @Override
    public List<SFunction<DictData, String>> getKeywordSearchFields() {
        return List.of(DictData::getDictLabel, DictData::getDictValue, DictData::getDescription);
    }
}

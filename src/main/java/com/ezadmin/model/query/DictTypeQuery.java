package com.ezadmin.model.query;

import com.ezadmin.common.response.page.BaseQuery;
import com.ezadmin.modules.system.entity.DictType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 字典类型查询
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DictTypeQuery extends BaseQuery<DictType> {

    @Schema(description = "状态【0 停用 1 正常】")
    private Integer status;

    @Override
    public List<com.baomidou.mybatisplus.core.toolkit.support.SFunction<DictType, String>> getKeywordSearchFields() {
        return List.of(DictType::getDictName, DictType::getDictType, DictType::getDescription);
    }
}

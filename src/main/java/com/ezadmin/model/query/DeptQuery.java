package com.ezadmin.model.query;

import com.ezadmin.common.response.page.BaseQuery;
import com.ezadmin.modules.system.entity.Dept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 部门查询
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DeptQuery extends BaseQuery<Dept> {

    @Schema(description = "状态【0 停用 1 正常】")
    private Integer status;

    @Override
    public List<com.baomidou.mybatisplus.core.toolkit.support.SFunction<Dept, String>> getKeywordSearchFields() {
        return List.of(Dept::getDeptName, Dept::getAncestors, Dept::getDescription);
    }
}

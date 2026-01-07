package com.ezadmin.model.query;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.ezadmin.common.response.page.BaseQuery;
import com.ezadmin.modules.system.entity.Dept;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 部门查询
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DeptQuery extends BaseQuery<Dept> {

    @Override
    public List<SFunction<Dept, String>> getKeywordSearchFields() {
        return List.of(Dept::getDeptName);
    }
}

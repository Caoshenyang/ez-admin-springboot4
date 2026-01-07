package com.ezadmin.model.query;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.ezadmin.common.response.page.BaseQuery;
import com.ezadmin.modules.system.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 角色查询
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleQuery extends BaseQuery<Role> {

    @Schema(description = "状态【0 停用 1 正常】")
    private Integer status;

    @Override
    public List<SFunction<Role, String>> getKeywordSearchFields() {
        return List.of(Role::getRoleName, Role::getRoleLabel, Role::getDescription);
    }
}

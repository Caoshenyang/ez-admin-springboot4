package com.ezadmin.model.query;

import com.ezadmin.common.response.page.BaseQuery;
import com.ezadmin.modules.system.entity.Menu;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 菜单查询
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MenuQuery extends BaseQuery<Menu> {

    @Schema(description = "状态【0 停用 1 正常】")
    private Integer status;

    @Override
    public List<com.baomidou.mybatisplus.core.toolkit.support.SFunction<Menu, String>> getKeywordSearchFields() {
        return List.of(Menu::getMenuName, Menu::getMenuLabel, Menu::getMenuPerm, Menu::getRouteName);
    }
}

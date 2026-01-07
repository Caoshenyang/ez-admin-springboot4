package com.ezadmin.model.dto;

import lombok.Data;

/**
 * 部门父节点树查询DTO
 */
@Data
public class DeptParentTreeDTO {

    /**
     * 排除的部门ID（用于上级部门选择时排除自己及子节点）
     */
    private Long excludeId;
}

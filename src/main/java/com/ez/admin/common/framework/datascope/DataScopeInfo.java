package com.ez.admin.common.framework.datascope;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 数据权限信息
 * <p>
 * 封装当前登录用户的数据权限相关信息，用于生成数据权限 SQL 条件
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataScopeInfo {

    /**
     * 数据权限范围
     * <ul>
     *   <li>1 - 仅本人数据权限</li>
     *   <li>2 - 本部门数据权限</li>
     *   <li>3 - 本部门及以下数据权限</li>
     *   <li>4 - 自定义数据权限</li>
     *   <li>5 - 全部数据权限</li>
     * </ul>
     */
    private Integer dataScope;

    /**
     * 当前用户ID
     */
    private Long userId;

    /**
     * 当前用户部门ID
     */
    private Long deptId;

    /**
     * 当前部门的祖先路径（格式：/1/2/）
     */
    private String ancestors;

    /**
     * 自定义数据权限的部门ID列表（当 dataScope = 4 时使用）
     */
    private List<Long> customDeptIds;
}

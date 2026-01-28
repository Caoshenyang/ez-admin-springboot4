package com.ez.admin.common.framework.datascope;

import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static com.ez.admin.common.core.constant.SystemConstants.*;

/**
 * 用户数据权限处理器
 * <p>
 * 根据当前用户的数据权限范围，自动在 SQL 中添加数据权限过滤条件
 * </p>
 * <p>
 * 数据权限说明：
 * <ul>
 *   <li>1 - 仅本人数据权限：只能查看自己创建的数据</li>
 *   <li>2 - 本部门数据权限：只能查看本部门人员创建的数据</li>
 *   <li>3 - 本部门及以下数据权限：可以查看本部门及子部门人员创建的数据</li>
 *   <li>4 - 自定义数据权限：可以查看指定部门人员创建的数据</li>
 *   <li>5 - 全部数据权限：可以查看所有数据</li>
 * </ul>
 * </p>
 * <p>
 * 使用说明：
 * <pre>{@code
 * // 1. 在 MyBatis-Plus 配置中注册拦截器
 * @Bean
 * public MybatisPlusInterceptor mybatisPlusInterceptor(UserDataPermissionHandler handler) {
 *     MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
 *     interceptor.addInnerInterceptor(new DataPermissionInterceptor(handler));
 *     return interceptor;
 * }
 *
 * // 2. 在登录时设置数据权限信息
 * DataScopeContext.setDataScopeInfo(dataScopeInfo);
 *
 * // 3. 查询时会自动添加数据权限条件
 * List<Order> orders = orderMapper.selectList(null);
 * // 实际 SQL: SELECT * FROM ez_admin_order WHERE created_by IN (...)
 * }</pre>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-28
 */
@Slf4j
@Component
public class UserDataPermissionHandler implements MultiDataPermissionHandler {

    /**
     * 需要进行数据权限过滤的表名白名单
     * <p>
     * 只有在白名单中的表才会进行数据权限过滤
     * </p>
     * <p>
     * 注意：表名必须是数据库表名，不是实体类名
     * </p>
     */
    private static final List<String> DATA_PERMISSION_TABLES = List.of(
            // 测试订单表（用于验证数据权限功能）
            "ez_admin_test_order"
            // 后续添加新表时，在此处注册表名
            // 例如："ez_admin_contract", "ez_admin_customer" 等
    );

    /**
     * 创建人字段名
     */
    private static final String CREATED_BY = "created_by";

    /**
     * 获取数据权限 SQL 片段
     * <p>
     * 根据当前用户的数据权限范围，生成对应的 SQL 条件表达式
     * </p>
     *
     * @param table               表对象
     * @param where               原有的 WHERE 条件
     * @param mappedStatementId   Mapper 方法全限定名
     * @return SQL 条件表达式，如果不需要过滤则返回 null
     */
    @Override
    public Expression getSqlSegment(Table table, Expression where, String mappedStatementId) {
        // 1. 获取数据权限信息
        DataScopeInfo info = DataScopeContext.getDataScopeInfo();
        if (info == null) {
            log.debug("数据权限信息未设置，跳过权限过滤");
            return null;
        }

        // 2. 检查表是否在白名单中
        String tableName = table.getName();
        if (!DATA_PERMISSION_TABLES.contains(tableName)) {
            log.debug("表 {} 不在数据权限白名单中，跳过权限过滤", tableName);
            return null;
        }

        // 3. 根据数据权限范围生成 SQL 片段
        String sqlSegment = buildSqlSegment(info, tableName);

        if (sqlSegment == null) {
            log.debug("数据权限范围为【全部数据】，不添加过滤条件");
            return null;
        }

        // 4. 解析 SQL 片段为表达式
        try {
            Expression sqlExpression = CCJSqlParserUtil.parseCondExpression(sqlSegment);
            log.debug("数据权限 SQL 片段生成成功: 表={}, 条件={}", tableName, sqlExpression);

            // 5. 将数据权限条件与原有条件合并
            if (where != null) {
                return new AndExpression(where, sqlExpression);
            } else {
                return sqlExpression;
            }
        } catch (Exception e) {
            log.error("数据权限 SQL 片段解析失败: 表={}, SQL片段={}", tableName, sqlSegment, e);
            return null;
        }
    }

    /**
     * 根据数据权限范围构建 SQL 片段
     *
     * @param info     数据权限信息
     * @param tableName 表名
     * @return SQL 片段，如果不需要过滤则返回 null
     */
    private String buildSqlSegment(DataScopeInfo info, String tableName) {
        Integer dataScope = info.getDataScope();
        Long userId = info.getUserId();
        Long deptId = info.getDeptId();
        String ancestors = info.getAncestors();
        List<Long> customDeptIds = info.getCustomDeptIds();

        return switch (dataScope) {
            case DATA_SCOPE_SELF -> // 仅本人数据权限
                    buildSelfCondition(userId);

            case DATA_SCOPE_DEPT -> // 本部门数据权限
                    buildDeptCondition(deptId);

            case DATA_SCOPE_DEPT_AND_CHILD -> // 本部门及以下数据权限
                    buildDeptAndChildCondition(deptId, ancestors);

            case DATA_SCOPE_CUSTOM -> // 自定义数据权限
                    buildCustomCondition(customDeptIds);

            case DATA_SCOPE_ALL -> // 全部数据权限
                    null;

            default -> {
                log.warn("未知的数据权限范围: {}", dataScope);
                yield null;
            }
        };
    }

    /**
     * 构建仅本人数据的 SQL 条件
     * <p>
     * SQL: created_by = 当前用户ID
     * </p>
     *
     * @param userId 当前用户ID
     * @return SQL 条件
     */
    private String buildSelfCondition(Long userId) {
        return CREATED_BY + " = " + userId;
    }

    /**
     * 构建本部门数据的 SQL 条件
     * <p>
     * SQL: created_by IN (SELECT user_id FROM ez_admin_sys_user WHERE dept_id = 部门ID)
     * </p>
     *
     * @param deptId 部门ID
     * @return SQL 条件
     */
    private String buildDeptCondition(Long deptId) {
        return CREATED_BY + " IN (SELECT user_id FROM ez_admin_sys_user WHERE dept_id = " + deptId + ")";
    }

    /**
     * 构建本部门及以下数据的 SQL 条件
     * <p>
     * SQL: created_by IN (SELECT user_id FROM ez_admin_sys_user WHERE dept_id IN
     *      (SELECT dept_id FROM ez_admin_sys_dept WHERE ancestors LIKE '祖先路径/部门ID/%' OR dept_id = 部门ID))
     * </p>
     *
     * @param deptId    部门ID
     * @param ancestors 祖先路径
     * @return SQL 条件
     */
    private String buildDeptAndChildCondition(Long deptId, String ancestors) {
        String likePattern = ancestors + deptId + "/%";
        return CREATED_BY + " IN (SELECT user_id FROM ez_admin_sys_user WHERE dept_id IN " +
                "(SELECT dept_id FROM ez_admin_sys_dept WHERE ancestors LIKE '" + likePattern + "' OR dept_id = " + deptId + "))";
    }

    /**
     * 构建自定义数据权限的 SQL 条件
     * <p>
     * SQL: created_by IN (SELECT user_id FROM ez_admin_sys_user WHERE dept_id IN (部门ID列表))
     * </p>
     *
     * @param customDeptIds 自定义部门ID列表
     * @return SQL 条件
     */
    private String buildCustomCondition(List<Long> customDeptIds) {
        if (customDeptIds == null || customDeptIds.isEmpty()) {
            log.warn("自定义数据权限的部门ID列表为空，返回空结果");
            return "1 = 0"; // 没有权限，返回空结果
        }

        String deptIdsStr = customDeptIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

        return CREATED_BY + " IN (SELECT user_id FROM ez_admin_sys_user WHERE dept_id IN (" + deptIdsStr + "))";
    }
}

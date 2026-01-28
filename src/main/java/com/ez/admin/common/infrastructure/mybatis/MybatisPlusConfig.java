package com.ez.admin.common.infrastructure.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.ez.admin.common.framework.datascope.UserDataPermissionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置类
 * <p>
 * 配置 MyBatis-Plus 常用插件：
 * <ul>
 *   <li>分页插件：支持多种数据库的分页查询</li>
 *   <li>防止全表更新和删除插件：避免误操作导致数据丢失</li>
 *   <li>数据权限插件：根据用户角色自动过滤数据</li>
 * </ul>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 配置 MyBatis-Plus 拦截器
     * <p>
     * 添加多个插件到拦截器链中，按顺序执行。
     * 注意：插件顺序很重要，建议顺序如下：
     * 1. 分页插件
     * 2. 数据权限插件
     * 3. 防止全表更新和删除插件
     * </p>
     *
     * @param dataPermissionHandler 数据权限处理器
     * @return MybatisPlusInterceptor 实例
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(UserDataPermissionHandler dataPermissionHandler) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 1. 分页插件
        // 支持多种数据库：MySQL、PostgreSQL、Oracle、SQL Server 等
        // 使用：Page<T> page = new Page<>(current, size);
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.POSTGRE_SQL);
        // 设置单页分页条数限制（可选，防止恶意查询大量数据）
        paginationInterceptor.setMaxLimit(1000L);
        // 分页溢出处理：false=返回空数据，true=自动查询最后一页
        // API 接口建议设为 false，让调用方感知页码越界错误
        paginationInterceptor.setOverflow(false);
        interceptor.addInnerInterceptor(paginationInterceptor);

        // 2. 数据权限插件
        // 根据当前登录用户的角色和数据权限范围，自动在 SQL 中添加数据过滤条件
        // 只对白名单中的表生效（在 UserDataPermissionHandler 中配置）
        interceptor.addInnerInterceptor(new DataPermissionInterceptor(dataPermissionHandler));

        // 3. 防止全表更新和删除插件
        // 阻止恶意的全表更新或删除操作（没有 WHERE 条件的 UPDATE/DELETE）
        // 一旦检测到全表操作，直接抛出异常，防止数据误删
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        return interceptor;
    }
}

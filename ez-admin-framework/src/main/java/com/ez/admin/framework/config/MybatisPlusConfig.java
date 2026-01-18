package com.ez.admin.framework.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置类
 * <p>
 * 配置 MyBatis-Plus 的核心插件，包括分页插件和防全表更新删除插件。
 * <ul>
 *   <li>分页插件：自动拦截相关 SQL 并进行分页处理，配合 Page<T> 使用</li>
 *   <li>防全表更新删除插件：防止恶意的全表更新和删除操作，增强数据库操作安全性</li>
 * </ul>
 *
 * @author ez-admin
 */
@Configuration
@MapperScan("com.ez.admin.**.mapper")
public class MybatisPlusConfig {

    /**
     * 配置 MyBatis-Plus 拦截器链
     * <p>
     * 按顺序添加以下插件：
     * <ol>
     *   <li>防全表更新删除插件（BlockAttackInnerInterceptor）：拦截并阻止没有 WHERE 条件的 UPDATE 和 DELETE 语句，
     *       防止误操作导致整表数据被修改或删除</li>
     *   <li>分页插件（PaginationInnerInterceptor）：用于支持分页查询功能。
     *       当使用 Page<T> 对象作为 Mapper 方法参数时，分页插件会自动：
     *       1. 统计总记录数（执行 COUNT 查询）
     *       2. 查询当前页数据（执行带 LIMIT 的查询）
     *       3. 将结果封装回 Page<T> 对象</li>
     * </ol>
     * <p>
     * 注意事项：
     * - 如果配置多个插件，分页插件必须放在最后添加
     * - 多数据源场景下可以不指定 DbType，单数据源建议明确指定以优化性能
     *
     * @return MybatisPlusInterceptor 拦截器实例
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加防全表更新删除插件
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        // 添加分页插件，指定数据库类型为 MySQL（必须放在最后）
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

}

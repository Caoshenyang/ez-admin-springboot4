package com.ez.admin.common.config;

import com.ez.admin.common.metadata.QueryMetadataProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * 查询元数据扫描配置
 * <p>
 * 自动扫描所有 QueryMetadataProvider Bean 并注册元数据
 * </p>
 * <p>
 * 使用方式：
 * 在各模块的 dto.{module}.metadata 包下创建 Provider：
 * <pre>{@code
 * @Component
 * public class UserQueryMetadataProvider implements QueryMetadataProvider<SysUser> {
 *
 *     @Override
 *     public Class<SysUser> getEntityClass() {
 *         return SysUser.class;
 *     }
 *
 *     @Override
 *     @PostConstruct
 *     public void registerMetadata() {
 *         QueryMetadataBuilder.create(SysUser.class)
 *             .field("username", FieldType.STRING, "用户账号")
 *                 .keywordSearch()
 *                 .operators(Operator.EQ, Operator.LIKE)
 *                 .column(SysUser::getUsername)
 *                 .add()
 *             .register();
 *     }
 * }
 * }</pre>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Slf4j
@Configuration
public class MetadataScannerConfiguration {

    /**
     * 应用启动后输出扫描结果
     * <p>
     * 注意：实际的元数据注册由各 Provider 的 @PostConstruct 自动完成
     * </p>
     */
    @PostConstruct
    public void scanAndLogMetadata() {
        log.info("=========================================== ");
        log.info("查询元数据扫描配置已启动");
        log.info("各模块的 QueryMetadataProvider 将自动注册元数据");
        log.info("=========================================== ");
    }
}

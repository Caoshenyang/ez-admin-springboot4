package com.ez.admin.dto.role.metadata;

import com.ez.admin.common.enums.FieldType;
import com.ez.admin.common.enums.Operator;
import com.ez.admin.common.metadata.QueryMetadataBuilder;
import com.ez.admin.common.metadata.QueryMetadataProvider;
import com.ez.admin.modules.system.entity.SysRole;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 角色查询元数据提供者
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Slf4j
@Component
public class RoleQueryMetadataProvider implements QueryMetadataProvider<SysRole> {

    @Override
    public Class<SysRole> getEntityClass() {
        return SysRole.class;
    }

    @Override
    @PostConstruct
    public void registerMetadata() {
        log.info("注册角色查询元数据...");

        QueryMetadataBuilder.create(SysRole.class)
                // 角色名称
                .field("roleName", FieldType.STRING, "角色名称")
                .keywordSearch()
                .operators(Operator.EQ, Operator.LIKE)
                .column(SysRole::getRoleName)
                .add()
                // 角色权限字符
                .field("roleLabel", FieldType.STRING, "角色权限字符")
                .keywordSearch()
                .operators(Operator.EQ, Operator.LIKE)
                .column(SysRole::getRoleLabel)
                .add()
                // 数据范围
                .field("dataScope", FieldType.INTEGER, "数据范围")
                .operators(Operator.EQ, Operator.IN)
                .column(SysRole::getDataScope)
                .dictType("sys_data_scope")
                .add()
                // 角色状态
                .field("status", FieldType.INTEGER, "角色状态")
                .operators(Operator.EQ, Operator.IN)
                .column(SysRole::getStatus)
                .dictType("sys_common_status")
                .add()
                .register();
    }
}

package com.ez.admin.dto.system.role.metadata;

import com.ez.admin.common.core.enums.FieldType;
import com.ez.admin.common.core.enums.Operator;
import com.ez.admin.common.data.metadata.metadata.QueryMetadataBuilder;
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
public class RoleQueryMetadataProvider {

    @PostConstruct
    public void registerMetadata() {
        log.info("注册角色查询元数据...");

        QueryMetadataBuilder.create(SysRole.class)
                .description("角色")
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

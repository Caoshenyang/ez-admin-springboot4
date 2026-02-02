package com.ez.admin.dto.system.dict.metadata;

import com.ez.admin.common.core.enums.FieldType;
import com.ez.admin.common.core.enums.Operator;
import com.ez.admin.common.data.metadata.metadata.QueryMetadataBuilder;
import com.ez.admin.modules.system.entity.SysDictType;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 字典类型查询元数据提供者
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Slf4j
@Component
public class DictTypeQueryMetadataProvider {

    @PostConstruct
    public void registerMetadata() {
        log.info("注册字典类型查询元数据...");

        QueryMetadataBuilder.create(SysDictType.class)
                .description("字典类型")
                // 字典名称
                .field("dictName", FieldType.STRING, "字典名称")
                .keywordSearch()
                .operators(Operator.EQ, Operator.LIKE)
                .column(SysDictType::getDictName)
                .add()
                // 字典类型
                .field("dictType", FieldType.STRING, "字典类型")
                .keywordSearch()
                .operators(Operator.EQ, Operator.LIKE)
                .column(SysDictType::getDictType)
                .add()
                // 状态
                .field("status", FieldType.INTEGER, "状态")
                .operators(Operator.EQ, Operator.IN)
                .column(SysDictType::getStatus)
                .dictType("sys_common_status")
                .add()
                .register();
    }
}

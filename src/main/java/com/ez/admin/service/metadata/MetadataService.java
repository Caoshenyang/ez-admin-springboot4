package com.ez.admin.service.metadata;

import com.ez.admin.common.condition.FieldConfig;
import com.ez.admin.common.condition.QueryConditionSupport;
import com.ez.admin.common.enums.Operator;
import com.ez.admin.common.exception.ErrorCode;
import com.ez.admin.common.exception.EzBusinessException;
import com.ez.admin.common.metadata.FieldMetadata;
import com.ez.admin.common.metadata.QueryMetadataVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 元数据服务
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Slf4j
@Service
public class MetadataService {

    /**
     * 获取资源查询元数据
     *
     * @param resource 资源名称（如 user、role、menu）
     * @return 查询元数据
     */
    public QueryMetadataVO getMetadata(String resource) {
        log.debug("获取元数据请求，资源：{}", resource);

        // 根据资源名称找到对应的实体类
        Class<?> entityClass = getEntityClassByResource(resource);
        if (entityClass == null) {
            log.warn("元数据不存在：{}", resource);
            throw new EzBusinessException(ErrorCode.NOT_FOUND, "不存在的资源: " + resource);
        }

        // 从 QueryConditionSupport 获取已注册的字段配置
        Map<String, FieldConfig<?>> fieldConfigs = QueryConditionSupport.getFieldConfigs(entityClass);
        if (fieldConfigs == null || fieldConfigs.isEmpty()) {
            log.warn("资源元数据未注册：{}", resource);
            throw new EzBusinessException(ErrorCode.NOT_FOUND, "资源元数据未注册: " + resource);
        }

        // 转换为 FieldMetadata 列表
        List<FieldMetadata> fields = new ArrayList<>();
        for (FieldConfig<?> config : fieldConfigs.values()) {
            fields.add(convertToFieldMetadata(config));
        }

        // 从 QueryConditionSupport 获取资源描述
        String description = QueryConditionSupport.getDescription(entityClass);

        // 构建 VO
        QueryMetadataVO vo = QueryMetadataVO.builder()
                .resource(resource)
                .description(description)
                .fields(fields)
                .build();

        log.debug("返回元数据，资源：{}，字段数量：{}", resource, vo.getFields().size());
        return vo;
    }

    /**
     * 获取所有已注册的资源列表
     *
     * @return 资源名称列表
     */
    public List<String> getRegisteredResources() {
        List<Class<?>> entityClasses = QueryConditionSupport.getRegisteredEntityClasses();
        List<String> resources = new ArrayList<>();
        for (Class<?> entityClass : entityClasses) {
            resources.add(convertEntityClassToResource(entityClass));
        }
        return resources;
    }

    /**
     * 将实体类转换为资源名称
     *
     * @param entityClass 实体类
     * @return 资源名称
     */
    private String convertEntityClassToResource(Class<?> entityClass) {
        return entityClass.getSimpleName()
                .replace("Sys", "")
                .toLowerCase();
    }

    /**
     * 根据资源名称获取实体类
     *
     * @param resource 资源名称
     * @return 实体类，如果不存在返回 null
     */
    private Class<?> getEntityClassByResource(String resource) {
        for (Class<?> entityClass : QueryConditionSupport.getRegisteredEntityClasses()) {
            String className = convertEntityClassToResource(entityClass);
            if (className.equalsIgnoreCase(resource)) {
                return entityClass;
            }
        }
        return null;
    }

    /**
     * 转换 FieldConfig 为 FieldMetadata
     *
     * @param config 字段配置
     * @return 字段元数据
     */
    private FieldMetadata convertToFieldMetadata(FieldConfig<?> config) {
        return FieldMetadata.builder()
                .field(config.getFieldCode())
                .type(config.getType().name())
                .description(config.getFieldCode()) // TODO: 从 Provider 中传入描述
                .operators(convertOperators(config.getOperators()))
                .required(false)
                .example(null)
                .dictType(config.getDictType())
                .build();
    }

    /**
     * 转换操作符枚举为字符串列表
     *
     * @param operators 操作符数组
     * @return 操作符字符串列表
     */
    private List<String> convertOperators(Operator[] operators) {
        List<String> list = new ArrayList<>();
        for (Operator op : operators) {
            list.add(op.getCode());
        }
        return list;
    }
}

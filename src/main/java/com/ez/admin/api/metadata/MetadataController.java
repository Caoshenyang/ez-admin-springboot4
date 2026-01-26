package com.ez.admin.api.metadata;

import com.ez.admin.common.condition.FieldConfig;
import com.ez.admin.common.condition.QueryConditionSupport;
import com.ez.admin.common.enums.FieldType;
import com.ez.admin.common.exception.ErrorCode;
import com.ez.admin.common.exception.EzBusinessException;
import com.ez.admin.common.model.R;
import com.ez.admin.common.metadata.FieldMetadata;
import com.ez.admin.common.metadata.QueryMetadataVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 元数据控制器
 * <p>
 * 提供资源查询元数据接口，让前端能够动态获取可查询字段信息
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Slf4j
@RestController
@RequestMapping("/api/metadata")
@Tag(name = "元数据管理", description = "提供资源查询元数据接口")
public class MetadataController {

    /**
     * 获取资源查询元数据
     *
     * @param resource 资源名称（如 user、role、menu）
     * @return 查询元数据
     */
    @GetMapping("/{resource}")
    @Operation(summary = "获取资源查询元数据", description = "根据资源名称获取可查询字段的元数据信息")
    public R<QueryMetadataVO> getMetadata(
            @Parameter(description = "资源名称（如 user、role、menu）", example = "user", required = true)
            @PathVariable String resource) {

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
            FieldMetadata fieldMetadata = FieldMetadata.builder()
                    .field(config.getFieldCode())
                    .type(config.getType().name())
                    .description(config.getFieldCode()) // TODO: 从 Provider 中传入描述
                    .operators(convertOperators(config.getOperators()))
                    .required(false)
                    .example(null)
                    .dictType(config.getDictType())
                    .build();
            fields.add(fieldMetadata);
        }

        // 构建 VO
        QueryMetadataVO vo = QueryMetadataVO.builder()
                .resource(resource)
                .description(getResourceDescription(resource))
                .fields(fields)
                .build();

        log.debug("返回元数据，资源：{}，字段数量：{}", resource, vo.getFields().size());

        return R.success(vo);
    }

    /**
     * 获取所有已注册的资源列表
     *
     * @return 资源列表
     */
    @GetMapping
    @Operation(summary = "获取所有已注册的资源", description = "获取所有已注册查询元数据的资源列表")
    public R<List<String>> getRegisteredResources() {
        List<Class<?>> entityClasses = QueryConditionSupport.getRegisteredEntityClasses();
        List<String> resources = new ArrayList<>();
        for (Class<?> entityClass : entityClasses) {
            String resourceName = entityClass.getSimpleName()
                    .replace("Sys", "")
                    .replace("Dict", "")
                    .toLowerCase();
            resources.add(resourceName);
        }
        return R.success(resources);
    }

    /**
     * 根据资源名称获取实体类
     *
     * @param resource 资源名称
     * @return 实体类，如果不存在返回 null
     */
    private Class<?> getEntityClassByResource(String resource) {
        // 遍历所有已注册的实体类
        for (Class<?> entityClass : QueryConditionSupport.getRegisteredEntityClasses()) {
            String className = entityClass.getSimpleName()
                    .replace("Sys", "")
                    .replace("Dict", "")
                    .toLowerCase();
            if (className.equalsIgnoreCase(resource)) {
                return entityClass;
            }
        }
        return null;
    }

    /**
     * 获取资源描述
     *
     * @param resource 资源名称
     * @return 描述
     */
    private String getResourceDescription(String resource) {
        return switch (resource.toLowerCase()) {
            case "user", "users" -> "用户";
            case "role", "roles" -> "角色";
            case "menu", "menus" -> "菜单";
            case "dept", "dept" -> "部门";
            case "dicttype", "dicttype" -> "字典类型";
            default -> resource;
        };
    }

    /**
     * 转换操作符枚举为字符串列表
     *
     * @param operators 操作符数组
     * @return 操作符字符串列表
     */
    private List<String> convertOperators(com.ez.admin.common.enums.Operator[] operators) {
        List<String> list = new ArrayList<>();
        for (com.ez.admin.common.enums.Operator op : operators) {
            list.add(op.getCode());
        }
        return list;
    }
}

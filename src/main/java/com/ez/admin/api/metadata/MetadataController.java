package com.ez.admin.api.metadata;

import com.ez.admin.common.exception.ErrorCode;
import com.ez.admin.common.exception.EzBusinessException;
import com.ez.admin.common.metadata.QueryMetadata;
import com.ez.admin.common.metadata.QueryMetadataVO;
import com.ez.admin.common.model.R;
import com.ez.admin.dto.user.metadata.UserQueryMetadata;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

        QueryMetadata<?> metadata = getMetadataByResource(resource);
        if (metadata == null) {
            log.warn("元数据不存在：{}", resource);
            throw new EzBusinessException(ErrorCode.NOT_FOUND, "不存在的资源: " + resource);
        }

        QueryMetadataVO vo = QueryMetadataVO.builder()
                .resource(resource)
                .description(metadata.getDescription())
                .fields(metadata.getFields())
                .build();

        log.debug("返回元数据，资源：{}，字段数量：{}", resource, vo.getFields().size());

        return R.success(vo);
    }

    /**
     * 根据资源名称获取元数据枚举
     *
     * @param resource 资源名称
     * @return 元数据枚举，如果不存在返回 null
     */
    private QueryMetadata getMetadataByResource(String resource) {
        return switch (resource.toLowerCase()) {
            case "user", "users" -> UserQueryMetadata.USERNAME;
            // TODO: 未来扩展
            // case "role", "roles" -> RoleQueryMetadata.ROLE_NAME;
            default -> null;
        };
    }
}


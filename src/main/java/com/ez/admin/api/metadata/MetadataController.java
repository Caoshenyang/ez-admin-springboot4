package com.ez.admin.api.metadata;

import com.ez.admin.common.metadata.QueryMetadataVO;
import com.ez.admin.common.model.R;
import com.ez.admin.service.metadata.MetadataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 元数据控制器
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/metadata")
@Tag(name = "元数据管理", description = "提供资源查询元数据接口")
public class MetadataController {

    private final MetadataService metadataService;

    @GetMapping("/{resource}")
    @Operation(summary = "获取资源查询元数据", description = "根据资源名称获取可查询字段的元数据信息")
    public R<QueryMetadataVO> getMetadata(
            @Parameter(description = "资源名称（如 user、role、menu）", example = "user", required = true)
            @PathVariable String resource) {
        QueryMetadataVO metadata = metadataService.getMetadata(resource);
        return R.success(metadata);
    }

    @GetMapping
    @Operation(summary = "获取所有已注册的资源", description = "获取所有已注册查询元数据的资源列表")
    public R<List<String>> getRegisteredResources() {
        List<String> resources = metadataService.getRegisteredResources();
        return R.success(resources);
    }
}

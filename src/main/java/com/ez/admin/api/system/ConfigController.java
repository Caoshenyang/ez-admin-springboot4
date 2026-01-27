package com.ez.admin.api.system;

import com.ez.admin.common.model.annotation.OperationLog;
import com.ez.admin.common.model.model.PageQuery;
import com.ez.admin.common.model.model.PageVO;
import com.ez.admin.common.model.model.R;
import com.ez.admin.dto.config.req.ConfigCreateReq;
import com.ez.admin.dto.config.req.ConfigUpdateReq;
import com.ez.admin.dto.config.vo.ConfigDetailVO;
import com.ez.admin.dto.config.vo.ConfigListVO;
import com.ez.admin.service.config.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统配置控制器
 *
 * @author ez-admin
 * @since 2026-01-27
 */
@Slf4j
@RestController
@RequestMapping("/config")
@RequiredArgsConstructor
@Tag(name = "系统配置管理", description = "系统配置的增删改查操作")
public class ConfigController {

    private final ConfigService configService;

    @PostMapping
    @OperationLog(module = "系统配置", operation = "创建", description = "创建系统配置")
    @Operation(summary = "创建配置", description = "创建新的系统配置")
    public R<Void> create(@Valid @RequestBody ConfigCreateReq request) {
        log.info("创建配置请求，配置名称：{}", request.getConfigName());
        configService.createConfig(request);
        return R.success("创建成功");
    }

    @PutMapping
    @OperationLog(module = "系统配置", operation = "更新", description = "更新系统配置")
    @Operation(summary = "更新配置", description = "更新系统配置")
    public R<Void> update(@Valid @RequestBody ConfigUpdateReq request) {
        log.info("更新配置请求，配置ID：{}", request.getConfigId());
        configService.updateConfig(request);
        return R.success("更新成功");
    }

    @DeleteMapping("/{configId}")
    @OperationLog(module = "系统配置", operation = "删除", description = "删除系统配置")
    @Operation(summary = "删除配置", description = "根据配置ID删除配置")
    public R<Void> delete(@PathVariable Long configId) {
        log.info("删除配置请求，配置ID：{}", configId);
        configService.deleteConfig(configId);
        return R.success("删除成功");
    }

    @GetMapping("/{configId}")
    @Operation(summary = "查询配置详情", description = "根据配置ID查询配置完整信息")
    public R<ConfigDetailVO> getById(@PathVariable Long configId) {
        ConfigDetailVO config = configService.getConfigById(configId);
        return R.success(config);
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询配置", description = "分页查询系统配置列表")
    public R<PageVO<ConfigListVO>> getPage(@RequestBody PageQuery query) {
        PageVO<ConfigListVO> page = configService.getConfigPage(query);
        return R.success(page);
    }

    @GetMapping("/list")
    @Operation(summary = "查询所有配置", description = "查询所有系统配置列表（不分页）")
    public R<List<ConfigListVO>> getList() {
        List<ConfigListVO> list = configService.getConfigList();
        return R.success(list);
    }

    @GetMapping("/value/{configKey}")
    @Operation(summary = "获取配置值", description = "根据配置键值获取配置内容")
    public R<String> getConfigValue(@PathVariable String configKey) {
        String value = configService.getConfigValue(configKey);
        return R.success(value);
    }
}

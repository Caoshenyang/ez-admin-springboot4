package com.ez.admin.dto.system.config.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 系统配置创建请求对象
 *
 * @author ez-admin
 * @since 2026-01-27
 */
@Data
@Schema(name = "ConfigCreateReq", description = "系统配置创建请求")
public class ConfigCreateReq {

    @Schema(description = "配置名称", example = "系统名称")
    @NotBlank(message = "配置名称不能为空")
    @Size(max = 100, message = "配置名称长度不能超过 100 个字符")
    private String configName;

    @Schema(description = "配置键值", example = "sys.name")
    @NotBlank(message = "配置键值不能为空")
    @Size(max = 100, message = "配置键值长度不能超过 100 个字符")
    private String configKey;

    @Schema(description = "配置内容", example = "EZ-Admin 管理系统")
    @NotBlank(message = "配置内容不能为空")
    @Size(max = 500, message = "配置内容长度不能超过 500 个字符")
    private String configValue;

    @Schema(description = "配置类型（system=系统配置，user=用户配置）", example = "system")
    @NotBlank(message = "配置类型不能为空")
    private String configType;

    @Schema(description = "备注", example = "系统名称，用于页面标题显示")
    @Size(max = 200, message = "备注长度不能超过 200 个字符")
    private String remark;
}

package com.ez.admin.dto.config.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 系统配置更新请求对象
 *
 * @author ez-admin
 * @since 2026-01-27
 */
@Data
@Schema(name = "ConfigUpdateReq", description = "系统配置更新请求")
public class ConfigUpdateReq {

    @Schema(description = "配置ID", example = "1")
    @NotNull(message = "配置ID不能为空")
    private Long configId;

    @Schema(description = "配置名称", example = "系统名称")
    @Size(max = 100, message = "配置名称长度不能超过 100 个字符")
    private String configName;

    @Schema(description = "配置内容", example = "EZ-Admin 管理系统")
    @Size(max = 500, message = "配置内容长度不能超过 500 个字符")
    private String configValue;

    @Schema(description = "备注", example = "系统名称，用于页面标题显示")
    @Size(max = 200, message = "备注长度不能超过 200 个字符")
    private String remark;
}

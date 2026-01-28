package com.ez.admin.dto.config.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 系统配置详情响应对象
 *
 * @author ez-admin
 * @since 2026-01-27
 */
@Getter
@Builder
@Schema(name = "ConfigDetailVO", description = "系统配置详情响应对象")
public class ConfigDetailVO {

    @Schema(description = "配置ID")
    private Long configId;

    @Schema(description = "配置名称")
    private String configName;

    @Schema(description = "配置键值")
    private String configKey;

    @Schema(description = "配置内容")
    private String configValue;

    @Schema(description = "配置类型（system=系统配置，user=用户配置）")
    private String configType;

    @Schema(description = "是否系统内置（0=否，1=是）")
    private Integer isSystem;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建者ID")
    private Long createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新者ID")
    private Long updateBy;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}

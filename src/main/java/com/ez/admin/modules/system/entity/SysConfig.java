package com.ez.admin.modules.system.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统配置表
 * <p>
 * 用于管理系统参数配置，如系统名称、默认密码、上传限制等
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-27
 */
@Getter
@Setter
@ToString
@TableName("ez_admin_sys_config")
@Schema(name = "SysConfig", description = "系统配置表")
public class SysConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "配置ID")
    @TableId(value = "config_id", type = IdType.ASSIGN_ID)
    private Long configId;

    @TableField("config_name")
    @Schema(description = "配置名称")
    private String configName;

    @TableField("config_key")
    @Schema(description = "配置键值")
    private String configKey;

    @TableField("config_value")
    @Schema(description = "配置键值")
    private String configValue;

    @TableField("config_type")
    @Schema(description = "配置类型（system=系统配置，user=用户配置）")
    private String configType;

    @Schema(description = "是否系统内置（0=否，1=是）")
    @TableField("is_system")
    private Integer isSystem;

    @TableField("remark")
    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建者")
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新者")
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @Schema(description = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("is_deleted")
    @Schema(description = "是否删除【0 正常 1 已删除】")
    private Integer isDeleted;
}

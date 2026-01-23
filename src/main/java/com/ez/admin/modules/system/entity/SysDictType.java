package com.ez.admin.modules.system.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 字典类型表
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Getter
@Setter
@ToString
@TableName("ez_admin_sys_dict_type")
@Schema(name = "SysDictType", description = "字典类型表")
public class SysDictType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "字典主键")
    @TableId(value = "dict_id", type = IdType.ASSIGN_ID)
    private Long dictId;

    @TableField("dict_name")
    @Schema(description = "字典名称")
    private String dictName;

    @TableField("dict_type")
    @Schema(description = "字典类型")
    private String dictType;

    @TableField("status")
    @Schema(description = "状态【0 停用 1 正常】")
    private Integer status;

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

    @TableField("description")
    @Schema(description = "描述信息")
    private String description;
}

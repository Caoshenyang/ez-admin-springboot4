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
 * 字典详情表
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Getter
@Setter
@ToString
@TableName("ez_admin_sys_dict_data")
@Schema(name = "SysDictData", description = "字典详情表")
public class SysDictData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "字典详情主键ID")
    @TableId(value = "dict_data_id", type = IdType.ASSIGN_ID)
    private Long dictDataId;

    @TableField("dict_id")
    @Schema(description = "字典类型ID")
    private Long dictId;

    @TableField("dict_label")
    @Schema(description = "字典标签")
    private String dictLabel;

    @TableField("dict_value")
    @Schema(description = "字典键值")
    private String dictValue;

    @TableField("dict_sort")
    @Schema(description = "字典排序")
    private Integer dictSort;

    @TableField("list_class")
    @Schema(description = "表格回显样式")
    private String listClass;

    @TableField("is_default")
    @Schema(description = "是否默认【0 否 1 是】")
    private Integer isDefault;

    @TableField("status")
    @Schema(description = "状态【0 停用 1 正常】")
    private Integer status;

    @Schema(description = "创建者ID")
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private Long createBy;

    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新者ID")
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    @Schema(description = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField("description")
    @Schema(description = "描述信息")
    private String description;
}

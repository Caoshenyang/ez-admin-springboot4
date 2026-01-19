package com.ez.admin.system.entity;

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
 * 字典详情表
 *
 * @author ez-admin
 * @since 2026-01-19
 */
@Schema(description = "字典详情表")
@Getter
@Setter
@ToString
@TableName("ez_admin_sys_dict_data")
public class DictData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "字典详情主键ID", example = "1")
    @TableId(value = "dict_data_id", type = IdType.ASSIGN_ID)
    private Long dictDataId;

    @Schema(description = "字典类型ID", example = "1")
    @TableField("dict_id")
    private Long dictId;

    @Schema(description = "字典标签", example = "男")
    @TableField("dict_label")
    private String dictLabel;

    @Schema(description = "字典键值", example = "1")
    @TableField("dict_value")
    private String dictValue;

    @Schema(description = "字典排序", example = "1")
    @TableField("dict_sort")
    private Integer dictSort;

    @Schema(description = "表格回显样式", example = "primary")
    @TableField("list_class")
    private String listClass;

    @Schema(description = "是否默认（0=否，1=是）", example = "0")
    @TableField("is_default")
    private Integer isDefault;

    @Schema(description = "状态（0=停用，1=正常）", example = "1")
    @TableField("status")
    private Integer status;

    @Schema(description = "创建者", example = "admin")
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    @Schema(description = "创建时间", example = "2026-01-19T10:00:00")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新者", example = "admin")
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @Schema(description = "更新时间", example = "2026-01-19T10:00:00")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Schema(description = "描述信息", example = "男性")
    @TableField("description")
    private String description;
}

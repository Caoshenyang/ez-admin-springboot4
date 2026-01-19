package com.ez.admin.system.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2026-01-19
 */
@Getter
@Setter
@ToString
@TableName("ez_admin_sys_dict_data")
public class DictData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字典详情主键ID
     */
    @TableId(value = "dict_data_id", type = IdType.ASSIGN_ID)
    private Long dictDataId;

    /**
     * 字典类型ID
     */
    @TableField("dict_id")
    private Long dictId;

    /**
     * 字典标签
     */
    @TableField("dict_label")
    private String dictLabel;

    /**
     * 字典键值
     */
    @TableField("dict_value")
    private String dictValue;

    /**
     * 字典排序
     */
    @TableField("dict_sort")
    private Integer dictSort;

    /**
     * 表格回显样式
     */
    @TableField("list_class")
    private String listClass;

    /**
     * 是否默认【0 否 1 是】
     */
    @TableField("is_default")
    private Integer isDefault;

    /**
     * 状态【0 停用 1 正常】
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建者
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 描述信息
     */
    @TableField("description")
    private String description;
}

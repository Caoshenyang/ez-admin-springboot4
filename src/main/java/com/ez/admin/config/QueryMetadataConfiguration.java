package com.ez.admin.config;

import com.ez.admin.common.enums.FieldType;
import com.ez.admin.common.enums.Operator;
import com.ez.admin.common.metadata.QueryMetadataBuilder;
import com.ez.admin.modules.system.entity.SysDictType;
import com.ez.admin.modules.system.entity.SysRole;
import com.ez.admin.modules.system.entity.SysUser;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 查询元数据配置
 * <p>
 * 统一注册所有模块的查询元数据
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Slf4j
@Component
public class QueryMetadataConfiguration {

    /**
     * 应用启动后自动注册所有查询元数据
     */
    @PostConstruct
    public void registerMetadata() {
        log.info("开始注册查询元数据...");

        // 用户查询元数据
        QueryMetadataBuilder.create(SysUser.class)
                // 用户账号
                .field("username", FieldType.STRING, "用户账号")
                .keywordSearch()
                .operators(Operator.EQ, Operator.LIKE, Operator.IN)
                .column(SysUser::getUsername)
                .add()
                // 用户昵称
                .field("nickname", FieldType.STRING, "用户昵称")
                .keywordSearch()
                .operators(Operator.LIKE)
                .column(SysUser::getNickname)
                .add()
                // 用户邮箱
                .field("email", FieldType.STRING, "用户邮箱")
                .operators(Operator.EQ, Operator.LIKE)
                .column(SysUser::getEmail)
                .add()
                // 手机号码
                .field("phoneNumber", FieldType.STRING, "手机号码")
                .keywordSearch()
                .operators(Operator.EQ, Operator.LIKE)
                .column(SysUser::getPhoneNumber)
                .add()
                // 性别
                .field("gender", FieldType.INTEGER, "性别")
                .operators(Operator.EQ, Operator.IN)
                .column(SysUser::getGender)
                .dictType("sys_gender")
                .add()
                // 用户状态
                .field("status", FieldType.INTEGER, "用户状态")
                .operators(Operator.EQ, Operator.IN)
                .column(SysUser::getStatus)
                .dictType("sys_user_status")
                .add()
                // 部门ID
                .field("deptId", FieldType.LONG, "部门ID")
                .operators(Operator.EQ, Operator.IN)
                .column(SysUser::getDeptId)
                .add()
                .register();

        // 角色查询元数据
        QueryMetadataBuilder.create(SysRole.class)
                // 角色名称
                .field("roleName", FieldType.STRING, "角色名称")
                .keywordSearch()
                .operators(Operator.EQ, Operator.LIKE)
                .column(SysRole::getRoleName)
                .add()
                // 角色权限字符
                .field("roleLabel", FieldType.STRING, "角色权限字符")
                .keywordSearch()
                .operators(Operator.EQ, Operator.LIKE)
                .column(SysRole::getRoleLabel)
                .add()
                // 数据范围
                .field("dataScope", FieldType.INTEGER, "数据范围")
                .operators(Operator.EQ, Operator.IN)
                .column(SysRole::getDataScope)
                .dictType("sys_data_scope")
                .add()
                // 角色状态
                .field("status", FieldType.INTEGER, "角色状态")
                .operators(Operator.EQ, Operator.IN)
                .column(SysRole::getStatus)
                .dictType("sys_common_status")
                .add()
                .register();

        // 字典类型查询元数据
        QueryMetadataBuilder.create(SysDictType.class)
                // 字典名称
                .field("dictName", FieldType.STRING, "字典名称")
                .keywordSearch()
                .operators(Operator.EQ, Operator.LIKE)
                .column(SysDictType::getDictName)
                .add()
                // 字典类型
                .field("dictType", FieldType.STRING, "字典类型")
                .keywordSearch()
                .operators(Operator.EQ, Operator.LIKE)
                .column(SysDictType::getDictType)
                .add()
                // 状态
                .field("status", FieldType.INTEGER, "状态")
                .operators(Operator.EQ, Operator.IN)
                .column(SysDictType::getStatus)
                .dictType("sys_common_status")
                .add()
                .register();

        log.info("查询元数据注册完成！");
    }
}

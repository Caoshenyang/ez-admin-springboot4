package com.ez.admin.dto.system.user.metadata;

import com.ez.admin.common.core.enums.FieldType;
import com.ez.admin.common.core.enums.Operator;
import com.ez.admin.common.data.metadata.metadata.QueryMetadataBuilder;
import com.ez.admin.modules.system.entity.SysUser;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 用户查询元数据提供者
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Slf4j
@Component
public class UserQueryMetadataProvider {

    @PostConstruct
    public void registerMetadata() {
        log.info("注册用户查询元数据...");

        QueryMetadataBuilder.create(SysUser.class)
                .description("用户")
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
    }
}

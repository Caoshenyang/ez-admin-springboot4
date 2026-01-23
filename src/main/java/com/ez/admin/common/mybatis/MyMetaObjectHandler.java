package com.ez.admin.common.mybatis;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.ez.admin.common.constant.SystemConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 自动填充处理器
 * <p>
 * 在插入和更新操作时，自动填充实体类中标注了 @TableField 注解的字段：
 * <ul>
 *   <li>createBy：创建者（插入时填充，获取当前登录用户ID）</li>
 *   <li>createTime：创建时间（插入时填充）</li>
 *   <li>updateBy：更新者（插入和更新时填充，获取当前登录用户ID）</li>
 *   <li>updateTime：更新时间（插入和更新时填充）</li>
 * </ul>
 * </p>
 * <p>
 * 注意：实体类中需要使用 @TableField(fill = FieldFill.INSERT) 或
 * @TableField(fill = FieldFill.INSERT_UPDATE) 注解标注需要自动填充的字段
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入操作自动填充
     * <p>
     * 填充字段：createBy、createTime、updateBy、updateTime
     * </p>
     *
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("开始插入填充...");

        // 填充创建时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());

        // 填充更新时间
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

        // 填充创建者（从当前登录用户获取，如果未登录则使用 system）
        String createBy = getCurrentUserId();
        this.strictInsertFill(metaObject, "createBy", String.class, createBy);

        // 填充更新者
        this.strictInsertFill(metaObject, "updateBy", String.class, createBy);
    }

    /**
     * 更新操作自动填充
     * <p>
     * 填充字段：updateBy、updateTime
     * </p>
     *
     * @param metaObject 元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("开始更新填充...");

        // 填充更新时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

        // 填充更新者（从当前登录用户获取，如果未登录则使用 system）
        String updateBy = getCurrentUserId();
        this.strictUpdateFill(metaObject, "updateBy", String.class, updateBy);
    }

    /**
     * 获取当前登录用户ID
     * <p>
     * 从 Sa-Token 中获取当前登录用户ID，如果未登录或获取失败，则返回系统默认值
     * </p>
     *
     * @return 用户ID
     */
    private String getCurrentUserId() {
        try {
            Object loginId = StpUtil.getLoginIdDefaultNull();
            return loginId != null ? String.valueOf(loginId) : SystemConstants.CREATOR_SYSTEM;
        } catch (Exception e) {
            log.warn("获取当前登录用户ID失败，使用默认值：{}", SystemConstants.CREATOR_SYSTEM, e);
            return SystemConstants.CREATOR_SYSTEM;
        }
    }
}

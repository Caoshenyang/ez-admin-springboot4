package com.ez.admin.common.metadata;

import com.ez.admin.modules.system.entity.SysDictType;
import com.ez.admin.modules.system.entity.SysRole;
import com.ez.admin.modules.system.entity.SysUser;

/**
 * 查询元数据提供者接口
 * <p>
 * 各模块通过实现此接口来提供查询元数据
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-26
 */
public interface QueryMetadataProvider<T> {

    /**
     * 获取实体类
     *
     * @return 实体类
     */
    Class<T> getEntityClass();

    /**
     * 注册元数据
     */
    void registerMetadata();
}

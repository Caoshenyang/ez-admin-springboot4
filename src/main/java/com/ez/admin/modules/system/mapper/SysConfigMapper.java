package com.ez.admin.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ez.admin.modules.system.entity.SysConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统配置 Mapper
 *
 * @author ez-admin
 * @since 2026-01-27
 */
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {

    /**
     * 检查配置键值是否存在
     *
     * @param configKey 配置键值
     * @return 是否存在
     */
    default boolean existsByConfigKey(String configKey) {
        return selectCount(com.baomidou.mybatisplus.core.toolkit.Wrappers.<SysConfig>lambdaQuery()
                .eq(SysConfig::getConfigKey, configKey)) > 0;
    }
}

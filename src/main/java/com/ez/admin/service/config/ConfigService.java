package com.ez.admin.service.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ez.admin.common.core.exception.EzBusinessException;
import com.ez.admin.common.core.exception.ErrorCode;
import com.ez.admin.common.model.model.PageQuery;
import com.ez.admin.common.model.model.PageVO;
import com.ez.admin.dto.system.config.req.ConfigCreateReq;
import com.ez.admin.dto.system.config.req.ConfigUpdateReq;
import com.ez.admin.dto.system.config.vo.ConfigDetailVO;
import com.ez.admin.dto.system.config.vo.ConfigListVO;
import com.ez.admin.modules.system.entity.SysConfig;
import com.ez.admin.modules.system.mapper.SysConfigMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统配置服务
 *
 * @author ez-admin
 * @since 2026-01-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigService {

    private final SysConfigMapper configMapper;

    /**
     * 创建配置
     *
     * @param request 创建请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void createConfig(ConfigCreateReq request) {
        // 1. 检查配置键值是否已存在
        if (configMapper.existsByConfigKey(request.getConfigKey())) {
            throw new EzBusinessException(ErrorCode.VALIDATION_ERROR, "配置键值已存在");
        }

        // 2. 创建配置
        SysConfig config = new SysConfig();
        config.setConfigName(request.getConfigName());
        config.setConfigKey(request.getConfigKey());
        config.setConfigValue(request.getConfigValue());
        config.setConfigType(request.getConfigType());
        config.setIsSystem(0);
        config.setRemark(request.getRemark());
        configMapper.insert(config);

        log.info("创建配置成功，配置名称：{}", request.getConfigName());
    }

    /**
     * 更新配置
     *
     * @param request 更新请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateConfig(ConfigUpdateReq request) {
        // 1. 检查配置是否存在
        SysConfig existConfig = configMapper.selectById(request.getConfigId());
        if (existConfig == null) {
            throw new EzBusinessException(ErrorCode.VALIDATION_ERROR, "配置不存在");
        }

        // 2. 系统内置配置不允许修改键值
        if (existConfig.getIsSystem() == 1) {
            throw new EzBusinessException(ErrorCode.VALIDATION_ERROR, "系统内置配置不允许修改");
        }

        // 3. 更新配置
        SysConfig config = new SysConfig();
        config.setConfigId(request.getConfigId());
        if (StringUtils.hasText(request.getConfigName())) {
            config.setConfigName(request.getConfigName());
        }
        if (StringUtils.hasText(request.getConfigValue())) {
            config.setConfigValue(request.getConfigValue());
        }
        if (StringUtils.hasText(request.getRemark())) {
            config.setRemark(request.getRemark());
        }
        configMapper.updateById(config);

        log.info("更新配置成功，配置ID：{}", request.getConfigId());
    }

    /**
     * 删除配置
     *
     * @param configId 配置ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfig(Long configId) {
        // 1. 检查配置是否存在
        SysConfig config = configMapper.selectById(configId);
        if (config == null) {
            throw new EzBusinessException(ErrorCode.VALIDATION_ERROR, "配置不存在");
        }

        // 2. 系统内置配置不允许删除
        if (config.getIsSystem() == 1) {
            throw new EzBusinessException(ErrorCode.VALIDATION_ERROR, "系统内置配置不允许删除");
        }

        // 3. 逻辑删除配置
        configMapper.deleteById(configId);

        log.info("删除配置成功，配置ID：{}", configId);
    }

    /**
     * 根据ID查询配置详情
     *
     * @param configId 配置ID
     * @return 配置详情
     */
    public ConfigDetailVO getConfigById(Long configId) {
        SysConfig config = configMapper.selectById(configId);
        if (config == null) {
            throw new EzBusinessException(ErrorCode.VALIDATION_ERROR, "配置不存在");
        }

        return ConfigDetailVO.builder()
                .configId(config.getConfigId())
                .configName(config.getConfigName())
                .configKey(config.getConfigKey())
                .configValue(config.getConfigValue())
                .configType(config.getConfigType())
                .isSystem(config.getIsSystem())
                .remark(config.getRemark())
                .createBy(config.getCreateBy())
                .createTime(config.getCreateTime())
                .updateBy(config.getUpdateBy())
                .updateTime(config.getUpdateTime())
                .build();
    }

    /**
     * 分页查询配置列表
     *
     * @param query 分页查询请求
     * @return 分页结果
     */
    public PageVO<ConfigListVO> getConfigPage(PageQuery query) {
        // 执行分页查询
        Page<SysConfig> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<SysConfig> result = configMapper.selectPage(page, new LambdaQueryWrapper<SysConfig>()
                .orderByDesc(SysConfig::getCreateTime));

        // 转换为 VO
        List<ConfigListVO> voList = result.getRecords().stream()
                .map(config -> ConfigListVO.builder()
                        .configId(config.getConfigId())
                        .configName(config.getConfigName())
                        .configKey(config.getConfigKey())
                        .configValue(config.getConfigValue())
                        .configType(config.getConfigType())
                        .isSystem(config.getIsSystem())
                        .remark(config.getRemark())
                        .build())
                .collect(Collectors.toList());

        return PageVO.<ConfigListVO>builder()
                .records(voList)
                .total(result.getTotal())
                .pageNum(result.getCurrent())
                .pageSize(result.getSize())
                .build();
    }

    /**
     * 查询所有配置列表
     *
     * @return 配置列表
     */
    public List<ConfigListVO> getConfigList() {
        List<SysConfig> configs = configMapper.selectList(new LambdaQueryWrapper<SysConfig>()
                .orderByDesc(SysConfig::getCreateTime));

        return configs.stream()
                .map(config -> ConfigListVO.builder()
                        .configId(config.getConfigId())
                        .configName(config.getConfigName())
                        .configKey(config.getConfigKey())
                        .configValue(config.getConfigValue())
                        .configType(config.getConfigType())
                        .isSystem(config.getIsSystem())
                        .remark(config.getRemark())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 根据配置键值获取配置值
     *
     * @param configKey 配置键值
     * @return 配置值
     */
    public String getConfigValue(String configKey) {
        SysConfig config = configMapper.selectOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, configKey));

        if (config == null) {
            return null;
        }

        return config.getConfigValue();
    }
}

package com.ez.admin.common.mapstruct;

import com.ez.admin.dto.auth.vo.LoginVO;
import com.ez.admin.modules.system.entity.SysUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 用户对象转换器
 * <p>
 * 演示 MapStruct + Builder 模式的集成使用。
 * MapStruct 1.6.3 会自动检测 LoginVO 的 @Builder 注解，
 * 生成的实现类会使用 builder 模式构造对象，无需额外配置。
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Mapper(componentModel = "spring")
public interface UserConverter {

    /**
     * 获取转换器实例（非 Spring 环境使用）
     */
    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);


}

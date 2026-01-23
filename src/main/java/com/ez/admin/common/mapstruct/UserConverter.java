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

    /**
     * 将 SysUser 实体转换为 LoginVO
     * <p>
     * MapStruct 会自动识别 LoginVO 的 @Builder 注解，
     * 生成的代码等价于：
     * <pre>
     * return LoginVO.builder()
     *     .userId(user.getUserId())
     *     .username(user.getUsername())
     *     .nickname(user.getNickname())
     *     .avatar(user.getAvatar())
     *     .build();
     * </pre>
     * </p>
     *
     * @param user 用户实体
     * @return 登录响应 VO
     */
    LoginVO toLoginVO(SysUser user);

    /**
     * 将 SysUser 实体转换为 LoginVO（附带 token）
     * <p>
     * 当字段名相同时，无需 @Mapping 注解，MapStruct 会自动匹配。
     * 只有在字段名不同或需要指定参数来源时才需要 @Mapping。
     * </p>
     *
     * @param user  用户实体
     * @param token 访问令牌
     * @return 登录响应 VO
     */
    @Mapping(source = "token", target = "token")
    LoginVO toLoginVOWithToken(SysUser user, String token);
}

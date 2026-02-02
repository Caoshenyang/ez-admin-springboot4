package com.ez.admin.service.auth;

import cn.dev33.satoken.stp.StpUtil;
import com.ez.admin.common.core.exception.ErrorCode;
import com.ez.admin.common.core.exception.EzBusinessException;
import com.ez.admin.common.framework.datascope.DataScopeContext;
import com.ez.admin.common.framework.datascope.DataScopeInfo;
import com.ez.admin.common.framework.datascope.DataScopeService;
import com.ez.admin.dto.auth.req.LoginReq;
import com.ez.admin.dto.auth.vo.CurrentUserVO;
import com.ez.admin.dto.auth.vo.LoginVO;
import com.ez.admin.dto.system.menu.vo.MenuTreeVO;
import com.ez.admin.dto.system.vo.RoleInfo;
import com.ez.admin.modules.system.entity.SysUser;
import com.ez.admin.modules.system.mapper.SysUserMapper;
import com.ez.admin.modules.system.service.SysMenuService;
import com.ez.admin.modules.system.service.SysUserRoleRelationService;
import com.ez.admin.service.cache.UserCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 认证服务
 * <p>
 * 处理用户登录、登出、当前用户信息查询等认证相关功能
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserMapper userMapper;
    private final DataScopeService dataScopeService;
    private final SysMenuService menuService;
    private final SysUserRoleRelationService userRoleRelationService;
    private final UserCacheService userCacheService;

    /**
     * 密码编码器（用于验证密码）
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应（包含 token）
     */
    public LoginVO login(LoginReq request) {
        // 1. 根据用户名查询用户
        SysUser user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            throw new EzBusinessException(ErrorCode.USER_PASSWORD_ERROR);
        }

        // 2. 验证用户状态
        if (user.getStatus() == 0) {
            throw new EzBusinessException(ErrorCode.USER_ACCOUNT_DISABLED);
        }

        // 3. 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new EzBusinessException(ErrorCode.USER_PASSWORD_ERROR);
        }

        // 4. 使用 Sa-Token 进行登录，生成 token
        StpUtil.login(user.getUserId());

        // 5. 获取用户的数据权限信息
        DataScopeInfo dataScopeInfo = dataScopeService.getDataScopeInfo(user);

        // 6. 将数据权限信息存入 Sa-Token Session，供后续请求使用
        StpUtil.getSession().set("dataScopeInfo", dataScopeInfo);
        log.info("用户 {} 数据权限信息已存入 Session：{}", user.getUsername(), dataScopeInfo);

        // 7. 设置到当前请求的上下文中
        DataScopeContext.setDataScopeInfo(dataScopeInfo);

        // 8. 预加载用户的角色信息到缓存
        loadAndCacheUserRoles(user.getUserId());

        // 9. 构造登录响应
        LoginVO response = LoginVO.builder()
                .token(StpUtil.getTokenValue())
                .build();

        log.info("用户登录成功：{}", user.getUsername());
        return response;
    }

    /**
     * 用户登出
     */
    public void logout() {
        Long userId = StpUtil.getLoginIdAsLong();

        // 清除用户缓存
        userCacheService.deleteUserRoles(userId);

        // Sa-Token 登出
        StpUtil.logout();
        log.info("用户 {} 登出成功", userId);
    }

    /**
     * 刷新用户的角色缓存（用于用户角色变更后）
     * <p>
     * 当管理员给用户分配或移除角色时，调用此方法刷新 Redis 中的角色信息
     * </p>
     *
     * @param userId 用户ID
     */
    public void refreshUserRolesCache(Long userId) {
        // 从数据库查询最新角色信息
        List<RoleInfo> roleInfoList = userRoleRelationService.getRoleInfoByUserId(userId);

        // 更新缓存
        if (roleInfoList.isEmpty()) {
            userCacheService.deleteUserRoles(userId);
            log.info("用户 {} 无角色，已删除 Redis 角色缓存", userId);
        } else {
            userCacheService.saveUserRoles(userId, roleInfoList);
            String roleLabels = roleInfoList.stream()
                    .map(RoleInfo::getRoleLabel)
                    .collect(Collectors.joining(", "));
            log.info("已刷新用户 {} 的角色缓存，角色：{}", userId, roleLabels);
        }
    }

    /**
     * 获取当前登录用户的角色信息列表
     * <p>
     * 采用 Cache-Aside 缓存策略：
     * <ol>
     *   <li>先从缓存获取</li>
     *   <li>缓存未命中时，从数据库查询</li>
     *   <li>将查询结果写入缓存</li>
     * </ol>
     * </p>
     *
     * @return 角色信息列表
     */
    public List<RoleInfo> getCurrentUserRoles() {
        Long userId = StpUtil.getLoginIdAsLong();

        // 1. 先从缓存获取
        List<RoleInfo> cachedRoles = userCacheService.getUserRoles(userId);
        if (cachedRoles != null) {
            return cachedRoles;
        }

        // 2. 缓存未命中，从数据库查询
        List<RoleInfo> roleInfoList = userRoleRelationService.getRoleInfoByUserId(userId);

        // 3. 写入缓存
        if (!roleInfoList.isEmpty()) {
            userCacheService.saveUserRoles(userId, roleInfoList);
        }

        return roleInfoList;
    }

    /**
     * 判断当前登录用户是否拥有指定角色
     *
     * @param roleLabel 角色标识（如：SUPER_ADMIN）
     * @return true-拥有该角色，false-不拥有
     */
    public boolean hasRole(String roleLabel) {
        List<RoleInfo> roles = getCurrentUserRoles();
        return roles.stream()
                .anyMatch(role -> role.getRoleLabel().equals(roleLabel));
    }

    /**
     * 获取当前登录用户完整信息
     * <p>
     * 包含用户基本信息、角色、权限标识、菜单树等信息
     * 用于前端动态路由、动态菜单、权限控制
     * </p>
     *
     * @return 当前用户信息
     */
    public CurrentUserVO getCurrentUser() {
        // 1. 获取当前用户ID
        Long userId = StpUtil.getLoginIdAsLong();

        // 2. 查询用户基本信息
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new EzBusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 3. 获取用户的角色信息
        List<RoleInfo> roleInfoList = getCurrentUserRoles();
        List<String> roleLabels = roleInfoList.stream()
                .map(RoleInfo::getRoleLabel)
                .collect(Collectors.toList());
        List<Long> roleIds = roleInfoList.stream()
                .map(RoleInfo::getRoleId)
                .collect(Collectors.toList());

        // 4. 获取用户的权限标识列表
        List<String> permissions = menuService.getUserPermissions(roleIds);

        // 5. 获取用户的菜单树
        List<MenuTreeVO> menus = menuService.getUserMenuTree(roleIds);

        // 6. 构建返回对象
        return CurrentUserVO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .roleLabels(roleLabels)
                .permissions(permissions)
                .menus(menus)
                .build();
    }

    /**
     * 加载并缓存用户角色信息
     * <p>
     * 从数据库查询用户角色信息并缓存到 Redis
     * </p>
     *
     * @param userId 用户ID
     */
    private void loadAndCacheUserRoles(Long userId) {
        List<RoleInfo> roleInfoList = userRoleRelationService.getRoleInfoByUserId(userId);
        if (!roleInfoList.isEmpty()) {
            userCacheService.saveUserRoles(userId, roleInfoList);
        }
    }
}

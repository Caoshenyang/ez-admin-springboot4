package com.ezadmin.service;

import cn.dev33.satoken.stp.StpUtil;
import com.ezadmin.common.cache.AdminCache;
import com.ezadmin.common.constants.UserConstants;
import com.ezadmin.common.exception.ExceptionEnum;
import com.ezadmin.common.exception.EzAdminException;
import com.ezadmin.model.dto.LoginDTO;
import com.ezadmin.model.mapstruct.MsUserMapper;
import com.ezadmin.model.vo.UserInfoVO;
import com.ezadmin.modules.system.entity.Role;
import com.ezadmin.modules.system.entity.User;
import com.ezadmin.modules.system.entity.UserRole;
import com.ezadmin.modules.system.service.IRoleService;
import com.ezadmin.modules.system.service.IUserRoleService;
import com.ezadmin.modules.system.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 认证服务
 * </p>
 *
 * @author 曹申阳
 * @since 2025-12-16 15:24:21
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final IUserService userService;
    private final IRoleService roleService;
    private final IUserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;
    private final AdminCache adminCache;


    /**
     * 初始化管理员账号
     */
    @Transactional(rollbackFor = Exception.class)
    public void initAdmin() {
        // 检查是否已有admin用户
        User adminUser = userService.selectUserByUsername(UserConstants.ADMIN_USER_NAME);
        if (adminUser != null) {
            // 已有管理员，不重复创建
            throw new EzAdminException(ExceptionEnum.ADMIN_ACCOUNT_INITIALIZED);
        }

        // 创建新的管理员账号
        User user = new User();
        user.setUsername(UserConstants.ADMIN_USER_NAME);
        // 使用Sa-Token提供的MD5加密密码
        user.setPassword(passwordEncoder.encode(UserConstants.ADMIN_USER_PASSWORD));
        user.setNickname(UserConstants.ADMIN_USER_NAME);
        // 保存管理员账号
        userService.save(user);
        Role adminRole = new Role();
        adminRole.setRoleName(UserConstants.ADMIN_ROLE_NAME);
        adminRole.setRoleLabel(UserConstants.ADMIN_ROLE_LABEL);
        roleService.save(adminRole);
        // 创建用户角色关联
        UserRole userRole = new UserRole();
        userRole.setUserId(user.getUserId());  // 使用新创建的用户ID
        userRole.setRoleId(adminRole.getRoleId());  // 使用管理员角色ID
        userRoleService.save(userRole);
    }


    /**
     * 用户登录
     *
     * @param loginDTO 登录DTO
     */
    public void login(LoginDTO loginDTO) {

        User user = userService.selectUserByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new EzAdminException(ExceptionEnum.USERNAME_NOT_EXISTS);
        }
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new EzAdminException(ExceptionEnum.USERNAME_OR_PASSWORD_ERROR);
        }

        // 用户验证
        StpUtil.login(user.getUserId());
        // 隐藏密码
        user.setPassword(null);
        StpUtil.getSession().set("user", user);
        // 登录成功, 缓存用户角色信息，避免每次查询数据库
        List<Role> roles = roleService.selectRoleListByUserId(user.getUserId());
        List<String> roleLabels = roles.stream().map(Role::getRoleLabel).collect(Collectors.toList());
        adminCache.cacheUserRoles(user.getUserId(), roleLabels);
    }

    public UserInfoVO getUserInfo() {
        long loginId = StpUtil.getLoginIdAsLong();
        // 从 session 中获取用户信息
        User user = (User) StpUtil.getSession().get("user");
        if (user == null) {
            throw new EzAdminException(ExceptionEnum.USER_NOT_LOGGED_IN);
        }
        List<String> permissionList = StpUtil.getPermissionList(loginId);
        List<String> roleList = StpUtil.getRoleList(loginId);
//        List<MenuPermissionVO> menuByRoleLabels = adminCache.getMenuByRoleLabels(roleList);
//        List<MenuTreeVO> routerTreeVOS = MsMenuMapper.INSTANCE.menuPermissionVO2MenuTreeVOs(menuByRoleLabels);
//        List<MenuTreeVO> menus = TreeBuilder.buildTree(routerTreeVOS);
        UserInfoVO userInfoVO = MsUserMapper.INSTANCE.user2UserInfoVO(user);
        userInfoVO.setPermissions(permissionList);
//        userInfoVO.setMenus(menus);
        return userInfoVO;
    }
}

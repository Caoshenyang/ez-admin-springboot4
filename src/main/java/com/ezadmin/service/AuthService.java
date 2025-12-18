package com.ezadmin.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import com.ezadmin.common.cache.AdminCache;
import com.ezadmin.common.constants.UserConstants;
import com.ezadmin.common.exception.ExceptionEnum;
import com.ezadmin.common.exception.EzAdminException;
import com.ezadmin.common.response.tree.TreeBuilder;
import com.ezadmin.model.dto.LoginDTO;
import com.ezadmin.model.mapstruct.MsMenuMapper;
import com.ezadmin.model.vo.MenuPermissionVO;
import com.ezadmin.model.vo.MenuTreeVO;
import com.ezadmin.model.vo.UserInfoVO;
import com.ezadmin.modules.system.entity.Role;
import com.ezadmin.modules.system.entity.User;
import com.ezadmin.modules.system.entity.UserRoleRelation;
import com.ezadmin.modules.system.service.IRoleService;
import com.ezadmin.modules.system.service.IUserRoleRelationService;
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
    private final IUserRoleRelationService userRoleRelationService;
    private final PasswordEncoder passwordEncoder;
    private final AdminCache adminCache;


    /**
     * 初始化管理员账号
     */
    @Transactional(rollbackFor = Exception.class)
    public void initAdmin() {
        // 校验是否已初始化管理员账号
        checkInitAdminAccount();

        // 创建管理员用户
        User adminUser = new User();
        adminUser.setUsername(UserConstants.ADMIN_USER_NAME);
        adminUser.setPassword(passwordEncoder.encode(UserConstants.ADMIN_USER_PASSWORD));
        adminUser.setNickname(UserConstants.ADMIN_USER_NAME);
        adminUser.setCreateBy("init");
        adminUser.setUpdateBy("init");
        // 保存用户
        userService.save(adminUser);
        Role adminRole = new Role();
        adminRole.setRoleName(UserConstants.ADMIN_ROLE_NAME);
        adminRole.setRoleLabel(UserConstants.ADMIN_ROLE_LABEL);
        adminRole.setCreateBy("init");
        adminRole.setUpdateBy("init");
        roleService.save(adminRole);
        UserRoleRelation adminUserRoleRelation = new UserRoleRelation();
        adminUserRoleRelation.setRoleId(adminRole.getRoleId());
        adminUserRoleRelation.setUserId(adminUser.getUserId());
        userRoleRelationService.save(adminUserRoleRelation);
    }

    /**
     * 校验是否已初始化管理员账号
     */
    private void checkInitAdminAccount() {
        // 检查是否已有admin用户
        User adminUser = userService.selectUserByUsername(UserConstants.ADMIN_USER_NAME);
        if (adminUser != null) {
            // 已有管理员，不重复创建
            throw new EzAdminException(ExceptionEnum.ADMIN_ACCOUNT_INITIALIZED);
        }
    }

    /**
     * 用户登录
     * web端登录, 基于 cookie-session 机制, 自动携带 token, 无需前端主动携带 token
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
        StpUtil.login(user.getUserId(), new SaLoginParameter()
            .setExtra("username", user.getUsername())
            .setExtra("nickname", user.getNickname())
            .setExtra("avatar", user.getAvatar()));

        // 登录成功, 缓存用户角色信息，避免每次查询数据库
        List<Role> roles = roleService.selectRoleListByUserId(user.getUserId());
        List<String> roleLabels = roles.stream().map(Role::getRoleLabel).collect(Collectors.toList());
        adminCache.cacheUserRoles(user.getUserId(), roleLabels);
    }

    public UserInfoVO getUserInfo() {
        // 获取当前登录用户信息
        long loginId = StpUtil.getLoginIdAsLong();
        String username = (String) StpUtil.getExtra("username");
        String nickname = (String) StpUtil.getExtra("nickname");
        String avatar = (String) StpUtil.getExtra("avatar");
        // 获取当前登录用户权限信息
        List<String> permissionList = StpUtil.getPermissionList(loginId);
        // 获取当前登录用户角色信息
        List<String> roleList = StpUtil.getRoleList(loginId);
        // 获取当前登录用户菜单权限信息
        List<MenuPermissionVO> menuByRoleLabels = adminCache.getMenuByRoleLabels(roleList);
        // 转换为路由树
        List<MenuTreeVO> routerTreeVOS = MsMenuMapper.INSTANCE.menuPermissionVO2MenuTreeVOs(menuByRoleLabels);
        // 构建菜单树
        List<MenuTreeVO> menus = TreeBuilder.buildTree(routerTreeVOS);
        // 构建用户信息VO
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setPermissions(permissionList);
        userInfoVO.setMenus(menus);
        userInfoVO.setUserId(loginId);
        userInfoVO.setUsername(username);
        userInfoVO.setNickname(nickname);
        userInfoVO.setAvatar(avatar);
        return userInfoVO;
    }


}

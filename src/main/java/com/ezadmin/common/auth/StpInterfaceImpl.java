package com.ezadmin.common.auth;

import cn.dev33.satoken.stp.StpInterface;
import com.ezadmin.common.cache.AdminCache;
import com.ezadmin.model.vo.MenuPermissionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;


/**
 * <p>
 * 自定义权限加载接口实现类
 * </p>
 *
 * @author shenyang
 * @since 2024-12-02 15:40:09
 */
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final AdminCache adminCache;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        List<String> roleLabelList = getRoleList(loginId, loginType);
        if (roleLabelList.isEmpty()) {
            return Collections.emptyList();
        }
        List<MenuPermissionVO> menuPermissions = adminCache.getMenuByRoleLabels(roleLabelList);
        if (menuPermissions == null || menuPermissions.isEmpty()) {
            return Collections.emptyList();
        }
        return menuPermissions.stream()
            .map(MenuPermissionVO::getMenuPerm)
            .filter(StringUtils::hasText)
            .distinct()
            .toList();
    }

    /**
     * 获取角色列表
     *
     * @param loginId   用户id
     * @param loginType 登录类型
     * @return List<String>
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        if (loginId == null) {
            return Collections.emptyList();
        }
        Long userId = parseUserId(loginId);
        if (userId == null) {
            return Collections.emptyList();
        }
        return adminCache.getUserRoles(userId);
    }

    private Long parseUserId(Object loginId) {
        try {
            if (loginId instanceof Number number) {
                return number.longValue();
            }
            return Long.parseLong(String.valueOf(loginId));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

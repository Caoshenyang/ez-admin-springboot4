package com.ez.admin.common.framework.component;

import com.ez.admin.dto.system.vo.SuperAdminPermissionSyncVO;
import com.ez.admin.modules.system.mapper.SysUserMapper;
import com.ez.admin.service.permission.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 权限缓存统一初始化器
 * <p>
 * 应用启动时统一初始化所有权限相关的缓存数据，包括：
 * <ol>
 *   <li>超级管理员权限同步（维护数据库关联关系）</li>
 *   <li>角色菜单权限缓存（预热到 Redis）</li>
 *   <li>路由权限规则缓存（预热到 Redis）</li>
 * </ol>
 * </p>
 * <p>
 * <b>重要说明：</b>
 * 如果系统未初始化（无用户），将跳过权限同步，仅记录警告日志，
 * 不影响系统启动。用户可以通过 /install 接口完成系统初始化。
 * </p>
 * <p>
 * 使用 {@link ApplicationRunner} 确保在所有 Bean 初始化完成后执行，
 * 通过 {@link Order} 注解控制执行顺序（值为 1，最先执行）
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-28
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Order(1)
public class PermissionCacheInitializer implements ApplicationRunner {

    private final PermissionService permissionService;
    private final SysUserMapper userMapper;

    /**
     * 应用启动时统一初始化权限缓存
     * <p>
     * 执行顺序：
     * <ol>
     *   <li>检查系统是否已初始化</li>
     *   <li>如果已初始化，同步超级管理员权限</li>
     *   <li>初始化角色菜单权限缓存</li>
     *   <li>初始化路由权限规则缓存</li>
     * </ol>
     * </p>
     *
     * @param args 应用启动参数
     */
    @Override
    public void run(ApplicationArguments args) {
        log.info("========================================");
        log.info("开始初始化权限缓存数据...");
        log.info("========================================");

        // 检查系统是否已初始化
        boolean systemInitialized = checkSystemInitialized();

        if (!systemInitialized) {
            log.warn("⚠ 系统尚未初始化（无用户数据），跳过权限同步");
            log.warn("⚠ 请访问 POST /install 接口完成系统初始化");
            log.info("========================================");
            log.info("权限缓存初始化完成（系统未初始化，已跳过）");
            log.info("========================================");
            return;
        }

        try {
            // 1. 同步超级管理员权限
            initSuperAdminPermissions();

            // 2. 初始化角色菜单权限缓存
            initRoleMenuPermissions();

            // 3. 初始化路由权限规则缓存
            initRoutePermissions();

            log.info("========================================");
            log.info("权限缓存初始化完成 ✓");
            log.info("========================================");
        } catch (Exception e) {
            log.error("========================================");
            log.error("权限缓存初始化失败 ✗", e);
            log.error("========================================");
            // 不再抛出异常，避免影响系统启动
            log.warn("⚠ 权限缓存初始化失败，但不影响系统启动，可以手动刷新缓存");
        }
    }

    /**
     * 检查系统是否已初始化
     *
     * @return true-已初始化，false-未初始化
     */
    private boolean checkSystemInitialized() {
        Long userCount = userMapper.countActiveUsers();
        return userCount != null && userCount > 0;
    }

    /**
     * 同步超级管理员权限
     * <p>
     * 将所有启用状态的菜单权限分配给 SUPER_ADMIN 角色，并刷新缓存
     * </p>
     */
    private void initSuperAdminPermissions() {
        log.info("① [1/3] 同步超级管理员权限...");
        try {
            SuperAdminPermissionSyncVO result = permissionService.syncSuperAdminPermissions();
            log.info("   ✓ 超级管理员权限同步成功：菜单数量={}", result.getMenuCount());
        } catch (Exception e) {
            log.error("   ✗ 超级管理员权限同步失败", e);
            throw e;
        }
    }

    /**
     * 初始化角色菜单权限缓存
     * <p>
     * 从数据库加载所有角色的菜单权限，预热到 Redis 缓存
     * </p>
     */
    private void initRoleMenuPermissions() {
        log.info("② [2/3] 初始化角色菜单权限缓存...");
        try {
            int count = permissionService.initAllRoleMenuPermissions();
            log.info("   ✓ 角色菜单权限缓存初始化完成：成功 {} 个", count);
        } catch (Exception e) {
            log.error("   ✗ 角色菜单权限缓存初始化失败", e);
            throw e;
        }
    }

    /**
     * 初始化路由权限规则缓存
     * <p>
     * 从数据库加载所有路由权限规则，预热到 Redis Hash 结构
     * </p>
     */
    private void initRoutePermissions() {
        log.info("③ [3/3] 初始化路由权限规则缓存...");
        try {
            int count = permissionService.refreshRoutePermissions();
            log.info("   ✓ 路由权限规则缓存初始化完成：共 {} 条规则", count);
        } catch (Exception e) {
            log.error("   ✗ 路由权限规则缓存初始化失败", e);
            throw e;
        }
    }
}

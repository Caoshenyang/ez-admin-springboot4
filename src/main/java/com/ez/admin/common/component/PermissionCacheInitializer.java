package com.ez.admin.common.component;

import com.ez.admin.dto.system.vo.SuperAdminPermissionSyncVO;
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
 * 使用 {@link ApplicationRunner} 确保在所有 Bean 初始化完成后执行，
 * 通过 {@link Order} 注解控制执行顺序（值为 1，最先执行）
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-27
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Order(1)
public class PermissionCacheInitializer implements ApplicationRunner {

    private final PermissionService permissionService;

    /**
     * 应用启动时统一初始化权限缓存
     * <p>
     * 执行顺序：
     * <ol>
     *   <li>同步超级管理员权限（确保数据库关联正确）</li>
     *   <li>初始化角色菜单权限缓存（预热到 Redis）</li>
     *   <li>初始化路由权限规则缓存（预热到 Redis）</li>
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
            throw new RuntimeException("权限缓存初始化失败", e);
        }
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

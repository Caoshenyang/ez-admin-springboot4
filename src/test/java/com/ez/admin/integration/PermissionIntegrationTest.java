package com.ez.admin.integration;

import cn.dev33.satoken.stp.StpUtil;
import com.ez.admin.dto.auth.req.LoginReq;
import com.ez.admin.dto.role.req.RoleAssignMenuReq;
import com.ez.admin.dto.role.req.RoleCreateReq;
import com.ez.admin.dto.user.req.UserCreateReq;
import com.ez.admin.dto.user.req.UserAssignRoleReq;
import com.ez.admin.service.auth.AuthService;
import com.ez.admin.service.system.RoleService;
import com.ez.admin.service.system.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 权限功能集成测试
 * <p>
 * 测试完整的权限分配和校验流程
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@SpringBootTest
@Transactional
@DisplayName("权限功能集成测试")
public class PermissionIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    private Long adminUserId;
    private Long testRoleId;
    private Long testUserId;

    /**
     * 测试前准备：创建测试数据
     */
    @BeforeEach
    void setUp() {
        // 1. 创建管理员用户（如果不存在）
        try {
            LoginReq loginReq = new LoginReq();
            loginReq.setUsername("admin");
            loginReq.setPassword("admin123456");
            // authService.login(loginReq);
            adminUserId = 1L;
        } catch (Exception e) {
            // 忽略，假设管理员已存在
        }
    }

    @Test
    @DisplayName("测试完整的权限分配流程")
    void testPermissionFlow() {
        // 步骤 1：创建测试角色
        testRoleId = createTestRole();
        System.out.println("✅ 创建测试角色成功，ID: " + testRoleId);

        // 步骤 2：为角色分配菜单权限
        assignMenusToRole();
        System.out.println("✅ 为角色分配菜单成功");

        // 步骤 3：创建测试用户
        testUserId = createTestUser();
        System.out.println("✅ 创建测试用户成功，ID: " + testUserId);

        // 步骤 4：为用户分配角色
        assignRoleToUser();
        System.out.println("✅ 为用户分配角色成功");

        // 步骤 5：验证用户权限
        verifyUserPermissions();
        System.out.println("✅ 用户权限验证成功");

        System.out.println("\n=== 权限功能测试完成 ===");
    }

    /**
     * 创建测试角色
     */
    private Long createTestRole() {
        RoleCreateReq request = new RoleCreateReq();
        request.setRoleName("测试角色");
        request.setRoleLabel("test");
        request.setRoleSort(1);
        request.setDataScope(1);
        request.setStatus(1);
        request.setDescription("集成测试角色");

        roleService.createRole(request);

        // 获取刚创建的角色（假设是最后一个）
        // 实际应用中应该从返回值获取
        return 100L; // 占位符，实际需要从数据库查询
    }

    /**
     * 为角色分配菜单
     */
    private void assignMenusToRole() {
        // 假设有一些菜单 ID
        List<Long> menuIds = List.of(1L, 2L, 3L, 4L, 5L);

        roleService.assignMenus(testRoleId, menuIds);
    }

    /**
     * 创建测试用户
     */
    private Long createTestUser() {
        UserCreateReq request = new UserCreateReq();
        request.setUsername("testuser");
        request.setPassword("test123456");
        request.setNickname("测试用户");
        request.setDeptId(1L);
        request.setGender(1);
        request.setStatus(1);

        userService.createUser(request);

        return 100L; // 占位符，实际需要从数据库查询
    }

    /**
     * 为用户分配角色
     */
    private void assignRoleToUser() {
        List<Long> roleIds = List.of(testRoleId);

        userService.assignRoles(testUserId, roleIds);
    }

    /**
     * 验证用户权限
     */
    private void verifyUserPermissions() {
        // 模拟登录测试用户
        StpUtil.login(testUserId);

        // 检查权限
        boolean hasPermission = StpUtil.hasPermission("system:user:list");
        assert hasPermission : "应该有 system:user:list 权限";

        // 获取所有权限
        List<String> permissions = StpUtil.getPermissionList();
        System.out.println("用户权限列表: " + permissions);

        // 退出登录
        StpUtil.logout();
    }

    @Test
    @DisplayName("测试角色菜单查询")
    void testGetRoleMenus() {
        // 准备测试数据
        testRoleId = 1L; // 假设角色 ID 为 1

        // 查询角色菜单
        List<Long> menuIds = roleService.getRoleMenuIds(testRoleId);

        System.out.println("角色菜单ID列表: " + menuIds);
        assert menuIds != null : "菜单ID列表不应为 null";
    }

    @Test
    @DisplayName("测试用户角色查询")
    void testGetUserRoles() {
        // 准备测试数据
        testUserId = 1L; // 假设用户 ID 为 1

        // 查询用户角色
        List<Long> roleIds = userService.getUserRoleIds(testUserId);

        System.out.println("用户角色ID列表: " + roleIds);
        assert roleIds != null : "角色ID列表不应为 null";
    }
}

package com.ez.admin.api.system;

import com.ez.admin.common.model.annotation.OperationLog;
import com.ez.admin.common.model.model.R;
import com.ez.admin.dto.system.vo.SuperAdminPermissionSyncVO;
import com.ez.admin.service.permission.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统管理接口
 * <p>
 * 提供系统级别的管理功能，如超级管理员权限同步等
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-27
 */
@Slf4j
@RestController
@RequestMapping("/system")
@RequiredArgsConstructor
@Tag(name = "系统管理", description = "系统级别管理接口")
@SecurityRequirement(name = "bearerAuth")
public class SystemController {

    private final PermissionService permissionService;

    @PostMapping("/sync-super-admin-permissions")
    @OperationLog(module = "系统管理", operation = "同步权限", description = "同步超级管理员权限")
    @Operation(summary = "同步超级管理员权限", description = "将所有菜单权限自动分配给 SUPER_ADMIN 角色")
    public R<SuperAdminPermissionSyncVO> syncSuperAdminPermissions() {
        log.info("手动触发超级管理员权限同步");

        // 执行同步
        SuperAdminPermissionSyncVO result = permissionService.syncSuperAdminPermissions();

        return R.success("超级管理员权限同步成功", result);
    }
}

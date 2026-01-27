package com.ez.admin.service.install;

import com.ez.admin.common.core.constant.SystemConstants;
import com.ez.admin.common.core.exception.EzBusinessException;
import com.ez.admin.common.core.exception.ErrorCode;
import com.ez.admin.dto.install.req.InstallReq;
import com.ez.admin.dto.install.vo.InstallStatusVO;
import com.ez.admin.dto.install.vo.InstallVO;
import com.ez.admin.modules.system.entity.SysRole;
import com.ez.admin.modules.system.entity.SysUser;
import com.ez.admin.modules.system.entity.SysUserRoleRelation;
import com.ez.admin.modules.system.mapper.SysRoleMapper;
import com.ez.admin.modules.system.mapper.SysUserRoleRelationMapper;
import com.ez.admin.modules.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 系统初始化服务
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstallService {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysUserRoleRelationMapper userRoleRelationMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 检查系统是否已初始化
     * <p>
     * 通过查询用户表判断，如果存在任何用户记录则认为已初始化
     * </p>
     *
     * @return 初始化状态响应
     */
    public InstallStatusVO checkInstallStatus() {
        // 查询用户表中的记录数
        Long userCount = userMapper.countActiveUsers();

        boolean initialized = userCount > 0;

        return InstallStatusVO.builder()
                .initialized(initialized)
                .message(initialized ? "系统已初始化" : "系统未初始化")
                .build();
    }

    /**
     * 初始化系统管理员
     * <p>
     * 执行流程：
     * 1. 检查系统是否已初始化，如果已初始化则抛出异常
     * 2. 创建超级管理员角色
     * 3. 创建管理员用户
     * 4. 建立用户-角色关联
     * </p>
     *
     * @param request 初始化请求
     * @return 初始化响应
     */
    @Transactional(rollbackFor = Exception.class)
    public InstallVO installSystem(InstallReq request) {
        // 1. 检查系统是否已初始化
        Long userCount = userMapper.countActiveUsers();

        if (userCount > 0) {
            throw new EzBusinessException(ErrorCode.SYSTEM_ALREADY_INITIALIZED);
        }

        try {
            // 2. 创建超级管理员角色
            SysRole adminRole = createSuperAdminRole();

            // 3. 创建管理员用户
            SysUser adminUser = createAdminUser(request);

            // 4. 建立用户-角色关联
            createUserRoleRelation(adminUser.getUserId(), adminRole.getRoleId());

            log.info("系统初始化成功，管理员账号：{}，昵称：{}", request.getUsername(), request.getNickname());

            return InstallVO.builder()
                    .userId(adminUser.getUserId())
                    .username(adminUser.getUsername())
                    .nickname(adminUser.getNickname())
                    .initTime(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("系统初始化失败", e);
            throw new EzBusinessException(ErrorCode.SYSTEM_INIT_FAILED);
        }
    }

    /**
     * 创建超级管理员角色
     *
     * @return 角色实体
     */
    private SysRole createSuperAdminRole() {
        SysRole adminRole = new SysRole();
        adminRole.setRoleName(SystemConstants.ROLE_NAME_SUPER_ADMIN);
        adminRole.setRoleLabel(SystemConstants.ROLE_LABEL_SUPER_ADMIN);
        adminRole.setRoleSort(SystemConstants.ROLE_SORT_SUPER_ADMIN);
        adminRole.setDataScope(SystemConstants.DATA_SCOPE_ALL);
        adminRole.setStatus(SystemConstants.STATUS_NORMAL);
        adminRole.setDescription(SystemConstants.ROLE_DESC_SUPER_ADMIN);
        adminRole.setCreateBy(SystemConstants.CREATOR_SYSTEM);
        adminRole.setCreateTime(LocalDateTime.now());
        adminRole.setUpdateBy(SystemConstants.CREATOR_SYSTEM);
        adminRole.setUpdateTime(LocalDateTime.now());
        adminRole.setIsDeleted(SystemConstants.NOT_DELETED);

        roleMapper.insert(adminRole);
        return adminRole;
    }

    /**
     * 创建管理员用户
     *
     * @param request 初始化请求
     * @return 用户实体
     */
    private SysUser createAdminUser(InstallReq request) {
        SysUser adminUser = new SysUser();
        adminUser.setUsername(request.getUsername());
        adminUser.setPassword(passwordEncoder.encode(request.getPassword()));
        adminUser.setNickname(request.getNickname());
        adminUser.setEmail(request.getEmail());
        adminUser.setPhoneNumber(request.getPhoneNumber());
        adminUser.setGender(SystemConstants.GENDER_SECRET);
        adminUser.setAvatar("");
        adminUser.setStatus(SystemConstants.STATUS_NORMAL);
        adminUser.setDescription(SystemConstants.DEFAULT_DESCRIPTION);
        adminUser.setCreateBy(SystemConstants.CREATOR_SYSTEM);
        adminUser.setCreateTime(LocalDateTime.now());
        adminUser.setUpdateBy(SystemConstants.CREATOR_SYSTEM);
        adminUser.setUpdateTime(LocalDateTime.now());
        adminUser.setIsDeleted(SystemConstants.NOT_DELETED);

        userMapper.insert(adminUser);
        return adminUser;
    }

    /**
     * 建立用户-角色关联
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     */
    private void createUserRoleRelation(Long userId, Long roleId) {
        SysUserRoleRelation relation = new SysUserRoleRelation();
        relation.setUserId(userId);
        relation.setRoleId(roleId);

        userRoleRelationMapper.insert(relation);
    }
}

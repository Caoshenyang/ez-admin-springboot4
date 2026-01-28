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
     * 初始化超级管理员
     * <p>
     * 执行流程：
     * 1. 检查系统是否已初始化，如果已初始化则抛出异常
     * 2. 创建超级管理员角色（拥有全部数据权限）
     * 3. 创建超级管理员用户并关联角色
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
            SysRole superAdminRole = createSuperAdminRole();

            // 3. 创建超级管理员用户并关联角色
            SysUser superAdminUser = createSuperAdminUser(request, superAdminRole.getRoleId());

            log.info("系统初始化成功，超级管理员账号：{}，昵称：{}", request.getUsername(), request.getNickname());

            return InstallVO.builder()
                    .userId(superAdminUser.getUserId())
                    .username(superAdminUser.getUsername())
                    .nickname(superAdminUser.getNickname())
                    .initTime(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("系统初始化失败", e);
            throw new EzBusinessException(ErrorCode.SYSTEM_INIT_FAILED);
        }
    }

    /**
     * 创建超级管理员角色
     * <p>
     * 角色特性：
     * - 拥有全部数据权限（DATA_SCOPE_ALL）
     * - 状态为正常
     * - 排序为 1（最高优先级）
     * </p>
     *
     * @return 角色实体
     */
    private SysRole createSuperAdminRole() {
        SysRole superAdminRole = new SysRole();
        superAdminRole.setRoleName(SystemConstants.ROLE_NAME_SUPER_ADMIN);
        superAdminRole.setRoleLabel(SystemConstants.ROLE_LABEL_SUPER_ADMIN);
        superAdminRole.setRoleSort(SystemConstants.ROLE_SORT_SUPER_ADMIN);
        superAdminRole.setDataScope(SystemConstants.DATA_SCOPE_ALL);
        superAdminRole.setStatus(SystemConstants.STATUS_NORMAL);
        superAdminRole.setDescription(SystemConstants.ROLE_DESC_SUPER_ADMIN);
        superAdminRole.setCreateBy(SystemConstants.CREATOR_SYSTEM_ID);
        superAdminRole.setCreateTime(LocalDateTime.now());
        superAdminRole.setUpdateBy(SystemConstants.CREATOR_SYSTEM_ID);
        superAdminRole.setUpdateTime(LocalDateTime.now());
        superAdminRole.setIsDeleted(SystemConstants.NOT_DELETED);

        roleMapper.insert(superAdminRole);
        log.info("创建超级管理员角色成功，角色ID：{}", superAdminRole.getRoleId());

        return superAdminRole;
    }

    /**
     * 创建超级管理员用户
     * <p>
     * 用户特性：
     * - 不设置部门和联系方式
     * - 性别默认为保密
     * - 状态为正常
     * - 自动关联超级管理员角色
     * </p>
     *
     * @param request 初始化请求
     * @param roleId  超级管理员角色ID
     * @return 用户实体
     */
    private SysUser createSuperAdminUser(InstallReq request, Long roleId) {
        SysUser superAdminUser = new SysUser();
        superAdminUser.setUsername(request.getUsername());
        superAdminUser.setPassword(passwordEncoder.encode(request.getPassword()));
        superAdminUser.setNickname(request.getNickname());
        superAdminUser.setGender(SystemConstants.GENDER_SECRET);
        superAdminUser.setStatus(SystemConstants.STATUS_NORMAL);
        superAdminUser.setDescription("系统超级管理员，拥有所有权限");
        superAdminUser.setCreateBy(SystemConstants.CREATOR_SYSTEM_ID);
        superAdminUser.setCreateTime(LocalDateTime.now());
        superAdminUser.setUpdateBy(SystemConstants.CREATOR_SYSTEM_ID);
        superAdminUser.setUpdateTime(LocalDateTime.now());
        superAdminUser.setIsDeleted(SystemConstants.NOT_DELETED);

        userMapper.insert(superAdminUser);
        log.info("创建超级管理员用户成功，用户ID：{}", superAdminUser.getUserId());

        // 建立用户-角色关联
        createUserRoleRelation(superAdminUser.getUserId(), roleId);

        return superAdminUser;
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
        log.debug("建立用户-角色关联成功，用户ID：{}，角色ID：{}", userId, roleId);
    }
}

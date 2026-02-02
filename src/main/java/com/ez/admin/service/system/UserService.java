package com.ez.admin.service.system;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ez.admin.common.core.constant.SystemConstants;
import com.ez.admin.common.core.exception.EzBusinessException;
import com.ez.admin.common.core.exception.ErrorCode;
import com.ez.admin.common.data.mapstruct.UserConverter;
import com.ez.admin.common.model.model.PageQuery;
import com.ez.admin.common.model.model.PageVO;
import com.ez.admin.dto.system.user.req.UserCreateReq;
import com.ez.admin.dto.system.user.req.UserPasswordChangeReq;
import com.ez.admin.dto.system.user.req.UserProfileUpdateReq;
import com.ez.admin.dto.system.user.req.UserStatusChangeReq;
import com.ez.admin.dto.system.user.req.UserUpdateReq;
import com.ez.admin.dto.system.user.vo.UserDetailVO;
import com.ez.admin.dto.system.user.vo.UserListVO;
import com.ez.admin.dto.system.vo.RoleInfo;
import com.ez.admin.modules.system.entity.SysRole;
import com.ez.admin.modules.system.entity.SysUser;
import com.ez.admin.modules.system.entity.SysUserRoleRelation;
import com.ez.admin.modules.system.mapper.SysRoleMapper;
import com.ez.admin.modules.system.mapper.SysUserRoleRelationMapper;
import com.ez.admin.modules.system.mapper.SysUserMapper;
import com.ez.admin.modules.system.service.SysUserRoleRelationService;
import com.ez.admin.modules.system.service.SysUserService;
import com.ez.admin.service.cache.UserCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理服务
 * <p>
 * 业务聚合层，组合原子服务实现用户管理的复杂业务逻辑
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysUserRoleRelationMapper userRoleRelationMapper;
    private final SysUserRoleRelationService userRoleRelationService;
    private final PasswordEncoder passwordEncoder;
    private final SysUserService sysUserService;
    private final UserConverter userConverter;
    private final UserCacheService userCacheService;

    /**
     * 创建用户
     *
     * @param request 创建请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void createUser(UserCreateReq request) {
        // 1. 检查用户名是否已存在
        if (userMapper.selectByUsername(request.getUsername()) != null) {
            throw new EzBusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }

        // 2. 检查手机号是否已存在
        if (StringUtils.hasText(request.getPhoneNumber()) && userMapper.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new EzBusinessException(ErrorCode.USER_PHONE_ALREADY_EXISTS);
        }

        // 3. 检查邮箱是否已存在
        if (StringUtils.hasText(request.getEmail()) && userMapper.existsByEmail(request.getEmail())) {
            throw new EzBusinessException(ErrorCode.USER_EMAIL_ALREADY_EXISTS);
        }

        // 4. 创建用户
        SysUser user = buildUser(request);
        userMapper.insert(user);

        // 5. 分配角色
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            assignRoles(user.getUserId(), request.getRoleIds());
        }

        log.info("创建用户成功，用户名：{}", request.getUsername());
    }

    /**
     * 更新用户
     *
     * @param request 更新请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserUpdateReq request) {
        // 1. 检查用户是否存在
        SysUser existUser = userMapper.selectById(request.getUserId());
        if (existUser == null) {
            throw new EzBusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 2. 检查手机号是否被其他用户占用
        if (StringUtils.hasText(request.getPhoneNumber()) && userMapper.existsByPhoneNumberExclude(request.getPhoneNumber(), request.getUserId())) {
            throw new EzBusinessException(ErrorCode.USER_PHONE_ALREADY_EXISTS);
        }

        // 3. 检查邮箱是否被其他用户占用
        if (StringUtils.hasText(request.getEmail()) && userMapper.existsByEmailExclude(request.getEmail(), request.getUserId())) {
            throw new EzBusinessException(ErrorCode.USER_EMAIL_ALREADY_EXISTS);
        }

        // 4. 更新用户信息
        SysUser user = new SysUser();
        user.setUserId(request.getUserId());
        user.setNickname(request.getNickname());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());
        user.setGender(request.getGender());
        user.setAvatar(request.getAvatar());
        user.setDeptId(request.getDeptId());
        user.setStatus(request.getStatus());
        user.setDescription(request.getDescription());
        userMapper.updateById(user);

        // 5. 重新分配角色
        if (request.getRoleIds() != null) {
            assignRoles(request.getUserId(), request.getRoleIds());
        }

        log.info("更新用户成功，用户ID：{}", request.getUserId());
    }

    /**
     * 删除用户
     *
     * @param userId 用户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId) {
        // 1. 检查用户是否存在
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new EzBusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 2. 逻辑删除用户
        sysUserService.removeById(userId);

        // 3. 删除用户角色关联
        userRoleRelationMapper.delete(new LambdaQueryWrapper<SysUserRoleRelation>()
                .eq(SysUserRoleRelation::getUserId, userId));

        log.info("删除用户成功，用户ID：{}", userId);
    }

    /**
     * 批量删除用户
     *
     * @param userIds 用户ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteUsers(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw new EzBusinessException(ErrorCode.VALIDATION_ERROR);
        }

        // 批量逻辑删除用户
        sysUserService.removeByIds(userIds);

        // 批量删除用户角色关联
        userRoleRelationMapper.delete(new LambdaQueryWrapper<SysUserRoleRelation>()
                .in(SysUserRoleRelation::getUserId, userIds));

        log.info("批量删除用户成功，数量：{}", userIds.size());
    }

    /**
     * 根据ID查询用户详情
     *
     * @param userId 用户ID
     * @return 用户详情
     */
    public UserDetailVO getUserById(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new EzBusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 查询用户角色
        List<SysRole> roles = roleMapper.selectList(new LambdaQueryWrapper<SysRole>()
                .in(SysRole::getRoleId, getUserRoleIds(userId)));

        // 使用 MapStruct 转换（通过参数传递角色列表）
        return userConverter.toDetailVOWithRoles(user, userConverter.toRoleVOList(roles));
    }

    /**
     * 分页查询用户列表
     *
     * @param query 分页查询请求
     * @return 分页结果
     */
    public PageVO<UserListVO> getUserPage(PageQuery query) {
        // 执行分页查询
        Page<SysUser> result = userMapper.selectUserPage(query.toMpPage(), query);

        return PageVO.of(result, userConverter::toListVO);
    }

    // ==================== 私有方法 ====================

    /**
     * 构建用户实体
     */
    private SysUser buildUser(UserCreateReq request) {
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setGender(request.getGender() != null ? request.getGender() : SystemConstants.GENDER_SECRET);
        user.setAvatar(request.getAvatar());
        user.setDeptId(request.getDeptId());
        user.setStatus(SystemConstants.STATUS_NORMAL);
        user.setDescription(request.getDescription());
        return user;
    }

    /**
     * 分配角色（覆盖式）
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        // 1. 检查用户是否存在
        if (userMapper.selectById(userId) == null) {
            throw new EzBusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 2. 删除原有角色关联
        userRoleRelationMapper.delete(new LambdaQueryWrapper<SysUserRoleRelation>()
                .eq(SysUserRoleRelation::getUserId, userId));

        // 3. 批量插入新关联
        if (roleIds != null && !roleIds.isEmpty()) {
            List<SysUserRoleRelation> relations = roleIds.stream()
                    .map(roleId -> {
                        SysUserRoleRelation relation = new SysUserRoleRelation();
                        relation.setUserId(userId);
                        relation.setRoleId(roleId);
                        return relation;
                    })
                    .toList();

            // 批量插入（使用 MyBatis-Plus IService.saveBatch，底层 JDBC Batch 优化）
            userRoleRelationService.saveBatch(relations);
        }

        // 刷新用户角色缓存
        List<RoleInfo> roleInfoList = roleIds != null ? roleMapper.selectList(
                        new LambdaQueryWrapper<SysRole>().in(SysRole::getRoleId, roleIds))
                .stream()
                .map(role -> RoleInfo.builder()
                        .roleId(role.getRoleId())
                        .roleLabel(role.getRoleLabel())
                        .roleName(role.getRoleName())
                        .build())
                .toList() : List.of();
        userCacheService.saveUserRoles(userId, roleInfoList);

        log.info("用户分配角色成功，用户ID：{}，角色数量：{}", userId, roleIds != null ? roleIds.size() : 0);
    }

    /**
     * 获取用户角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    public List<Long> getUserRoleIds(Long userId) {
        return userRoleRelationMapper.selectList(new LambdaQueryWrapper<SysUserRoleRelation>()
                        .eq(SysUserRoleRelation::getUserId, userId))
                .stream()
                .map(SysUserRoleRelation::getRoleId)
                .collect(Collectors.toList());
    }

    /**
     * 上传用户头像
     *
     * @param userId 用户ID
     * @param avatarUrl 头像URL
     */
    @Transactional(rollbackFor = Exception.class)
    public void uploadAvatar(Long userId, String avatarUrl) {
        // 1. 检查用户是否存在
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new EzBusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 2. 更新用户头像
        SysUser updateUser = new SysUser();
        updateUser.setUserId(userId);
        updateUser.setAvatar(avatarUrl);
        userMapper.updateById(updateUser);

        log.info("用户头像上传成功，用户ID：{}，头像：{}", userId, avatarUrl);
    }

    /**
     * 修改用户个人信息
     *
     * @param userId 用户ID
     * @param request 个人信息修改请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(Long userId, UserProfileUpdateReq request) {
        // 1. 检查用户是否存在
        SysUser existUser = userMapper.selectById(userId);
        if (existUser == null) {
            throw new EzBusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 2. 检查手机号是否被其他用户占用
        if (StringUtils.hasText(request.getPhoneNumber()) &&
                userMapper.existsByPhoneNumberExclude(request.getPhoneNumber(), userId)) {
            throw new EzBusinessException(ErrorCode.USER_PHONE_ALREADY_EXISTS);
        }

        // 3. 检查邮箱是否被其他用户占用
        if (StringUtils.hasText(request.getEmail()) &&
                userMapper.existsByEmailExclude(request.getEmail(), userId)) {
            throw new EzBusinessException(ErrorCode.USER_EMAIL_ALREADY_EXISTS);
        }

        // 4. 更新用户信息
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setGender(request.getGender());
        user.setDescription(request.getDescription());
        userMapper.updateById(user);

        log.info("用户个人信息修改成功，用户ID：{}", userId);
    }

    /**
     * 修改用户密码
     *
     * @param userId 用户ID
     * @param request 密码修改请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, UserPasswordChangeReq request) {
        // 1. 检查用户是否存在
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new EzBusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 2. 验证旧密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new EzBusinessException(ErrorCode.USER_PASSWORD_ERROR, "旧密码错误");
        }

        // 3. 检查新密码是否与旧密码相同
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new EzBusinessException(ErrorCode.VALIDATION_ERROR, "新密码不能与旧密码相同");
        }

        // 4. 更新密码
        SysUser updateUser = new SysUser();
        updateUser.setUserId(userId);
        updateUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userMapper.updateById(updateUser);

        // 5. 清除用户登录状态（强制重新登录）
        StpUtil.logout(userId);

        log.info("用户密码修改成功，用户ID：{}", userId);
    }

    /**
     * 切换用户状态
     *
     * @param request 状态切换请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(UserStatusChangeReq request) {
        // 1. 检查用户是否存在
        SysUser user = userMapper.selectById(request.getUserId());
        if (user == null) {
            throw new EzBusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 2. 更新用户状态
        SysUser updateUser = new SysUser();
        updateUser.setUserId(request.getUserId());
        updateUser.setStatus(request.getStatus());
        userMapper.updateById(updateUser);

        // 3. 如果禁用用户，清除其登录状态
        if (request.getStatus() == SystemConstants.STATUS_DISABLED) {
            StpUtil.logout(request.getUserId());
        }

        log.info("用户状态切换成功，用户ID：{}，状态：{}", request.getUserId(), request.getStatus());
    }

}
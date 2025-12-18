package com.ezadmin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ezadmin.common.exception.ExceptionEnum;
import com.ezadmin.common.exception.EzAdminException;
import com.ezadmin.common.response.page.PageQuery;
import com.ezadmin.common.response.page.PageVO;
import com.ezadmin.model.dto.UserCreateDTO;
import com.ezadmin.model.dto.UserUpdateDTO;
import com.ezadmin.model.mapstruct.MsUserMapper;
import com.ezadmin.model.query.UserQuery;
import com.ezadmin.model.vo.UserDetailVO;
import com.ezadmin.model.vo.UserListVO;
import com.ezadmin.modules.system.entity.Role;
import com.ezadmin.modules.system.entity.User;
import com.ezadmin.modules.system.service.IRoleService;
import com.ezadmin.modules.system.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 类名: UserManagementService
 * 功能描述: 用户管理
 *
 * @author shenyang
 * @since 2025/3/19 15:42
 */
@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final IUserService userService;
    private final IRoleService roleService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 新增用户
     *
     * @param userCreateDTO 用户创建DTO
     */
    public void createUser(UserCreateDTO userCreateDTO) {
        // 检查用户名是否已存在
        User existingUser = userService.selectUserByUsername(userCreateDTO.getUsername());
        if (existingUser != null) {
            throw new EzAdminException(ExceptionEnum.USERNAME_EXISTS);
        }
        // 密码加密
        userCreateDTO.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));
        User newUser = MsUserMapper.INSTANCE.userCreateDTO2User(userCreateDTO);
        userService.save(newUser);
    }

    /**
     * 更新用户
     *
     * @param userUpdateDTO 用户更新DTO
     */
    public void updateUser(UserUpdateDTO userUpdateDTO) {
        User user = MsUserMapper.INSTANCE.userUpdateDTO2User(userUpdateDTO);
        userService.updateById(user);
    }

    /**
     * 删除用户
     *
     * @param userId userId
     */
    public void deleteUser(Long userId) {
        userService.removeById(userId);
    }

    /**
     * 根据ID查询用户
     *
     * @param userId userId
     * @return UserDetailVO
     */
    public UserDetailVO findUserById(Long userId) {
        User user = userService.getById(userId);
        UserDetailVO userDetailVO = MsUserMapper.INSTANCE.user2UserDetailVO(user);
        // 查询用户角色
        List<Role> roles = roleService.selectRoleListByUserId(user.getUserId());
        // 收集所有角色Id，并且转成字符串数组
        List<String> roleIds = roles.stream().map(role -> String.valueOf(role.getRoleId())).toList();
        userDetailVO.setRoleIds(roleIds);
        return userDetailVO;
    }

    public PageVO<UserListVO> findPage(PageQuery<UserQuery> userQuery) {
        // 将查询对象 转换为 Mybatis Plus 的 Page 对象
        Page<UserListVO> page = userQuery.toMpPage();
        UserQuery search = userQuery.getSearch();
        // 查询
        userService.findPage(page, search);
        // 将 Mybatis Plus 的 Page 对象 转换为 PageVO
        return PageVO.of(page, UserListVO.class);
    }


}

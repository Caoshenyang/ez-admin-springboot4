package com.ez.admin.service.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ez.admin.common.core.exception.ErrorCode;
import com.ez.admin.common.core.exception.EzBusinessException;
import com.ez.admin.common.data.mapstruct.DeptConverter;
import com.ez.admin.common.data.tree.TreeBuilder;
import com.ez.admin.dto.system.dept.req.DeptCreateReq;
import com.ez.admin.dto.system.dept.req.DeptUpdateReq;
import com.ez.admin.dto.system.dept.vo.DeptDetailVO;
import com.ez.admin.modules.system.entity.SysDept;
import com.ez.admin.modules.system.entity.SysUser;
import com.ez.admin.modules.system.mapper.SysDeptMapper;
import com.ez.admin.modules.system.mapper.SysUserMapper;
import com.ez.admin.modules.system.service.SysDeptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 部门管理服务
 * <p>
 * 业务聚合层，组合原子服务实现部门管理的复杂业务逻辑
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeptService {

    private final SysDeptMapper deptMapper;
    private final SysUserMapper userMapper;
    private final SysDeptService sysDeptService;
    private final DeptConverter deptConverter;

    /**
     * 创建部门
     *
     * @param request 创建请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void createDept(DeptCreateReq request) {
        // 1. 如果有父部门ID，检查父部门是否存在
        if (request.getParentId() != null && !request.getParentId().equals(0L)) {
            SysDept parentDept = deptMapper.selectById(request.getParentId());
            if (parentDept == null) {
                throw new EzBusinessException(ErrorCode.DEPT_NOT_FOUND);
            }
        }

        // 2. 构建祖级路径
        String ancestors = buildAncestors(request.getParentId());

        // 3. 创建部门
        SysDept dept = buildDept(request, ancestors);
        deptMapper.insert(dept);

        log.info("创建部门成功，部门名称：{}", request.getDeptName());
    }

    /**
     * 更新部门
     *
     * @param request 更新请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateDept(DeptUpdateReq request) {
        // 1. 检查部门是否存在
        SysDept existDept = deptMapper.selectById(request.getDeptId());
        if (existDept == null) {
            throw new EzBusinessException(ErrorCode.DEPT_NOT_FOUND);
        }

        // 2. 如果有父部门ID，检查父部门是否存在
        if (request.getParentId() != null && !request.getParentId().equals(0L)) {
            // 不能将自己设置为父部门
            if (request.getParentId().equals(request.getDeptId())) {
                throw new EzBusinessException(ErrorCode.DEPT_NOT_FOUND);
            }

            SysDept parentDept = deptMapper.selectById(request.getParentId());
            if (parentDept == null) {
                throw new EzBusinessException(ErrorCode.DEPT_NOT_FOUND);
            }
        }

        // 3. 更新部门信息
        SysDept dept = new SysDept();
        dept.setDeptId(request.getDeptId());
        dept.setDeptName(request.getDeptName());
        dept.setDeptSort(request.getDeptSort());
        dept.setParentId(request.getParentId());
        dept.setStatus(request.getStatus());
        dept.setDescription(request.getDescription());

        // 如果父部门发生变化，需要更新祖级路径
        if (!existDept.getParentId().equals(request.getParentId())) {
            String ancestors = buildAncestors(request.getParentId());
            dept.setAncestors(ancestors);
        }

        deptMapper.updateById(dept);

        log.info("更新部门成功，部门ID：{}", request.getDeptId());
    }

    /**
     * 删除部门
     *
     * @param deptId 部门ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteDept(Long deptId) {
        // 1. 检查部门是否存在
        SysDept dept = deptMapper.selectById(deptId);
        if (dept == null) {
            throw new EzBusinessException(ErrorCode.DEPT_NOT_FOUND);
        }

        // 2. 检查部门是否存在子部门
        long childCount = deptMapper.selectCount(new LambdaQueryWrapper<SysDept>()
                .eq(SysDept::getParentId, deptId));
        if (childCount > 0) {
            throw new EzBusinessException(ErrorCode.DEPT_HAS_CHILDREN);
        }

        // 3. 检查部门下是否存在用户
        long userCount = userMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getDeptId, deptId));
        if (userCount > 0) {
            throw new EzBusinessException(ErrorCode.DEPT_HAS_USERS);
        }

        // 4. 逻辑删除部门
        sysDeptService.removeById(deptId);

        log.info("删除部门成功，部门ID：{}", deptId);
    }

    /**
     * 根据ID查询部门详情
     *
     * @param deptId 部门ID
     * @return 部门详情
     */
    public DeptDetailVO getDeptById(Long deptId) {
        SysDept dept = deptMapper.selectById(deptId);
        if (dept == null) {
            throw new EzBusinessException(ErrorCode.DEPT_NOT_FOUND);
        }

        return deptConverter.toDetailVO(dept);
    }

    /**
     * 查询部门树
     *
     * @param status 状态过滤（null=全部，0=停用，1=正常）
     * @return 部门树（完整的树形结构）
     */
    public List<com.ez.admin.dto.system.dept.vo.DeptTreeVO> getDeptTree(Integer status) {
        // 1. 查询所有部门
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<SysDept>()
                .orderByAsc(SysDept::getDeptSort);

        // 如果指定了状态，添加状态过滤
        if (status != null) {
            wrapper.eq(SysDept::getStatus, status);
        }

        List<SysDept> allDepts = deptMapper.selectList(wrapper);

        // 2. 转换为 TreeVO
        List<com.ez.admin.dto.system.dept.vo.DeptTreeVO> deptTreeVOs = deptConverter.toTreeVOList(allDepts);

        // 3. 构建树形结构
        return TreeBuilder.build(deptTreeVOs);
    }

    /**
     * 查询部门下的用户列表
     *
     * @param deptId 部门ID
     * @return 用户列表
     */
    public List<com.ez.admin.dto.system.dept.vo.DeptUserVO> getDeptUsers(Long deptId) {
        // 1. 检查部门是否存在
        SysDept dept = deptMapper.selectById(deptId);
        if (dept == null) {
            throw new EzBusinessException(ErrorCode.DEPT_NOT_FOUND);
        }

        // 2. 查询部门下的所有用户
        List<SysUser> users = userMapper.selectList(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getDeptId, deptId)
                .orderByDesc(SysUser::getCreateTime));

        // 3. 转换为 VO
        return users.stream()
                .map(user -> com.ez.admin.dto.system.dept.vo.DeptUserVO.builder()
                        .userId(user.getUserId())
                        .username(user.getUsername())
                        .nickname(user.getNickname())
                        .email(user.getEmail())
                        .phoneNumber(user.getPhoneNumber())
                        .gender(user.getGender())
                        .avatar(user.getAvatar())
                        .status(user.getStatus())
                        .loginIp(user.getLoginIp())
                        .loginDate(user.getLoginDate())
                        .createTime(user.getCreateTime())
                        .build())
                .toList();
    }

    // ==================== 私有方法 ====================

    /**
     * 构建祖级路径
     */
    private String buildAncestors(Long parentId) {
        if (parentId == null || parentId.equals(0L)) {
            return "0";
        }

        SysDept parentDept = deptMapper.selectById(parentId);
        if (parentDept == null) {
            return "0";
        }

        return parentDept.getAncestors() + "," + parentId;
    }

    /**
     * 构建部门实体
     */
    private SysDept buildDept(DeptCreateReq request, String ancestors) {
        SysDept dept = new SysDept();
        dept.setDeptName(request.getDeptName());
        dept.setDeptSort(request.getDeptSort());
        dept.setParentId(request.getParentId());
        dept.setAncestors(ancestors);
        dept.setStatus(request.getStatus());
        dept.setDescription(request.getDescription());
        return dept;
    }
}

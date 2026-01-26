package com.ez.admin.service.dept;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ez.admin.common.exception.EzBusinessException;
import com.ez.admin.common.exception.ErrorCode;
import com.ez.admin.common.mapstruct.DeptConverter;
import com.ez.admin.dto.dept.req.DeptCreateReq;
import com.ez.admin.dto.dept.req.DeptUpdateReq;
import com.ez.admin.dto.dept.vo.DeptVO;
import com.ez.admin.modules.system.entity.SysDept;
import com.ez.admin.modules.system.mapper.SysDeptMapper;
import com.ez.admin.modules.system.mapper.SysUserMapper;
import com.ez.admin.modules.system.service.SysDeptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        long userCount = userMapper.selectCount(new LambdaQueryWrapper<>()
                .eq("dept_id", deptId));
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
    public DeptVO getDeptById(Long deptId) {
        SysDept dept = deptMapper.selectById(deptId);
        if (dept == null) {
            throw new EzBusinessException(ErrorCode.DEPT_NOT_FOUND);
        }

        return deptConverter.toVO(dept);
    }

    /**
     * 查询部门树
     *
     * @return 部门树（完整的树形结构）
     */
    public java.util.List<DeptVO> getDeptTree() {
        // 1. 查询所有部门
        java.util.List<SysDept> allDepts = deptMapper.selectList(new LambdaQueryWrapper<SysDept>()
                .orderByAsc(SysDept::getDeptSort));

        // 2. 转换为 VO
        java.util.List<DeptVO> deptVOs = deptConverter.toVOList(allDepts);

        // 3. 构建树形结构（暂时预留，返回平铺列表）
        // TODO: 后续实现树形结构构建逻辑
        return deptVOs;
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

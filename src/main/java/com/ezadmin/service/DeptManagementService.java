package com.ezadmin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezadmin.common.response.tree.TreeBuilder;
import com.ezadmin.model.dto.DeptCreateDTO;
import com.ezadmin.model.dto.DeptParentTreeDTO;
import com.ezadmin.model.dto.DeptUpdateDTO;
import com.ezadmin.model.mapstruct.MsDeptMapper;
import com.ezadmin.model.query.DeptQuery;
import com.ezadmin.model.vo.DeptTreeVO;
import com.ezadmin.modules.system.entity.Dept;
import com.ezadmin.modules.system.service.IDeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 部门管理业务
 */
@Service
@RequiredArgsConstructor
public class DeptManagementService {

    private final IDeptService deptService;

    /**
     * 部门树
     */
    public List<DeptTreeVO> tree(DeptQuery query) {
        LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<>();
        if (query != null) {
            wrapper = query.buildWrapper();
        }
        wrapper.orderByAsc(Dept::getDeptSort);
        List<Dept> depts = deptService.list(wrapper);
        List<DeptTreeVO> nodes = depts.stream()
            .map(this::toTreeVO)
            .toList();
        return TreeBuilder.buildTree(nodes);
    }

    /**
     * 获取父节点树形结构（用于表单上级部门选择）
     */
    public List<DeptTreeVO> parentTree(DeptParentTreeDTO dto) {
        LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Dept::getDeptSort);

        // 如果指定了排除ID，则排除该部门及其所有子节点
        if (dto != null && dto.getExcludeId() != null) {
            // 排除自己：dept_id != excludeId
            wrapper.ne(Dept::getDeptId, dto.getExcludeId());
            // 排除子节点：ancestors 不包含 /excludeId/
            wrapper.notLike(Dept::getAncestors, "/" + dto.getExcludeId() + "/");
        }

        List<Dept> depts = deptService.list(wrapper);
        List<DeptTreeVO> nodes = depts.stream()
            .map(this::toTreeVO)
            .toList();
        return TreeBuilder.buildTree(nodes);
    }

    public Dept detail(Long deptId) {
        return deptService.getById(deptId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void create(DeptCreateDTO dto) {
        Dept dept = MsDeptMapper.INSTANCE.deptCreateDTO2Dept(dto);

        // 根据父级部门计算祖级路径
        if (dto.getParentId() != null && dto.getParentId() != 0) {
            Dept parentDept = deptService.getById(dto.getParentId());
            if (parentDept != null) {
                dept.setAncestors(parentDept.getAncestors() + parentDept.getDeptId() + "/");
            }
        } else {
            // 顶级部门
            dept.setAncestors("/");
        }

        deptService.save(dept);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(DeptUpdateDTO dto) {
        Dept dept = MsDeptMapper.INSTANCE.deptUpdateDTO2Dept(dto);

        // 如果修改了父级部门，需要重新计算祖级路径
        if (dto.getParentId() != null) {
            if (dto.getParentId() == 0) {
                // 设为顶级部门
                dept.setAncestors("/");
            } else {
                // 查询新的父级部门
                Dept parentDept = deptService.getById(dto.getParentId());
                if (parentDept != null) {
                    dept.setAncestors(parentDept.getAncestors() + parentDept.getDeptId() + "/");
                }
            }

            // 递归更新所有子部门的祖级路径
            updateChildrenAncestors(dto.getDeptId(), dept.getAncestors() + dto.getDeptId() + "/");
        }

        deptService.updateById(dept);
    }

    /**
     * 递归更新子部门的祖级路径
     */
    private void updateChildrenAncestors(Long parentId, String newAncestors) {
        List<Dept> children = deptService.lambdaQuery()
            .eq(Dept::getParentId, parentId)
            .list();

        for (Dept child : children) {
            child.setAncestors(newAncestors);
            deptService.updateById(child);

            // 递归更新子部门的子部门
            updateChildrenAncestors(child.getDeptId(), newAncestors + child.getDeptId() + "/");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long deptId) {
        long children = deptService.lambdaQuery().eq(Dept::getParentId, deptId).count();
        if (children > 0) {
            throw new IllegalStateException("请先删除子部门");
        }
        deptService.removeById(deptId);
    }

    private DeptTreeVO toTreeVO(Dept dept) {
        return MsDeptMapper.INSTANCE.dept2DeptTreeVO(dept);
    }
}

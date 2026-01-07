package com.ezadmin.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezadmin.common.response.tree.TreeBuilder;
import com.ezadmin.model.dto.DeptCreateDTO;
import com.ezadmin.model.dto.DeptParentTreeDTO;
import com.ezadmin.model.dto.DeptUpdateDTO;
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
        Dept dept = BeanUtil.copyProperties(dto, Dept.class);
        deptService.save(dept);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(DeptUpdateDTO dto) {
        Dept dept = BeanUtil.copyProperties(dto, Dept.class);
        deptService.updateById(dept);
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
        return BeanUtil.copyProperties(dept, DeptTreeVO.class);
    }
}

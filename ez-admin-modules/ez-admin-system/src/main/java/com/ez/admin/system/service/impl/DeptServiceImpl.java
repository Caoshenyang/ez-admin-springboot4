package com.ez.admin.system.service.impl;

import com.ez.admin.system.entity.Dept;
import com.ez.admin.system.mapper.DeptMapper;
import com.ez.admin.system.service.IDeptService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 部门信息表 服务实现类
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-19
 */
@Service
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements IDeptService {

}

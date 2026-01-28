package com.ez.admin.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ez.admin.modules.system.entity.TestOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 测试订单 Mapper 接口
 * <p>
 * 用于验证数据权限功能
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-28
 */
@Mapper
public interface TestOrderMapper extends BaseMapper<TestOrder> {

}

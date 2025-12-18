package com.ezadmin.modules.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezadmin.modules.system.entity.Menu;
import com.ezadmin.modules.system.mapper.MenuMapper;
import com.ezadmin.modules.system.service.IMenuService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 菜单信息表 服务实现类
 * </p>
 *
 * @author shenyang
 * @since 2025-12-17
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {

}

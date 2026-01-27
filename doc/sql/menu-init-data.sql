-- ============================================================================
-- EZ-ADMIN 菜单数据初始化脚本
-- 说明：初始化系统管理模块的菜单数据
-- ============================================================================

-- 清空现有菜单数据（可选，仅在开发环境使用）
-- TRUNCATE TABLE ez_admin_sys_role_menu_relation CASCADE;
-- DELETE FROM ez_admin_sys_menu WHERE menu_id > 0;

-- ============================================================================
-- 1. 系统管理（一级目录）
-- ============================================================================
INSERT INTO ez_admin_sys_menu (
    menu_id, menu_name, menu_icon, menu_label, parent_id, menu_sort,
    menu_type, menu_perm, route_path, route_name, component_path,
    api_route, api_method, status, create_time, update_time,
    is_deleted, description
) VALUES (
    1, -- 菜单ID
    '系统管理', -- 菜单名称
    'setting', -- 菜单图标
    'system', -- 菜单标识
    0, -- 父级菜单ID（0表示根菜单）
    1, -- 菜单排序
    1, -- 菜单类型（1 目录 2 菜单 3 按钮）
    '', -- 权限标识
    '/system', -- 路由地址
    'System', -- 路由名称
    'Layout', -- 组件路径
    NULL, -- API路由
    NULL, -- HTTP方法
    1, -- 菜单状态（0 停用 1 正常）
    CURRENT_TIMESTAMP, -- 创建时间
    CURRENT_TIMESTAMP, -- 更新时间
    0, -- 是否删除（0 正常 1 已删除）
    '系统管理目录' -- 描述信息
);

-- ============================================================================
-- 2. 用户管理（二级菜单 + 按钮）
-- ============================================================================

-- 2.1 用户管理菜单
INSERT INTO ez_admin_sys_menu (
    menu_id, menu_name, menu_icon, menu_label, parent_id, menu_sort,
    menu_type, menu_perm, route_path, route_name, component_path,
    api_route, api_method, status, create_time, update_time,
    is_deleted, description
) VALUES (
    100,
    '用户管理',
    'user',
    'system:user',
    1,
    1,
    2,
    'system:user:list',
    '/system/user',
    'UserList',
    'system/user/index',
    NULL,
    NULL,
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    '用户管理菜单'
);

-- 2.2 用户管理 - 按钮
-- 创建用户
INSERT INTO ez_admin_sys_menu (
    menu_id, menu_name, menu_icon, menu_label, parent_id, menu_sort,
    menu_type, menu_perm, route_path, route_name, component_path,
    api_route, api_method, status, create_time, update_time,
    is_deleted, description
) VALUES (
    1001,
    '创建用户',
    '',
    'system:user:create',
    100,
    1,
    3,
    'system:user:create',
    '',
    '',
    '',
    '/api/user',
    'POST',
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    '创建用户按钮'
);

-- 更新用户
INSERT INTO ez_admin_sys_menu (
    menu_id, menu_name, menu_icon, menu_label, parent_id, menu_sort,
    menu_type, menu_perm, route_path, route_name, component_path,
    api_route, api_method, status, create_time, update_time,
    is_deleted, description
) VALUES (
    1002,
    '更新用户',
    '',
    'system:user:update',
    100,
    2,
    3,
    'system:user:update',
    '',
    '',
    '',
    '/api/user',
    'PUT',
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    '更新用户按钮'
);

-- 删除用户
INSERT INTO ez_admin_sys_menu (
    menu_id, menu_name, menu_icon, menu_label, parent_id, menu_sort,
    menu_type, menu_perm, route_path, route_name, component_path,
    api_route, api_method, status, create_time, update_time,
    is_deleted, description
) VALUES (
    1003,
    '删除用户',
    '',
    'system:user:delete',
    100,
    3,
    3,
    'system:user:delete',
    '',
    '',
    '',
    '/api/user',
    'DELETE',
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    '删除用户按钮'
);

-- 查询用户
INSERT INTO ez_admin_sys_menu (
    menu_id, menu_name, menu_icon, menu_label, parent_id, menu_sort,
    menu_type, menu_perm, route_path, route_name, component_path,
    api_route, api_method, status, create_time, update_time,
    is_deleted, description
) VALUES (
    1004,
    '查询用户',
    '',
    'system:user:query',
    100,
    4,
    3,
    'system:user:query',
    '',
    '',
    '',
    '/api/user',
    'GET',
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    '查询用户按钮'
);

-- 分配角色
INSERT INTO ez_admin_sys_menu (
    menu_id, menu_name, menu_icon, menu_label, parent_id, menu_sort,
    menu_type, menu_perm, route_path, route_name, component_path,
    api_route, api_method, status, create_time, update_time,
    is_deleted, description
) VALUES (
    1005,
    '分配角色',
    '',
    'system:user:assign',
    100,
    5,
    3,
    'system:user:assign',
    '',
    '',
    '',
    '/api/user/assign/roles',
    'POST',
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    '分配角色按钮'
);

-- ============================================================================
-- 3. 角色管理（二级菜单 + 按钮）
-- ============================================================================

-- 3.1 角色管理菜单
INSERT INTO ez_admin_sys_menu (
    menu_id, menu_name, menu_icon, menu_label, parent_id, menu_sort,
    menu_type, menu_perm, route_path, route_name, component_path,
    api_route, api_method, status, create_time, update_time,
    is_deleted, description
) VALUES (
    200,
    '角色管理',
    'team',
    'system:role',
    1,
    2,
    2,
    'system:role:list',
    '/system/role',
    'RoleList',
    'system/role/index',
    NULL,
    NULL,
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    '角色管理菜单'
);

-- 3.2 角色管理 - 按钮
-- 创建角色
INSERT INTO ez_admin_sys_menu (
    menu_id, menu_name, menu_icon, menu_label, parent_id, menu_sort,
    menu_type, menu_perm, route_path, route_name, component_path,
    api_route, api_method, status, create_time, update_time,
    is_deleted, description
) VALUES (
    2001,
    '创建角色',
    '',
    'system:role:create',
    200,
    1,
    3,
    'system:role:create',
    '',
    '',
    '',
    '/api/role',
    'POST',
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    '创建角色按钮'
);

-- 更新角色
INSERT INTO ez_admin_sys_menu (
    menu_id, menu_name, menu_icon, menu_label, parent_id, menu_sort,
    menu_type, menu_perm, route_path, route_name, component_path,
    api_route, api_method, status, create_time, update_time,
    is_deleted, description
) VALUES (
    2002,
    '更新角色',
    '',
    'system:role:update',
    200,
    2,
    3,
    'system:role:update',
    '',
    '',
    '',
    '/api/role',
    'PUT',
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    '更新角色按钮'
);

-- 删除角色
INSERT INTO ez_admin_sys_menu (
    menu_id, menu_name, menu_icon, menu_label, parent_id, menu_sort,
    menu_type, menu_perm, route_path, route_name, component_path,
    api_route, api_method, status, create_time, update_time,
    is_deleted, description
) VALUES (
    2003,
    '删除角色',
    '',
    'system:role:delete',
    200,
    3,
    3,
    'system:role:delete',
    '',
    '',
    '',
    '/api/role',
    'DELETE',
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    '删除角色按钮'
);

-- 查询角色
INSERT INTO ez_admin_sys_menu (
    menu_id, menu_name, menu_icon, menu_label, parent_id, menu_sort,
    menu_type, menu_perm, route_path, route_name, component_path,
    api_route, api_method, status, create_time, update_time,
    is_deleted, description
) VALUES (
    2004,
    '查询角色',
    '',
    'system:role:query',
    200,
    4,
    3,
    'system:role:query',
    '',
    '',
    '',
    '/api/role',
    'GET',
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    '查询角色按钮'
);

-- 分配菜单
INSERT INTO ez_admin_sys_menu (
    menu_id, menu_name, menu_icon, menu_label, parent_id, menu_sort,
    menu_type, menu_perm, route_path, route_name, component_path,
    api_route, api_method, status, create_time, update_time,
    is_deleted, description
) VALUES (
    2005,
    '分配菜单',
    '',
    'system:role:assign',
    200,
    5,
    3,
    'system:role:assign',
    '',
    '',
    '',
    '/api/role/assign/menus',
    'POST',
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    '分配菜单按钮'
);

-- ============================================================================
-- 4. 菜单管理（二级菜单 + 按钮）
-- ============================================================================

-- 4.1 菜单管理菜单
INSERT INTO ez_admin_sys_menu (
    menu_id, menu_name, menu_icon, menu_label, parent_id, menu_sort,
    menu_type, menu_perm, route_path, route_name, component_path,
    api_route, api_method, status, create_time, update_time,
    is_deleted, description
) VALUES (
    300,
    '菜单管理',
    'menu',
    'system:menu',
    1,
    3,
    2,
    'system:menu:list',
    '/system/menu',
    'MenuList',
    'system/menu/index',
    NULL,
    NULL,
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    '菜单管理菜单'
);

-- 4.2 菜单管理 - 按钮
-- 创建菜单
INSERT INTO ez_admin_sys_menu (
    menu_id, menu_name, menu_icon, menu_label, parent_id, menu_sort,
    menu_type, menu_perm, route_path, route_name, component_path,
    api_route, api_method, status, create_time, update_time,
    is_deleted, description
) VALUES (
    3001,
    '创建菜单',
    '',
    'system:menu:create',
    300,
    1,
    3,
    'system:menu:create',
    '',
    '',
    '',
    '/api/menu',
    'POST',
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    '创建菜单按钮'
);

-- 更新菜单
INSERT INTO ez_admin_sys_menu (
    menu_id, menu_name, menu_icon, menu_label, parent_id, menu_sort,
    menu_type, menu_perm, route_path, route_name, component_path,
    api_route, api_method, status, create_time, update_time,
    is_deleted, description
) VALUES (
    3002,
    '更新菜单',
    '',
    'system:menu:update',
    300,
    2,
    3,
    'system:menu:update',
    '',
    '',
    '',
    '/api/menu',
    'PUT',
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    '更新菜单按钮'
);

-- 删除菜单
INSERT INTO ez_admin_sys_menu (
    menu_id, menu_name, menu_icon, menu_label, parent_id, menu_sort,
    menu_type, menu_perm, route_path, route_name, component_path,
    api_route, api_method, status, create_time, update_time,
    is_deleted, description
) VALUES (
    3003,
    '删除菜单',
    '',
    'system:menu:delete',
    300,
    3,
    3,
    'system:menu:delete',
    '',
    '',
    '',
    '/api/menu',
    'DELETE',
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    '删除菜单按钮'
);

-- 查询菜单
INSERT INTO ez_admin_sys_menu (
    menu_id, menu_name, menu_icon, menu_label, parent_id, menu_sort,
    menu_type, menu_perm, route_path, route_name, component_path,
    api_route, api_method, status, create_time, update_time,
    is_deleted, description
) VALUES (
    3004,
    '查询菜单',
    '',
    'system:menu:query',
    300,
    4,
    3,
    'system:menu:query',
    '',
    '',
    '',
    '/api/menu',
    'GET',
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    '查询菜单按钮'
);

-- ============================================================================
-- 5. 部门管理（二级菜单 + 按钮）
-- ============================================================================

-- 5.1 部门管理菜单
INSERT INTO ez_admin_sys_menu (
    menu_id, menu_name, menu_icon, menu_label, parent_id, menu_sort,
    menu_type, menu_perm, route_path, route_name, component_path,
    api_route, api_method, status, create_time, update_time,
    is_deleted, description
) VALUES (
    400,
    '部门管理',
    'apartment',
    'system:dept',
    1,
    4,
    2,
    'system:dept:list',
    '/system/dept',
    'DeptList',
    'system/dept/index',
    NULL,
    NULL,
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    '部门管理菜单'
);

-- 5.2 部门管理 - 按钮
-- 创建部门
INSERT INTO ez_admin_sys_menu (
    menu_id, menu_name, menu_icon, menu_label, parent_id, menu_sort,
    menu_type, menu_perm, route_path, route_name, component_path,
    api_route, api_method, status, create_time, update_time,
    is_deleted, description
) VALUES (
    4001,
    '创建部门',
    '',
    'system:dept:create',
    400,
    1,
    3,
    'system:dept:create',
    '',
    '',
    '',
    '/api/dept',
    'POST',
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    '创建部门按钮'
);

-- 更新部门
INSERT INTO ez_admin_sys_menu (
    menu_id, menu_name, menu_icon, menu_label, parent_id, menu_sort,
    menu_type, menu_perm, route_path, route_name, component_path,
    api_route, api_method, status, create_time, update_time,
    is_deleted, description
) VALUES (
    4002,
    '更新部门',
    '',
    'system:dept:update',
    400,
    2,
    3,
    'system:dept:update',
    '',
    '',
    '',
    '/api/dept',
    'PUT',
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    '更新部门按钮'
);

-- 删除部门
INSERT INTO ez_admin_sys_menu (
    menu_id, menu_name, menu_icon, menu_label, parent_id, menu_sort,
    menu_type, menu_perm, route_path, route_name, component_path,
    api_route, api_method, status, create_time, update_time,
    is_deleted, description
) VALUES (
    4003,
    '删除部门',
    '',
    'system:dept:delete',
    400,
    3,
    3,
    'system:dept:delete',
    '',
    '',
    '',
    '/api/dept',
    'DELETE',
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    '删除部门按钮'
);

-- 查询部门
INSERT INTO ez_admin_sys_menu (
    menu_id, menu_name, menu_icon, menu_label, parent_id, menu_sort,
    menu_type, menu_perm, route_path, route_name, component_path,
    api_route, api_method, status, create_time, update_time,
    is_deleted, description
) VALUES (
    4004,
    '查询部门',
    '',
    'system:dept:query',
    400,
    4,
    3,
    'system:dept:query',
    '',
    '',
    '',
    '/api/dept',
    'GET',
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    '查询部门按钮'
);

-- ============================================================================
-- 6. 字典管理（二级菜单 + 按钮）
-- ============================================================================

-- 6.1 字典管理菜单
INSERT INTO ez_admin_sys_menu (
    menu_id, menu_name, menu_icon, menu_label, parent_id, menu_sort,
    menu_type, menu_perm, route_path, route_name, component_path,
    api_route, api_method, status, create_time, update_time,
    is_deleted, description
) VALUES (
    500,
    '字典管理',
    'dict',
    'system:dict',
    1,
    5,
    2,
    'system:dict:list',
    '/system/dict',
    'DictList',
    'system/dict/index',
    NULL,
    NULL,
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    '字典管理菜单'
);

-- 6.2 字典管理 - 按钮
-- 创建字典
INSERT INTO ez_admin_sys_menu (
    menu_id, menu_name, menu_icon, menu_label, parent_id, menu_sort,
    menu_type, menu_perm, route_path, route_name, component_path,
    api_route, api_method, status, create_time, update_time,
    is_deleted, description
) VALUES (
    5001,
    '创建字典',
    '',
    'system:dict:create',
    500,
    1,
    3,
    'system:dict:create',
    '',
    '',
    '',
    '/api/dict/type',
    'POST',
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    '创建字典按钮'
);

-- 更新字典
INSERT INTO ez_admin_sys_menu (
    menu_id, menu_name, menu_icon, menu_label, parent_id, menu_sort,
    menu_type, menu_perm, route_path, route_name, component_path,
    api_route, api_method, status, create_time, update_time,
    is_deleted, description
) VALUES (
    5002,
    '更新字典',
    '',
    'system:dict:update',
    500,
    2,
    3,
    'system:dict:update',
    '',
    '',
    '',
    '/api/dict/type',
    'PUT',
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    '更新字典按钮'
);

-- 删除字典
INSERT INTO ez_admin_sys_menu (
    menu_id, menu_name, menu_icon, menu_label, parent_id, menu_sort,
    menu_type, menu_perm, route_path, route_name, component_path,
    api_route, api_method, status, create_time, update_time,
    is_deleted, description
) VALUES (
    5003,
    '删除字典',
    '',
    'system:dict:delete',
    500,
    3,
    3,
    'system:dict:delete',
    '',
    '',
    '',
    '/api/dict/type',
    'DELETE',
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    '删除字典按钮'
);

-- 查询字典
INSERT INTO ez_admin_sys_menu (
    menu_id, menu_name, menu_icon, menu_label, parent_id, menu_sort,
    menu_type, menu_perm, route_path, route_name, component_path,
    api_route, api_method, status, create_time, update_time,
    is_deleted, description
) VALUES (
    5004,
    '查询字典',
    '',
    'system:dict:query',
    500,
    4,
    3,
    'system:dict:query',
    '',
    '',
    '',
    '/api/dict/type',
    'GET',
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    '查询字典按钮'
);

-- ============================================================================
-- 验证数据
-- ============================================================================
-- 查询所有菜单
-- SELECT menu_id, menu_name, menu_type, parent_id, menu_perm, api_route, api_method
-- FROM ez_admin_sys_menu
-- ORDER BY parent_id, menu_sort;

-- 统计菜单数量
-- SELECT menu_type, COUNT(*) as count
-- FROM ez_admin_sys_menu
-- GROUP BY menu_type;
-- 1 目录: 1 个
-- 2 菜单: 5 个
-- 3 按钮: 24 个

-- ============================================================================
-- 说明
-- ============================================================================
-- 菜单ID规划：
-- 1: 系统管理（一级目录）
-- 100-199: 用户管理
-- 200-299: 角色管理
-- 300-399: 菜单管理
-- 400-499: 部门管理
-- 500-599: 字典管理
-- 1001-1999: 用户管理按钮
-- 2001-2999: 角色管理按钮
-- 3001-3999: 菜单管理按钮
-- 4001-4999: 部门管理按钮
-- 5001-5999: 字典管理按钮

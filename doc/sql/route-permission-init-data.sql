-- ============================================================================
-- EZ-ADMIN 路由权限映射初始数据
-- 说明：将后端 API 路由与权限码关联，用于路由拦截式鉴权
-- ============================================================================
-- 注意：本脚本基于现有菜单数据，更新菜单的 API 路由字段
-- 执行前请确保数据库中已有对应的菜单数据
-- ============================================================================

-- 更新用户管理相关菜单的 API 路由
UPDATE ez_admin_sys_menu
SET api_route = '/api/user',
    api_method = 'POST'
WHERE menu_perm = 'system:user:create';

UPDATE ez_admin_sys_menu
SET api_route = '/api/user',
    api_method = 'PUT'
WHERE menu_perm = 'system:user:update';

UPDATE ez_admin_sys_menu
SET api_route = '/api/user',
    api_method = 'DELETE'
WHERE menu_perm = 'system:user:delete';

UPDATE ez_admin_sys_menu
SET api_route = '/api/user',
    api_method = 'GET'
WHERE menu_perm = 'system:user:query';

UPDATE ez_admin_sys_menu
SET api_route = '/api/user/assign/roles',
    api_method = 'POST'
WHERE menu_perm = 'system:user:assign';

-- 更新角色管理相关菜单的 API 路由
UPDATE ez_admin_sys_menu
SET api_route = '/api/role',
    api_method = 'POST'
WHERE menu_perm = 'system:role:create';

UPDATE ez_admin_sys_menu
SET api_route = '/api/role',
    api_method = 'PUT'
WHERE menu_perm = 'system:role:update';

UPDATE ez_admin_sys_menu
SET api_route = '/api/role',
    api_method = 'DELETE'
WHERE menu_perm = 'system:role:delete';

UPDATE ez_admin_sys_menu
SET api_route = '/api/role',
    api_method = 'GET'
WHERE menu_perm = 'system:role:query';

UPDATE ez_admin_sys_menu
SET api_route = '/api/role/assign/menus',
    api_method = 'POST'
WHERE menu_perm = 'system:role:assign';

UPDATE ez_admin_sys_menu
SET api_route = '/api/role/assign/depts',
    api_method = 'POST'
WHERE menu_perm = 'system:role:assign';

-- 更新菜单管理相关菜单的 API 路由
UPDATE ez_admin_sys_menu
SET api_route = '/api/menu',
    api_method = 'POST'
WHERE menu_perm = 'system:menu:create';

UPDATE ez_admin_sys_menu
SET api_route = '/api/menu',
    api_method = 'PUT'
WHERE menu_perm = 'system:menu:update';

UPDATE ez_admin_sys_menu
SET api_route = '/api/menu',
    api_method = 'DELETE'
WHERE menu_perm = 'system:menu:delete';

UPDATE ez_admin_sys_menu
SET api_route = '/api/menu',
    api_method = 'GET'
WHERE menu_perm = 'system:menu:query';

-- 更新部门管理相关菜单的 API 路由
UPDATE ez_admin_sys_menu
SET api_route = '/api/dept',
    api_method = 'POST'
WHERE menu_perm = 'system:dept:create';

UPDATE ez_admin_sys_menu
SET api_route = '/api/dept',
    api_method = 'PUT'
WHERE menu_perm = 'system:dept:update';

UPDATE ez_admin_sys_menu
SET api_route = '/api/dept',
    api_method = 'DELETE'
WHERE menu_perm = 'system:dept:delete';

UPDATE ez_admin_sys_menu
SET api_route = '/api/dept',
    api_method = 'GET'
WHERE menu_perm = 'system:dept:query';

-- 更新字典管理相关菜单的 API 路由
UPDATE ez_admin_sys_menu
SET api_route = '/api/dict/type',
    api_method = 'POST'
WHERE menu_perm = 'system:dict:create';

UPDATE ez_admin_sys_menu
SET api_route = '/api/dict/type',
    api_method = 'PUT'
WHERE menu_perm = 'system:dict:update';

UPDATE ez_admin_sys_menu
SET api_route = '/api/dict/type',
    api_method = 'DELETE'
WHERE menu_perm = 'system:dict:delete';

UPDATE ez_admin_sys_menu
SET api_route = '/api/dict/type',
    api_method = 'GET'
WHERE menu_perm = 'system:dict:query';

-- 更新日志管理相关菜单的 API 路由
UPDATE ez_admin_sys_menu
SET api_route = '/api/log/operation/page',
    api_method = 'POST'
WHERE menu_perm = 'system:log:query';

UPDATE ez_admin_sys_menu
SET api_route = '/api/log/operation/clean',
    api_method = 'DELETE'
WHERE menu_perm = 'system:log:delete';

-- ============================================================================
-- 验证更新结果
-- ============================================================================
-- 查询所有已配置 API 路由的菜单
-- SELECT menu_id, menu_name, menu_perm, api_route, api_method
-- FROM ez_admin_sys_menu
-- WHERE api_route IS NOT NULL
-- ORDER BY menu_id;

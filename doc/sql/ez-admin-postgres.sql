-- ============================================================================
-- EZ-ADMIN PostgreSQL 数据库表结构设计
-- 基于RBAC权限管理系统
-- 数据库: PostgreSQL 15+
-- 说明: 本脚本不包含外键约束，通过应用代码层控制关联关系
-- ============================================================================

-- ============================================================================
-- 1. 部门信息表
-- ============================================================================
DROP TABLE IF EXISTS ez_admin_sys_dept CASCADE;

CREATE TABLE ez_admin_sys_dept (
    dept_id BIGINT NOT NULL,
    dept_name VARCHAR(60) NOT NULL,
    dept_sort INTEGER NOT NULL DEFAULT 999,
    ancestors VARCHAR(255) DEFAULT '',
    parent_id BIGINT NOT NULL DEFAULT 0,
    status SMALLINT NOT NULL DEFAULT 0,
    description VARCHAR(255),
    create_by VARCHAR(30) NOT NULL DEFAULT '',
    update_by VARCHAR(30) NOT NULL DEFAULT '',
    create_time TIMESTAMP NOT NULL,
    update_time TIMESTAMP NOT NULL,
    is_deleted SMALLINT NOT NULL DEFAULT 0,
    CONSTRAINT pk_ez_admin_sys_dept PRIMARY KEY (dept_id)
);

COMMENT ON TABLE ez_admin_sys_dept IS '部门信息表';
COMMENT ON COLUMN ez_admin_sys_dept.dept_id IS '部门ID';
COMMENT ON COLUMN ez_admin_sys_dept.dept_name IS '部门名称';
COMMENT ON COLUMN ez_admin_sys_dept.dept_sort IS '排序';
COMMENT ON COLUMN ez_admin_sys_dept.ancestors IS '祖级路径，格式：/1/2/';
COMMENT ON COLUMN ez_admin_sys_dept.parent_id IS '父级菜单ID';
COMMENT ON COLUMN ez_admin_sys_dept.status IS '部门状态【0 停用 1 正常】';
COMMENT ON COLUMN ez_admin_sys_dept.description IS '描述';
COMMENT ON COLUMN ez_admin_sys_dept.create_by IS '创建者';
COMMENT ON COLUMN ez_admin_sys_dept.update_by IS '更新者';
COMMENT ON COLUMN ez_admin_sys_dept.create_time IS '创建时间';
COMMENT ON COLUMN ez_admin_sys_dept.update_time IS '更新时间';
COMMENT ON COLUMN ez_admin_sys_dept.is_deleted IS '是否删除【0 正常 1 已删除】';

-- ============================================================================
-- 2. 字典类型表
-- ============================================================================
DROP TABLE IF EXISTS ez_admin_sys_dict_type CASCADE;

CREATE TABLE ez_admin_sys_dict_type (
    dict_id BIGINT NOT NULL,
    dict_name VARCHAR(100) DEFAULT '',
    dict_type VARCHAR(100) DEFAULT '',
    status SMALLINT DEFAULT 1,
    create_by VARCHAR(30) DEFAULT '',
    create_time TIMESTAMP,
    update_by VARCHAR(30) DEFAULT '',
    update_time TIMESTAMP,
    description VARCHAR(500) DEFAULT '',
    CONSTRAINT pk_ez_admin_sys_dict_type PRIMARY KEY (dict_id)
);

COMMENT ON TABLE ez_admin_sys_dict_type IS '字典类型表';
COMMENT ON COLUMN ez_admin_sys_dict_type.dict_id IS '字典主键';
COMMENT ON COLUMN ez_admin_sys_dict_type.dict_name IS '字典名称';
COMMENT ON COLUMN ez_admin_sys_dict_type.dict_type IS '字典类型';
COMMENT ON COLUMN ez_admin_sys_dict_type.status IS '状态【0 停用 1 正常】';
COMMENT ON COLUMN ez_admin_sys_dict_type.create_by IS '创建者';
COMMENT ON COLUMN ez_admin_sys_dict_type.create_time IS '创建时间';
COMMENT ON COLUMN ez_admin_sys_dict_type.update_by IS '更新者';
COMMENT ON COLUMN ez_admin_sys_dict_type.update_time IS '更新时间';
COMMENT ON COLUMN ez_admin_sys_dict_type.description IS '描述信息';

-- ============================================================================
-- 3. 字典详情表
-- ============================================================================
DROP TABLE IF EXISTS ez_admin_sys_dict_data CASCADE;

CREATE TABLE ez_admin_sys_dict_data (
    dict_data_id BIGINT NOT NULL,
    dict_id BIGINT,
    dict_label VARCHAR(100) DEFAULT '',
    dict_value VARCHAR(100) DEFAULT '',
    dict_sort INTEGER DEFAULT 0,
    list_class VARCHAR(100),
    is_default SMALLINT DEFAULT 0,
    status SMALLINT DEFAULT 1,
    create_by VARCHAR(30) DEFAULT '',
    create_time TIMESTAMP,
    update_by VARCHAR(30) DEFAULT '',
    update_time TIMESTAMP,
    description VARCHAR(500) DEFAULT '',
    CONSTRAINT pk_ez_admin_sys_dict_data PRIMARY KEY (dict_data_id)
);

COMMENT ON TABLE ez_admin_sys_dict_data IS '字典详情表';
COMMENT ON COLUMN ez_admin_sys_dict_data.dict_data_id IS '字典详情主键ID';
COMMENT ON COLUMN ez_admin_sys_dict_data.dict_id IS '字典类型ID';
COMMENT ON COLUMN ez_admin_sys_dict_data.dict_label IS '字典标签';
COMMENT ON COLUMN ez_admin_sys_dict_data.dict_value IS '字典键值';
COMMENT ON COLUMN ez_admin_sys_dict_data.dict_sort IS '字典排序';
COMMENT ON COLUMN ez_admin_sys_dict_data.list_class IS '表格回显样式';
COMMENT ON COLUMN ez_admin_sys_dict_data.is_default IS '是否默认【0 否 1 是】';
COMMENT ON COLUMN ez_admin_sys_dict_data.status IS '状态【0 停用 1 正常】';
COMMENT ON COLUMN ez_admin_sys_dict_data.create_by IS '创建者';
COMMENT ON COLUMN ez_admin_sys_dict_data.create_time IS '创建时间';
COMMENT ON COLUMN ez_admin_sys_dict_data.update_by IS '更新者';
COMMENT ON COLUMN ez_admin_sys_dict_data.update_time IS '更新时间';
COMMENT ON COLUMN ez_admin_sys_dict_data.description IS '描述信息';

-- ============================================================================
-- 4. 菜单信息表
-- ============================================================================
DROP TABLE IF EXISTS ez_admin_sys_menu CASCADE;

CREATE TABLE ez_admin_sys_menu (
    menu_id BIGINT NOT NULL,
    menu_name VARCHAR(30) NOT NULL,
    menu_icon VARCHAR(50) DEFAULT '',
    menu_label VARCHAR(30),
    parent_id BIGINT NOT NULL DEFAULT 0,
    menu_sort INTEGER NOT NULL DEFAULT 999,
    menu_type SMALLINT NOT NULL,
    menu_perm VARCHAR(255) NOT NULL DEFAULT '',
    route_path VARCHAR(255),
    route_name VARCHAR(255),
    component_path VARCHAR(255),
    api_route VARCHAR(255),
    api_method VARCHAR(20),
    status SMALLINT NOT NULL DEFAULT 0,
    create_by VARCHAR(30) DEFAULT '',
    create_time TIMESTAMP,
    update_by VARCHAR(30) DEFAULT '',
    update_time TIMESTAMP,
    description VARCHAR(500) DEFAULT '',
    is_deleted SMALLINT NOT NULL DEFAULT 0,
    CONSTRAINT pk_ez_admin_sys_menu PRIMARY KEY (menu_id)
);

COMMENT ON TABLE ez_admin_sys_menu IS '菜单信息表';
COMMENT ON COLUMN ez_admin_sys_menu.menu_id IS '菜单ID';
COMMENT ON COLUMN ez_admin_sys_menu.menu_name IS '菜单名称';
COMMENT ON COLUMN ez_admin_sys_menu.menu_icon IS '菜单图标';
COMMENT ON COLUMN ez_admin_sys_menu.menu_label IS '菜单标识';
COMMENT ON COLUMN ez_admin_sys_menu.parent_id IS '父级菜单ID';
COMMENT ON COLUMN ez_admin_sys_menu.menu_sort IS '菜单排序';
COMMENT ON COLUMN ez_admin_sys_menu.menu_type IS '菜单类型【1 目录 2 菜单 3 按钮】';
COMMENT ON COLUMN ez_admin_sys_menu.menu_perm IS '权限标识';
COMMENT ON COLUMN ez_admin_sys_menu.route_path IS '路由地址';
COMMENT ON COLUMN ez_admin_sys_menu.route_name IS '路由名称';
COMMENT ON COLUMN ez_admin_sys_menu.component_path IS '组件路径';
COMMENT ON COLUMN ez_admin_sys_menu.api_route IS '后端API路由地址';
COMMENT ON COLUMN ez_admin_sys_menu.api_method IS 'HTTP方法【GET POST PUT DELETE PATCH】';
COMMENT ON COLUMN ez_admin_sys_menu.status IS '菜单状态【0 停用 1 正常】';
COMMENT ON COLUMN ez_admin_sys_menu.create_by IS '创建者';
COMMENT ON COLUMN ez_admin_sys_menu.create_time IS '创建时间';
COMMENT ON COLUMN ez_admin_sys_menu.update_by IS '更新者';
COMMENT ON COLUMN ez_admin_sys_menu.update_time IS '更新时间';
COMMENT ON COLUMN ez_admin_sys_menu.description IS '描述信息';
COMMENT ON COLUMN ez_admin_sys_menu.is_deleted IS '是否删除【0 正常 1 已删除】';

-- ============================================================================
-- 5. 角色信息表
-- ============================================================================
DROP TABLE IF EXISTS ez_admin_sys_role CASCADE;

CREATE TABLE ez_admin_sys_role (
    role_id BIGINT NOT NULL,
    role_name VARCHAR(30) NOT NULL,
    role_label VARCHAR(100) NOT NULL,
    role_sort INTEGER NOT NULL DEFAULT 999,
    data_scope SMALLINT NOT NULL DEFAULT 1,
    status SMALLINT NOT NULL DEFAULT 0,
    create_by VARCHAR(30) DEFAULT '',
    create_time TIMESTAMP,
    update_by VARCHAR(30) DEFAULT '',
    update_time TIMESTAMP,
    description VARCHAR(500) DEFAULT '',
    is_deleted SMALLINT NOT NULL DEFAULT 0,
    CONSTRAINT pk_ez_admin_sys_role PRIMARY KEY (role_id)
);

COMMENT ON TABLE ez_admin_sys_role IS '角色信息表';
COMMENT ON COLUMN ez_admin_sys_role.role_id IS '角色ID';
COMMENT ON COLUMN ez_admin_sys_role.role_name IS '角色名称';
COMMENT ON COLUMN ez_admin_sys_role.role_label IS '角色权限字符标识';
COMMENT ON COLUMN ez_admin_sys_role.role_sort IS '排序';
COMMENT ON COLUMN ez_admin_sys_role.data_scope IS '数据范围【1 仅本人数据权限 2 本部门数据权限 3 本部门及以下数据权限 4 自定义数据权限 5 全部数据权限】';
COMMENT ON COLUMN ez_admin_sys_role.status IS '角色状态【0 停用 1 正常】';
COMMENT ON COLUMN ez_admin_sys_role.create_by IS '创建者';
COMMENT ON COLUMN ez_admin_sys_role.create_time IS '创建时间';
COMMENT ON COLUMN ez_admin_sys_role.update_by IS '更新者';
COMMENT ON COLUMN ez_admin_sys_role.update_time IS '更新时间';
COMMENT ON COLUMN ez_admin_sys_role.description IS '描述信息';
COMMENT ON COLUMN ez_admin_sys_role.is_deleted IS '是否删除【0 正常 1 已删除】';

-- ============================================================================
-- 6. 角色部门关联表
-- ============================================================================
DROP TABLE IF EXISTS ez_admin_sys_role_dept_relation CASCADE;

CREATE TABLE ez_admin_sys_role_dept_relation (
    id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    dept_id BIGINT NOT NULL,
    CONSTRAINT pk_ez_admin_sys_role_dept_relation PRIMARY KEY (id)
);

COMMENT ON TABLE ez_admin_sys_role_dept_relation IS '角色部门关联表';
COMMENT ON COLUMN ez_admin_sys_role_dept_relation.id IS '主键ID';
COMMENT ON COLUMN ez_admin_sys_role_dept_relation.role_id IS '角色ID';
COMMENT ON COLUMN ez_admin_sys_role_dept_relation.dept_id IS '部门ID';

-- 为关联表创建索引以提升查询性能
CREATE INDEX idx_role_dept_role_id ON ez_admin_sys_role_dept_relation(role_id);
CREATE INDEX idx_role_dept_dept_id ON ez_admin_sys_role_dept_relation(dept_id);

-- ============================================================================
-- 7. 角色菜单关联表
-- ============================================================================
DROP TABLE IF EXISTS ez_admin_sys_role_menu_relation CASCADE;

CREATE TABLE ez_admin_sys_role_menu_relation (
    id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    CONSTRAINT pk_ez_admin_sys_role_menu_relation PRIMARY KEY (id)
);

COMMENT ON TABLE ez_admin_sys_role_menu_relation IS '角色菜单关联表';
COMMENT ON COLUMN ez_admin_sys_role_menu_relation.id IS '主键ID';
COMMENT ON COLUMN ez_admin_sys_role_menu_relation.role_id IS '角色ID';
COMMENT ON COLUMN ez_admin_sys_role_menu_relation.menu_id IS '菜单ID';

-- 为关联表创建索引以提升查询性能
CREATE INDEX idx_role_menu_role_id ON ez_admin_sys_role_menu_relation(role_id);
CREATE INDEX idx_role_menu_menu_id ON ez_admin_sys_role_menu_relation(menu_id);

-- ============================================================================
-- 8. 用户信息表
-- ============================================================================
DROP TABLE IF EXISTS ez_admin_sys_user CASCADE;

CREATE TABLE ez_admin_sys_user (
    user_id BIGINT NOT NULL,
    dept_id BIGINT,
    username VARCHAR(30) NOT NULL,
    password VARCHAR(100) NOT NULL,
    nickname VARCHAR(30) NOT NULL,
    email VARCHAR(50),
    phone_number VARCHAR(20),
    gender SMALLINT NOT NULL DEFAULT 0,
    avatar VARCHAR(100),
    status SMALLINT NOT NULL DEFAULT 1,
    login_ip VARCHAR(128),
    login_date TIMESTAMP,
    create_by VARCHAR(30) DEFAULT '',
    create_time TIMESTAMP,
    update_by VARCHAR(30) DEFAULT '',
    update_time TIMESTAMP,
    description VARCHAR(500) DEFAULT '',
    is_deleted SMALLINT NOT NULL DEFAULT 0,
    CONSTRAINT pk_ez_admin_sys_user PRIMARY KEY (user_id)
);

COMMENT ON TABLE ez_admin_sys_user IS '用户信息表';
COMMENT ON COLUMN ez_admin_sys_user.user_id IS '用户ID';
COMMENT ON COLUMN ez_admin_sys_user.dept_id IS '部门ID';
COMMENT ON COLUMN ez_admin_sys_user.username IS '用户账号';
COMMENT ON COLUMN ez_admin_sys_user.password IS '密码';
COMMENT ON COLUMN ez_admin_sys_user.nickname IS '用户昵称';
COMMENT ON COLUMN ez_admin_sys_user.email IS '用户邮箱';
COMMENT ON COLUMN ez_admin_sys_user.phone_number IS '用户手机号码';
COMMENT ON COLUMN ez_admin_sys_user.gender IS '性别【0 保密 1 男 2 女】';
COMMENT ON COLUMN ez_admin_sys_user.avatar IS '用户头像';
COMMENT ON COLUMN ez_admin_sys_user.status IS '用户状态【0 禁用 1 正常】';
COMMENT ON COLUMN ez_admin_sys_user.login_ip IS '最后登录IP';
COMMENT ON COLUMN ez_admin_sys_user.login_date IS '最后登录时间';
COMMENT ON COLUMN ez_admin_sys_user.create_by IS '创建者';
COMMENT ON COLUMN ez_admin_sys_user.create_time IS '创建时间';
COMMENT ON COLUMN ez_admin_sys_user.update_by IS '更新者';
COMMENT ON COLUMN ez_admin_sys_user.update_time IS '更新时间';
COMMENT ON COLUMN ez_admin_sys_user.description IS '描述信息';
COMMENT ON COLUMN ez_admin_sys_user.is_deleted IS '是否删除【0 正常 1 已删除】';

-- 为用户表创建唯一索引
CREATE UNIQUE INDEX idx_username ON ez_admin_sys_user(username);
CREATE INDEX idx_dept_id ON ez_admin_sys_user(dept_id);

-- ============================================================================
-- 9. 用户角色关联表
-- ============================================================================
DROP TABLE IF EXISTS ez_admin_sys_user_role_relation CASCADE;

CREATE TABLE ez_admin_sys_user_role_relation (
    id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    CONSTRAINT pk_ez_admin_sys_user_role_relation PRIMARY KEY (id)
);

COMMENT ON TABLE ez_admin_sys_user_role_relation IS '用户角色关联表';
COMMENT ON COLUMN ez_admin_sys_user_role_relation.id IS '主键ID';
COMMENT ON COLUMN ez_admin_sys_user_role_relation.user_id IS '用户ID';
COMMENT ON COLUMN ez_admin_sys_user_role_relation.role_id IS '角色ID';

-- 为关联表创建索引以提升查询性能
CREATE INDEX idx_user_role_user_id ON ez_admin_sys_user_role_relation(user_id);
CREATE INDEX idx_user_role_role_id ON ez_admin_sys_user_role_relation(role_id);

-- ============================================================================
-- 10. 操作日志表
-- ============================================================================
DROP TABLE IF EXISTS ez_admin_sys_operation_log CASCADE;

CREATE TABLE ez_admin_sys_operation_log (
    log_id BIGINT NOT NULL,
    module VARCHAR(50),
    operation VARCHAR(50),
    description VARCHAR(255),
    user_id BIGINT,
    username VARCHAR(30),
    request_method VARCHAR(10),
    request_url VARCHAR(500),
    request_ip VARCHAR(128),
    request_params TEXT,
    execute_time BIGINT,
    status SMALLINT DEFAULT 1,
    error_msg TEXT,
    create_time TIMESTAMP,
    CONSTRAINT pk_ez_admin_sys_operation_log PRIMARY KEY (log_id)
);

COMMENT ON TABLE ez_admin_sys_operation_log IS '操作日志表';
COMMENT ON COLUMN ez_admin_sys_operation_log.log_id IS '日志ID';
COMMENT ON COLUMN ez_admin_sys_operation_log.module IS '模块名称';
COMMENT ON COLUMN ez_admin_sys_operation_log.operation IS '操作类型';
COMMENT ON COLUMN ez_admin_sys_operation_log.description IS '操作描述';
COMMENT ON COLUMN ez_admin_sys_operation_log.user_id IS '操作用户ID';
COMMENT ON COLUMN ez_admin_sys_operation_log.username IS '操作用户名';
COMMENT ON COLUMN ez_admin_sys_operation_log.request_method IS '请求方法';
COMMENT ON COLUMN ez_admin_sys_operation_log.request_url IS '请求URL';
COMMENT ON COLUMN ez_admin_sys_operation_log.request_ip IS '请求IP';
COMMENT ON COLUMN ez_admin_sys_operation_log.request_params IS '请求参数';
COMMENT ON COLUMN ez_admin_sys_operation_log.execute_time IS '执行时长（毫秒）';
COMMENT ON COLUMN ez_admin_sys_operation_log.status IS '操作状态【0 失败 1 成功】';
COMMENT ON COLUMN ez_admin_sys_operation_log.error_msg IS '错误信息';
COMMENT ON COLUMN ez_admin_sys_operation_log.create_time IS '创建时间';

-- 为日志表创建索引以提升查询性能
CREATE INDEX idx_operation_log_user_id ON ez_admin_sys_operation_log(user_id);
CREATE INDEX idx_operation_log_create_time ON ez_admin_sys_operation_log(create_time);
CREATE INDEX idx_operation_log_module ON ez_admin_sys_operation_log(module);

-- ============================================================================
-- 11. 系统配置表
-- ============================================================================
DROP TABLE IF EXISTS ez_admin_sys_config CASCADE;

CREATE TABLE ez_admin_sys_config (
    config_id BIGINT NOT NULL,
    config_name VARCHAR(100) NOT NULL,
    config_key VARCHAR(100) NOT NULL,
    config_value VARCHAR(500) NOT NULL,
    config_type VARCHAR(50) NOT NULL DEFAULT 'system',
    is_system SMALLINT NOT NULL DEFAULT 0,
    remark VARCHAR(500),
    create_by VARCHAR(30) DEFAULT '',
    create_time TIMESTAMP,
    update_by VARCHAR(30) DEFAULT '',
    update_time TIMESTAMP,
    is_deleted SMALLINT NOT NULL DEFAULT 0,
    CONSTRAINT pk_ez_admin_sys_config PRIMARY KEY (config_id)
);

COMMENT ON TABLE ez_admin_sys_config IS '系统配置表';
COMMENT ON COLUMN ez_admin_sys_config.config_id IS '配置ID';
COMMENT ON COLUMN ez_admin_sys_config.config_name IS '配置名称';
COMMENT ON COLUMN ez_admin_sys_config.config_key IS '配置键值';
COMMENT ON COLUMN ez_admin_sys_config.config_value IS '配置内容';
COMMENT ON COLUMN ez_admin_sys_config.config_type IS '配置类型（system=系统配置，user=用户配置）';
COMMENT ON COLUMN ez_admin_sys_config.is_system IS '是否系统内置（0=否，1=是）';
COMMENT ON COLUMN ez_admin_sys_config.remark IS '备注';
COMMENT ON COLUMN ez_admin_sys_config.create_by IS '创建者';
COMMENT ON COLUMN ez_admin_sys_config.create_time IS '创建时间';
COMMENT ON COLUMN ez_admin_sys_config.update_by IS '更新者';
COMMENT ON COLUMN ez_admin_sys_config.update_time IS '更新时间';
COMMENT ON COLUMN ez_admin_sys_config.is_deleted IS '是否删除【0 正常 1 已删除】';

-- 为配置表创建索引以提升查询性能
CREATE UNIQUE INDEX idx_config_key ON ez_admin_sys_config(config_key) WHERE is_deleted = 0;

-- ============================================================================
-- 初始化序列（可选，用于主键自增）
-- ============================================================================
-- 如果需要使用自增主键，可以创建序列
-- CREATE SEQUENCE IF NOT EXISTS seq_dept_id START 1;
-- CREATE SEQUENCE IF NOT EXISTS seq_dict_type_id START 1;
-- CREATE SEQUENCE IF NOT EXISTS seq_dict_data_id START 1;
-- CREATE SEQUENCE IF NOT EXISTS seq_menu_id START 1;
-- CREATE SEQUENCE IF NOT EXISTS seq_role_id START 1;
-- CREATE SEQUENCE IF NOT EXISTS seq_user_id START 1;
-- CREATE SEQUENCE IF NOT EXISTS seq_relation_id START 1;
-- CREATE SEQUENCE IF NOT EXISTS seq_operation_log_id START 1;
-- CREATE SEQUENCE IF NOT EXISTS seq_config_id START 1;

-- ============================================================================
-- 数据库升级脚本（针对已有数据库）
-- ============================================================================
-- 为已有的 ez_admin_sys_menu 表添加 API 路由字段
-- ALTER TABLE ez_admin_sys_menu ADD COLUMN IF NOT EXISTS api_route VARCHAR(255);
-- COMMENT ON COLUMN ez_admin_sys_menu.api_route IS '后端API路由地址';
--
-- ALTER TABLE ez_admin_sys_menu ADD COLUMN IF NOT EXISTS api_method VARCHAR(20);
-- COMMENT ON COLUMN ez_admin_sys_menu.api_method IS 'HTTP方法【GET POST PUT DELETE PATCH】';

-- 为新增字段创建索引以提升查询性能
-- CREATE INDEX IF NOT EXISTS idx_menu_api_route ON ez_admin_sys_menu(api_route, api_method);

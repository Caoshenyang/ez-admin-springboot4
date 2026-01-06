/*
 Navicat Premium Dump SQL

 Source Server         : iwan
 Source Server Type    : MySQL
 Source Server Version : 80402 (8.4.2)
 Source Host           : 121.5.162.159:3666
 Source Schema         : ez-admin

 Target Server Type    : MySQL
 Target Server Version : 80402 (8.4.2)
 File Encoding         : 65001

 Date: 19/12/2025 17:43:28
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ez_admin_dept
-- ----------------------------
DROP TABLE IF EXISTS `ez_admin_dept`;
CREATE TABLE `ez_admin_dept`  (
  `dept_id` bigint UNSIGNED NOT NULL COMMENT '部门ID',
  `dept_name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '部门名称',
  `dept_sort` int NOT NULL DEFAULT 999 COMMENT '排序',
  `ancestors` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '祖级列表',
  `parent_id` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '父级菜单ID',
  `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '部门状态【0 停用 1 正常】',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `create_by` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '创建者',
  `update_by` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '更新者',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `is_deleted` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除【0 正常 1 已删除】',
  PRIMARY KEY (`dept_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '部门信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ez_admin_dept
-- ----------------------------

-- ----------------------------
-- Table structure for ez_admin_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `ez_admin_dict_data`;
CREATE TABLE `ez_admin_dict_data`  (
  `dict_data_id` bigint UNSIGNED NOT NULL COMMENT '字典详情主键ID',
  `dict_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '字典类型ID',
  `dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典键值',
  `dict_sort` int NULL DEFAULT 0 COMMENT '字典排序',
  `list_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '表格回显样式',
  `is_default` tinyint UNSIGNED NULL DEFAULT 0 COMMENT '是否默认【0 否 1 是】',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态【0 停用 1 正常】',
  `create_by` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '描述信息',
  PRIMARY KEY (`dict_data_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '字典详情表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ez_admin_dict_data
-- ----------------------------

-- ----------------------------
-- Table structure for ez_admin_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `ez_admin_dict_type`;
CREATE TABLE `ez_admin_dict_type`  (
  `dict_id` bigint NOT NULL COMMENT '字典主键',
  `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典类型',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态【0 停用 1 正常】',
  `create_by` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '描述信息',
  PRIMARY KEY (`dict_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '字典类型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ez_admin_dict_type
-- ----------------------------

-- ----------------------------
-- Table structure for ez_admin_menu
-- ----------------------------
DROP TABLE IF EXISTS `ez_admin_menu`;
CREATE TABLE `ez_admin_menu`  (
  `menu_id` bigint UNSIGNED NOT NULL COMMENT '菜单ID',
  `menu_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单名称',
  `menu_icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '菜单图标',
  `menu_label` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜单标识',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父级菜单ID',
  `menu_sort` int UNSIGNED NOT NULL DEFAULT 999 COMMENT '菜单排序',
  `menu_type` tinyint UNSIGNED NOT NULL COMMENT '菜单类型【1 目录 2 菜单 3 按钮】',
  `menu_perm` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '权限标识',
  `route_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由地址',
  `route_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由名称',
  `component_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组件路径',
  `status` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '菜单状态【0 停用 1 正常】',
  `create_by` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '描述信息',
  `is_deleted` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除【0 正常 1 已删除】',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜单信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ez_admin_menu
-- ----------------------------

-- ----------------------------
-- Table structure for ez_admin_role
-- ----------------------------
DROP TABLE IF EXISTS `ez_admin_role`;
CREATE TABLE `ez_admin_role`  (
  `role_id` bigint UNSIGNED NOT NULL COMMENT '角色ID',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `role_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色权限字符标识',
  `role_sort` int UNSIGNED NOT NULL DEFAULT 999 COMMENT '排序',
  `data_scope` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '数据范围【1 仅本人数据权限 2 本部门数据权限 3 本部门及以下数据权限 4 自定义数据权限 5 全部数据权限】',
  `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '角色状态【0 停用 1 正常】',
  `create_by` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '描述信息',
  `is_deleted` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除【0 正常 1 已删除】',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ez_admin_role
-- ----------------------------
INSERT INTO `ez_admin_role` VALUES (2001550107779235842, '超级管理员', 'admin', 999, 1, 0, 'init', NULL, 'init', NULL, '', 0);

-- ----------------------------
-- Table structure for ez_admin_role_dept_relation
-- ----------------------------
DROP TABLE IF EXISTS `ez_admin_role_dept_relation`;
CREATE TABLE `ez_admin_role_dept_relation`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '主键ID',
  `role_id` bigint UNSIGNED NOT NULL COMMENT '角色ID',
  `dept_id` bigint UNSIGNED NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ez_admin_role_dept_relation
-- ----------------------------

-- ----------------------------
-- Table structure for ez_admin_role_menu_relation
-- ----------------------------
DROP TABLE IF EXISTS `ez_admin_role_menu_relation`;
CREATE TABLE `ez_admin_role_menu_relation`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '主键ID',
  `role_id` bigint UNSIGNED NOT NULL COMMENT '角色ID',
  `menu_id` bigint UNSIGNED NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色菜单关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ez_admin_role_menu_relation
-- ----------------------------

-- ----------------------------
-- Table structure for ez_admin_user
-- ----------------------------
DROP TABLE IF EXISTS `ez_admin_user`;
CREATE TABLE `ez_admin_user`  (
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `dept_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '部门ID',
  `username` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `nickname` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户邮箱',
  `phone_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户手机号码',
  `gender` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '性别【0 保密 1 男 2 女】',
  `avatar` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户头像',
  `status` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '用户状态【0 禁用 1 正常】',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最后登录IP',
  `login_date` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `create_by` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '描述信息',
  `is_deleted` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除【0 正常 1 已删除】',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ez_admin_user
-- ----------------------------
INSERT INTO `ez_admin_user` VALUES (2001550107699544065, NULL, 'admin', '$2a$10$y3Nd4ktFv97EjXw4Ixdc0e6YM8rM/hbcxl9eaYDaoAzZeByde.Un.', 'admin', NULL, NULL, 0, NULL, 1, NULL, NULL, 'init', NULL, 'init', NULL, '', 0);
INSERT INTO `ez_admin_user` VALUES (2001580540411711489, NULL, '潘玉珍', '$2a$10$PpYvf2GNz6P8zKOzxIkeoeQPD.uEAOU0LZ9BG.Eni2TdNHDy/OUYC', '革浩然', 'k7fv9r_q3p@gmail.com', '65907130513', 1, 'https://avatars.githubusercontent.com/u/55384180', 1, NULL, NULL, 'admin', '2025-12-18 17:09:23', 'admin', '2025-12-18 17:09:23', '', 0);

-- ----------------------------
-- Table structure for ez_admin_user_role_relation
-- ----------------------------
DROP TABLE IF EXISTS `ez_admin_user_role_relation`;
CREATE TABLE `ez_admin_user_role_relation`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '主键ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `role_id` bigint UNSIGNED NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ez_admin_user_role_relation
-- ----------------------------
INSERT INTO `ez_admin_user_role_relation` VALUES (2001550107846344706, 2001550107699544065, 2001550107779235842);

SET FOREIGN_KEY_CHECKS = 1;

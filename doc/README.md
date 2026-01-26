# EZ-ADMIN 开发文档索引

## 核心规范文档

| 文档 | 说明 |
|------|------|
| [VO 命名规范](./vo-naming-spec.md) | DetailVO、ListVO、TreeVO 的命名规范和使用场景 |
| [@Schema 注解规范](./annotation-spec.md) | Swagger 注解的 description 和 example 填写规范 |
| [对象构造规范](./object-construction-spec.md) | DTO、VO、Entity 的注解组合和构造方式 |
| [MapStruct 使用指南](./mapstruct-guide.md) | MapStruct 对象映射的最佳实践 |
| [MyBatis-Plus 使用规范](./mybatis-plus-guide.md) | Wrapper vs XML 的技术决策准则 |
| [错误码设计规范](./error-code-design.md) | 错误码结构、分类和速查表 |

## 设计文档

| 文档 | 说明 |
|------|------|
| [树形结构处理指南](./tree-structure-guide.md) | TreeNode 和 TreeBuilder 的使用指南 |
| [动态查询系统设计文档](./动态查询系统设计文档.md) | 元数据驱动的动态查询系统设计 |
| [依赖关系说明](./依赖关系说明.md) | 项目依赖和模块关系说明 |

## 数据库文档

| 文档 | 说明 |
|------|------|
| [PostgreSQL 建表脚本](./ez-admin-postgres.sql) | PostgreSQL 数据库初始化脚本 |

---

## 文档使用说明

1. **核心规范文档**：开发时经常查阅，掌握后可快速开发
2. **设计文档**：理解系统架构和设计思路
3. **数据库文档**：本地开发环境搭建时使用

## 更新日志

- 2026-01-26：新增 VO 命名规范、注解规范、对象构造规范等核心文档

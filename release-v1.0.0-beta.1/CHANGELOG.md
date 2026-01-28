# 更新日志

## v1.0.0-beta.1 (2026-01-28)

### 测试版首次发布

#### 功能特性

**核心模块**
- ✅ 用户管理（CRUD、角色分配）
- ✅ 角色管理（CRUD、菜单/部门分配、数据权限）
- ✅ 菜单管理（树形结构、路由权限）
- ✅ 部门管理（树形结构）
- ✅ 字典管理（字典类型、字典数据）
- ✅ 系统配置
- ✅ 操作日志（自动记录）

**认证授权**
- ✅ Sa-Token JWT 认证
- ✅ 登录/登出
- ✅ Token 刷新
- ✅ 数据权限（全部、自定义、本部门、本部门及子部门、仅本人）

**技术特性**
- ✅ MyBatis-Plus 拦截器实现数据权限
- ✅ MapStruct 对象映射
- ✅ 统一异常处理
- ✅ 统一响应格式
- ✅ 操作日志注解
- ✅ 参数校验（Jakarta Validation）
- ✅ Swagger API 文档

#### 技术栈

- Spring Boot 4.0.2
- JDK 21
- MyBatis-Plus 3.5.15
- Sa-Token 1.44.0
- PostgreSQL Driver 42.7.9
- MapStruct 1.6.3
- Lombok 1.18.34

#### 数据库支持

- PostgreSQL 12+

#### 已知问题

- 前端尚未完成，当前仅提供后端 API
- 部分高级功能可能需要进一步测试
- 文档尚在完善中

#### 测试状态

已测试功能：
- ✅ 系统初始化
- ✅ 用户登录/登出
- ✅ 用户 CRUD
- ✅ 角色 CRUD
- ✅ 菜单树查询
- ✅ 操作日志查询

待测试功能：
- ⏳ 数据权限完整测试
- ⏳ 前端联调
- ⏳ 性能测试
- ⏳ 并发测试

#### 安装说明

详见 [INSTALL.md](docs/INSTALL.md)

#### 反馈渠道

- GitHub Issues: [提交问题](https://github.com/your-repo/ez-admin-springboot4/issues)
- GitHub Discussions: [功能建议](https://github.com/your-repo/ez-admin-springboot4/discussions)

---

## 版本说明

### 版本命名规则

- `v1.0.0-beta.1`：测试版
- `v1.0.0-rc.1`：发布候选版
- `v1.0.0`：正式版

### 后续计划

**v1.0.0-beta.2**
- 前端联调
- Bug 修复
- 功能完善

**v1.0.0-rc.1**
- 性能优化
- 安全加固
- 完整测试

**v1.0.0**
- 正式发布
- 完整文档
- Docker 镜像

---

## 贡献指南

欢迎提交 Pull Request 或 Issue！

---

## 许可证

MIT License

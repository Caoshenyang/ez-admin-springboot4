# EZ-ADMIN SpringBoot 4.x

> 基于 Spring Boot 4.0 + JDK 21 的轻量级 RBAC 后台管理系统

## 版本信息

**v1.0.0-beta.1** (测试版)

发布日期：2026-01-28

---

## 特性

- ✅ **最新技术栈**：Spring Boot 4.0 + JDK 21
- ✅ **RBAC 权限模型**：用户-角色-菜单-部门权限体系
- ✅ **数据权限**：基于 MyBatis-Plus 拦截器的数据权限控制
- ✅ **操作日志**：自动记录用户操作行为
- ✅ **Sa-Token 认证**：轻量级权限认证框架
- ✅ **PostgreSQL 数据库**：业界领先的开源数据库
- ✅ **Swagger 文档**：自动生成 API 文档

---

## 快速开始

### 环境要求

- JDK 21+
- PostgreSQL 12+
- Redis 5.0+

### 三步启动

#### 1️⃣ 初始化数据库

```bash
# 创建数据库
createdb ez-admin

# 执行 SQL 脚本
psql -d ez-admin -f sql/ez-admin-schema-v2-postgres.sql
psql -d ez-admin -f sql/menu-init-data-v2-postgres.sql
```

#### 2️⃣ 配置文件

修改 `config/application-dev.yml` 中的数据库和 Redis 连接信息。

#### 3️⃣ 启动应用

```bash
java -jar ez-admin-springboot4-1.0.0-beta.1.jar
```

#### 4️⃣ 初始化系统

```bash
curl -X POST http://localhost:8080/install \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123","nickname":"Admin"}'
```

---

## 访问地址

- **应用地址**：http://localhost:8080
- **API 文档**：http://localhost:8080/swagger-ui/index.html
- **健康检查**：http://localhost:8080/actuator/health

---

## 目录结构

```
ez-admin-springboot4-1.0.0-beta.1/
├── ez-admin-springboot4-1.0.0-beta.1.jar    # 可执行 JAR
├── config/                                    # 配置文件示例
│   ├── application-example.yml
│   └── application-dev-example.yml
├── sql/                                       # 数据库脚本
│   ├── ez-admin-schema-v2-postgres.sql
│   └── menu-init-data-v2-postgres.sql
└── docs/                                      # 文档
    ├── INSTALL.md                             # 安装指南
    └── README.md
```

---

## 核心功能

### 用户管理
- 用户增删改查
- 用户角色分配
- 用户状态管理

### 角色管理
- 角色增删改查
- 角色菜单分配
- 角色部门分配
- 数据权限配置

### 菜单管理
- 菜单树形结构
- 路由权限配置
- 动态菜单加载

### 部门管理
- 部门树形结构
- 部门层级管理

### 系统管理
- 字典管理
- 配置管理
- 操作日志

---

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 4.0.2 | 基础框架 |
| JDK | 21 | 运行环境 |
| MyBatis-Plus | 3.5.15 | ORM 框架 |
| Sa-Token | 1.44.0 | 权限认证 |
| PostgreSQL Driver | 42.7.9 | 数据库驱动 |
| Redis | - | 缓存/会话 |
| MapStruct | 1.6.3 | 对象映射 |
| Lombok | 1.18.34 | 代码简化 |

---

## 配置说明

### 数据库配置

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ez-admin
    username: postgres
    password: postgres
```

### Redis 配置

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password:
      database: 0
```

### Sa-Token 配置

```yaml
sa-token:
  token-name: Authorization
  timeout: 2592000          # 30天
  active-timeout: 1800      # 30分钟
  jwt-secret-key: your-secret-key
```

---

## 常见问题

### 1. 端口冲突

修改 `server.port` 配置：

```yaml
server:
  port: 8088
```

### 2. 数据库连接失败

检查 PostgreSQL 服务是否启动，防火墙是否开放 5432 端口。

### 3. Redis 连接失败

检查 Redis 服务是否启动，确保 6379 端口可访问。

---

## 详细文档

- [安装指南](docs/INSTALL.md) - 详细的安装和配置说明
- [API 文档](http://localhost:8080/swagger-ui/index.html) - 启动后访问

---

## 开源协议

[MIT License](LICENSE)

---

## 反馈与支持

- **问题反馈**：[GitHub Issues](https://github.com/your-repo/ez-admin-springboot4/issues)
- **功能建议**：[GitHub Discussions](https://github.com/your-repo/ez-admin-springboot4/discussions)

---

**注意**：这是测试版本，不建议直接用于生产环境。

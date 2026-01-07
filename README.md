# EZ-ADMIN-SPRINGBOOT4
![Version](https://img.shields.io/badge/version-1.0.0-brightgreen)
![License](https://img.shields.io/badge/license-MIT-blue)
![GitHub Stars](https://img.shields.io/github/stars/Caoshenyang/ez-admin-springboot4?style=social)

**一款简单易用的后台管理系统**

如果你不知道接下来该干什么，烦于学习更加高深的技术栈，或不想陷入源码的深渊，不妨做点自己擅长的事，加强自己的自信心，相信后台管理系统大家都不陌生，
市面上优秀的产品也很多，有很多优点但也不乏一些弊端，那么我们为什么不取其精华，打造一款自己的产品，无论是学习，还是便于后面工作中提高自己的工作效率，
都是一个不错的选择，而且我一直认为，"造轮子"是对于自己掌握知识的一个全面汇总，他可以很好的体现我们的项目落地能力。而 EZ-ADMIN 就是这样诞生的一个项目。

EZ 取自 "easy" 谐音，体现系统简单易用的特点，这也是开发这个项目的目标之一。
没有复杂的过度封装，代码整洁，逻辑清晰，可轻松上手，易于二次开发，没有任何技术负担。
配备详细的使用文档，希望能帮助到需要的朋友。

**❗️注意：本仓库不接受 Pull Request**

请改为提交 `Issue` 来描述你的建议或问题。

## 🎯 项目特色

- **现代化技术栈**：基于 Spring Boot 4.x + Java 21 的最新技术栈
- **完善权限体系**：集成 Sa-Token 权限认证框架，支持 JWT
- **高效数据访问**：采用 MyBatis-Plus 增强 ORM 框架
- **缓存支持**：集成 Redis 缓存，提升系统性能
- **自动文档生成**：集成 SpringDoc OpenAPI，自动生成 API 文档
- **生产就绪**：包含完整的异常处理、数据校验、日志记录等生产级特性

## 📋 系统功能

- **用户管理**：用户信息的增删改查、状态管理
- **角色管理**：角色权限分配、数据范围控制
- **部门管理**：组织架构管理、树形结构展示
- **菜单管理**：动态菜单配置、权限控制
- **字典管理**：系统字典配置、数据标准化
- **权限控制**：基于角色的访问控制(RBAC)、细粒度权限管理

## 🛠 技术选型

| 技术领域       | 技术选型              | 版本       | 说明                                                                 |
|----------------|-----------------------|------------|----------------------------------------------------------------------|
| **核心框架**   | Spring Boot           | 4.0.0      | 最新版 Spring Boot 框架，提供开箱即用的特性                         |
| **ORM 框架**   | MyBatis-Plus          | 3.5.15     | 强大的 MyBatis 增强工具，简化 CRUD 操作                             |
| **数据库**     | MySQL                 | 8.x        | 主流关系型数据库，支持高并发和高可用                                 |
| **缓存**       | Redis                 | 最新版     | 高性能键值存储数据库，支持数据缓存                                   |
| **权限认证**   | Sa-Token              | 1.44.0     | 轻量级 Java 权限认证框架，支持 JWT                                   |
| **对象映射**   | MapStruct             | 1.6.3      | 编译时对象映射工具，提升转换性能                                     |
| **工具库**     | Lombok                | 1.18.30    | 简化 Java 代码的注解库，减少样板代码                                 |
| **JSON 处理**  | Jackson               | 2.20.1     | 高性能 JSON 序列化库                                                 |
| **API 文档**   | SpringDoc OpenAPI     | 3.0.0      | 自动生成 RESTful API 文档                                            |
| **密码加密**   | Spring Security Crypto| 最新版     | Spring 官方密码加密工具                                              |
| **构建工具**   | Maven                 | 3.9+       | 项目构建和依赖管理工具                                               |
| **JDK版本**    | Java                  | 21         | 最新 LTS 版本，提供现代化的语言特性                                 |

## 🏗 技术架构特点

### ✅ 后端架构
- **分层设计**：Controller → Service → Mapper → Entity
- **RESTful API**：标准的 REST 接口设计
- **数据校验**：完善的参数校验和业务校验
- **异常处理**：全局统一异常处理机制
- **日志记录**：结构化日志记录，支持多环境配置

### ✅ 数据访问层
- **MyBatis-Plus**：内置 CRUD 方法、分页插件、逻辑删除
- **XML 映射**：灵活的 SQL 映射配置
- **事务管理**：声明式事务控制，保证数据一致性
- **连接池优化**：高效的数据库连接管理

### ✅ 权限体系
- **Sa-Token 集成**：完整的权限认证解决方案
- **JWT 支持**：无状态 token 认证
- **多端登录**：支持同一账号多地同时登录
- **细粒度控制**：支持菜单权限、按钮权限、数据权限

### ✅ 缓存架构
- **Redis 集成**：Lettuce 连接池配置
- **缓存策略**：灵活的缓存配置和过期策略
- **性能优化**：减少数据库访问，提升响应速度

### ✅ API设计规范
- **统一响应格式**：标准化的 API 响应结构
- **HTTP状态码**：正确使用 HTTP 状态码
- **错误处理**：详细的错误信息和异常分类
- **接口文档**：自动生成的 Swagger UI 文档

## 🚀 快速开始

### 环境要求

| 工具/环境      | 版本要求      | 说明                          |
|---------------|--------------|-----------------------------|
| JDK            | 21+          | Java 开发工具包              |
| Maven          | 3.9+         | 项目构建工具                 |
| MySQL          | 8.0+         | 数据库服务器                 |
| Redis          | 6.0+         | 缓存服务器                   |
| IDE            | IntelliJ IDEA| 推荐开发工具                 |

### 数据库初始化

1. 创建 MySQL 数据库：
```sql
CREATE DATABASE ez_admin DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

2. 执行数据库初始化脚本：
```bash
# 在项目根目录执行
mysql -u root -p ez_admin < doc/ez-admin.sql
```

### 配置环境变量

创建 `.env` 文件或设置环境变量：

```properties
# 数据库配置
DB_HOST=localhost
DB_PORT=3306
DB_NAME=ez_admin
DB_USERNAME=root
DB_PASSWORD=your_password

# Redis 配置
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=your_password

# Sa-Token 配置
SA_TOKEN_TOKEN_NAME=ezadmin
SA_TOKEN_JWT_SECRET_KEY=your_jwt_secret_key
SA_TOKEN_TIMEOUT=2592000
```

### 启动项目

```bash
# 克隆项目
git clone https://github.com/Caoshenyang/ez-admin-springboot4.git
cd ez-admin-springboot4

# 编译项目
mvn clean compile

# 启动项目
mvn spring-boot:run
```

项目启动后访问：
- 应用地址：http://localhost:8080
- API 文档：http://localhost:8080/swagger-ui.html
- 默认账号：admin / admin

## 📁 项目结构

```
ez-admin-springboot4/
├── doc/                          # 文档目录
│   ├── ez-admin.sql             # 数据库初始化脚本
│   └── init_menu_data.sql       # 菜单初始化数据
├── src/main/java/com/ezadmin/
│   ├── api/                     # REST API 控制器
│   │   ├── AuthController.java      # 认证接口
│   │   ├── UserController.java      # 用户管理
│   │   ├── RoleController.java      # 角色管理
│   │   ├── DeptController.java      # 部门管理
│   │   ├── MenuController.java      # 菜单管理
│   │   └── DictController.java      # 字典管理
│   ├── common/                  # 公共模块
│   │   ├── auth/                # 权限相关
│   │   ├── cache/               # 缓存配置
│   │   ├── component/           # 组件配置
│   │   ├── constants/           # 常量定义
│   │   ├── exception/           # 异常处理
│   │   ├── handler/             # 处理类
│   │   ├── response/            # 响应封装
│   │   └── utils/               # 工具类
│   ├── config/                  # 配置类
│   │   ├── SaTokenConfigure.java   # Sa-Token 配置
│   │   ├── MybatisPlusConfig.java  # MyBatis-Plus 配置
│   │   ├── RedisTemplateConfig.java # Redis 配置
│   │   └── OpenApiConfig.java      # API 文档配置
│   ├── model/                   # 数据模型
│   │   ├── dto/                 # 数据传输对象
│   │   ├── vo/                  # 视图对象
│   │   ├── query/               # 查询对象
│   │   └── mapstruct/           # 对象映射配置
│   ├── modules/                 # 业务模块
│   │   └── system/              # 系统管理模块
│   └── service/                 # 业务服务层
└── src/main/resources/
    ├── mapper/                  # MyBatis XML 映射
    ├── application.yaml         # 主配置文件
    ├── application-dev.yaml     # 开发环境配置
    └── application-prod.yaml    # 生产环境配置
```

## 🔧 配置说明

### 应用配置

主要配置文件位于 `src/main/resources/application.yaml`：

- **数据库配置**：MySQL 连接参数和连接池设置
- **Redis 配置**：缓存服务器连接和连接池配置
- **Sa-Token 配置**：权限认证相关配置
- **SpringDoc 配置**：API 文档生成配置

### 环境配置

支持多环境配置：
- `dev`：开发环境（默认）
- `prod`：生产环境

通过 `spring.profiles.active` 指定激活的环境。

## 📚 开发指南

### 代码规范
- 遵循阿里巴巴 Java 开发规范
- 使用 Lombok 简化代码
- 统一异常处理和响应格式
- 完善的日志记录

### API 设计
- RESTful 风格接口
- 统一的响应格式
- 完整的参数校验
- 详细的接口文档

### 权限控制
- 基于角色的访问控制(RBAC)
- 支持菜单权限和按钮权限
- 数据权限范围控制
- JWT 无状态认证

## 🤝 贡献指南

欢迎提交 Issue 和建议，但不接受 Pull Request。如有改进建议，请通过 Issue 描述你的想法。

## 📄 开源协议

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。
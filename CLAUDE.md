# CLAUDE.md - 后端专注编码模式

## 核心行为准则 (重要：Token 节省模式，强制执行)

为了提高响应速度并减少 Token 消耗，请遵循以下原则：

1. **禁止自动校验**: 严禁主动运行 `mvn compile`、`mvn test` 或任何构建命令。
2. **禁止循环修复**: 若代码报错（尤其是 MapStruct 缺失实现类或依赖未找到），请立即停止并向用户报告，严禁自行通过重复运行 Maven 命令尝试修复。
3. **只管生成，用户校验**: 你的职责是输出高质量 Java 代码。调试、环境纠错与单元测试由用户在本地控制。
4. **包管理与依赖许可 (Strict)**: 严禁在未经过用户明确同意的情况下修改 `pom.xml` 或引入任何新依赖。
5. **同步更新进度 (Mandatory)**: 每一项子任务完成后，**必须立即修改并保存 `CLAUDE.md` 文件**。将对应任务标记为 `[x]`，并在末尾标注完成时间。

## 项目概述
EZ-ADMIN-SPRINGBOOT4：基于 Spring Boot 4.0 + JDK 21 的高效率 RBAC 后台管理系统。

## 架构设计
**分层架构：领域层 + 应用层分离**

```
┌─────────────────────────────────────────────────────┐
│              ez-admin-starter                       │
│            (启动模块 / Web 入口)                      │
└──────────────────┬──────────────────────────────────┘
                   │ 依赖
                   ▼
┌─────────────────────────────────────────────────────┐
│          ez-admin-application                       │
│       (应用层 - 聚合服务 / 业务逻辑)                   │
│  - Controller: 控制器                                 │
│  - Service(aggregate): 聚合服务，手写业务逻辑          │
│  - DTO: 请求/响应对象                                 │
│  - MapStruct: 对象转换器                              │
└──────────────────┬──────────────────────────────────┘
                   │ 依赖
                   ▼
┌─────────────────────────────────────────────────────┐
│            ez-admin-domain                          │
│      (领域层 - 原子服务 / 数据访问)                    │
│  - Entity: 实体类                                     │
│  - Mapper: MyBatis Mapper                            │
│  - Service(atomic): 原子服务，代码生成器生成           │
└──────────────────┬──────────────────────────────────┘
                   │ 依赖
                   ▼
┌─────────────────────────────────────────────────────┐
│         MyBatis-Plus + Database                      │
└─────────────────────────────────────────────────────┘
```

**模块说明**：
- `ez-admin-domain`: 领域层，由代码生成器生成，可安全覆盖
- `ez-admin-application`: 应用层，手写业务逻辑，不会被覆盖

## 技术栈规范
- **命名规范 (Strict)**:
  - 严禁使用模糊命名（如 `BaseReq`, `Handle.java`）。
  - 必须使用明确的语义化命名：`UserQueryDTO`, `RoleResponseVO`, `MenuTreeService`。
- **代码注释 (Mandatory)**: 必须在 Controller 接口、Service 复杂逻辑处编写清晰注释。注释需解释“为什么这么做”以及核心业务逻辑。
- **对象转换**: 强制使用 MapStruct。若遇到转换逻辑复杂，请在 `model/mapstruct/` 下定义转换器接口。

## 常用开发命令
- **运行项目**: `mvn spring-boot:run` (由用户执行)
- **跳过测试打包**: `mvn clean package -DskipTests` (由用户执行)
- **代码生成**: 运行 `CodeGenerator.java` (由用户执行)

## 任务清单 (Todo List)

### 架构重构
- [x] 创建新模块目录结构（domain 和 application） - 2026-01-19
- [x] 创建 ez-admin-domain 父模块 pom.xml - 2026-01-19
- [x] 创建 ez-admin-application 父模块 pom.xml - 2026-01-19
- [x] 迁移现有代码到 ez-admin-domain-system 模块 - 2026-01-19
- [x] 创建 ez-admin-application-system 模块结构 - 2026-01-19
- [x] 更新根 pom.xml 模块配置 - 2026-01-19
- [x] 更新代码生成器配置（模板路径和包路径） - 2026-01-19
- [x] 删除旧的 ez-admin-modules 目录 - 2026-01-19

### 统一认证授权中心 (多端适配 + 双Token机制)

**模块归属**：`ez-admin-auth` (独立认证中心，auth-api + auth-core)

**依赖关系**：
- `ez-admin-auth-core` → `ez-admin-domain-system`（获取 User 等实体）
- `ez-admin-application-*` → `ez-admin-auth-api`（获取认证能力）

#### 核心功能（必须完成）
- [x] 创建 ez-admin-auth 独立模块（auth-api + auth-core） - 2026-01-19
- [x] 重构 framework 模块（提取 Security/Redis/Web 通用能力） - 2026-01-19
- [x] 调整 auth-core 依赖（依赖 framework 而非直接依赖 Security） - 2026-01-19
- [x] 实现双Token机制（Access Token + Refresh Token） - 2026-01-19
- [x] 实现设备管理核心功能（Redis存储设备列表） - 2026-01-19
- [ ] 实现多渠道认证适配器（策略模式）
- [ ] 集成Spring Security 7配置和过滤器链
- [ ] 实现微信小程序登录渠道
- [ ] 实现账号密码登录渠道
- [ ] 实现手机号验证码登录渠道

#### 安全增强
- [ ] 实现Token指纹（设备ID+IP+User-Agent绑定）
- [ ] 实现Refresh Token刷新限流（10次/分钟）
- [ ] 实现异常登录检测（异地、频繁失败）

#### 用户体验优化
- [ ] 实现多设备管理接口（查看、踢出设备）
- [ ] 实现Device Token（记住登录，30天有效期）

#### 监控审计
- [ ] 实现登录/登出/刷新操作日志
- [ ] 实现异常登录告警通知
- [ ] 实现Token使用统计和分析

#### 后期扩展
- [ ] 接入钉钉认证渠道
- [ ] 接入飞书认证渠道
- [ ] 实现多租户认证隔离
- [ ] 实现SSO单点登录

---
*注：每次执行完代码修改后，请确认已勾选上述清单，并告知用户下一项任务是什么。*
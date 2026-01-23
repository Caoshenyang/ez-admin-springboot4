# CLAUDE.md - 后端专注编码模式

## 核心行为准则 (重要：Token 节省模式，强制执行)

为了提高响应速度并减少 Token 消耗，请遵循以下原则：

1. **禁止自动校验**: 严禁主动运行 `mvn compile`、`mvn test` 或任何构建命令。
2. **禁止循环修复**: 若代码报错（尤其是 MapStruct 缺失实现类或依赖未找到），请立即停止并向用户报告，严禁自行通过重复运行 Maven 命令尝试修复。
3. **只管生成，用户校验**: 你的职责是输出高质量 Java 代码。调试、环境纠错与单元测试由用户在本地控制。
4. **包管理与依赖许可 (Strict)**: 严禁在未经过用户明确同意的情况下修改 `pom.xml` 或引入任何新依赖。
5. **同步更新进度 (Mandatory)**: 每一项子任务完成后，**必须立即修改并保存 `CLAUDE.md` 文件**。将对应任务标记为 `[x]`，并在末尾标注完成时间。

## 项目概述
EZ-ADMIN-SPRINGBOOT4：基于 Spring Boot 4.0 + JDK 21 的轻量级 RBAC 后台管理系统，专为个人开发者和小团队设计。

## 架构设计（单体架构）

```
┌─────────────────────────────────────────────────────┐
│                   ez-admin                          │
│              (单体应用 - 极简架构)                    │
│                                                      │
│  ┌──────────────────────────────────────────┐      │
│  │  com.ez.admin                            │      │
│  │  ├── EzAdminApplication.java             │      │
│  │  ├── common/           (通用代码)         │      │
│  │  │   ├── exception/   (异常处理)          │      │
│  │  │   ├── response/    (统一响应)          │      │
│  │  │   ├── redis/       (Redis工具)         │      │
│  │  │   └── web/         (Web配置)           │      │
│  │  │                                        │      │
│  │  ├── system/           (系统管理)          │      │
│  │  │   └── modules/      (业务模块)          │      │
│  │  │       ├── admin/    (核心管理)          │      │
│  │  │       │   ├── entity/                  │      │
│  │  │       │   ├── mapper/                  │      │
│  │  │       │   └── service/                 │      │
│  │  │       └── {其他业务模块}               │      │
│  │  │                                        │      │
│  │  └── config/           (配置类)            │      │
│  └──────────────────────────────────────────┘      │
└─────────────────────────────────────────────────────┘
```

**模块职责**：
- `ez-admin`: 单体应用模块，包含所有功能
  - **common**: 通用代码（异常、响应、Redis、Web配置）
  - **system.modules**: 业务模块（admin、user、role、menu...）
  - **config**: Spring 配置类

**工具模块**：
- `ez-admin-generator`: 代码生成器（独立工具，不参与业务代码编译）

**包结构规则**：
| 包路径 | 代码来源 | 说明 |
|--------|----------|------|
| `com.ez.admin.common` | 手动编写 | 通用代码（异常、响应、Redis、Web） |
| `com.ez.admin.system.modules.admin` | 代码生成 | 核心管理模块（用户、角色、菜单等） |
| `com.ez.admin.system.modules.{module}` | 代码生成 | 其他业务模块 |
| `com.ez.admin.config` | 手动编写 | Spring 配置类 |

**代码生成器使用**：
```bash
# 运行代码生成器（交互式输入）
# 1. 输入模块名：如 admin、blog、order
# 2. 输入表名：如 ez_admin_user 或 all（生成所有表）
java -jar ez-admin-generator/target/ez-admin-generator.jar
```

**设计理念**：
- **单体应用**：所有代码在一个模块，无需跨模块依赖管理
- **包分层**：通过包结构区分功能，而非模块拆分
- **极简开发**：修改代码立即生效，无需重新编译依赖模块
- **适合个人/小团队**：减少配置复杂度，专注业务开发

## 技术栈规范
- **命名规范 (Strict)**:
  - 严禁使用模糊命名（如 `BaseReq`, `Handle.java`）。
  - 必须使用明确的语义化命名：`UserQueryDTO`, `RoleResponseVO`, `MenuTreeService`。
- **代码注释 (Mandatory)**: 必须在 Controller 接口、Service 复杂逻辑处编写清晰注释。注释需解释"为什么这么做"以及核心业务逻辑。
- **对象转换**: 强制使用 MapStruct。若遇到转换逻辑复杂，请在 `model/mapstruct/` 下定义转换器接口。

## 错误码设计规范

### 设计原则
- **易读性**: 5 位数字分段式，便于快速识别错误类型
- **唯一性**: 每个错误码全局唯一，避免歧义
- **分类明确**: 按层级和模块划分，易于扩展

### 错误码结构（ABBCC 格式）

```
A    BB         CC
│    │          │
│    │          └─ 具体错误流水号 (01-99)
│    └──────────── 模块级 (01=通用, 01=用户, 02=角色, 03=菜单, 04=部门, 05=字典, 06=认证, 07=文件)
└───────────────── 服务/大类级 (0=成功, 1=系统级, 2=业务级, 3=三方服务)
```

### 错误码分类

| 首位 | 分类 | 说明 | 示例 |
|------|------|------|------|
| 0 | 成功 | 操作成功 | 00000 |
| 1 | 系统级错误 | 参数、权限、限流等通用错误 | 10001=参数错误, 10002=未授权 |
| 2 | 业务级错误 | 业务逻辑错误（用户、角色、菜单等） | 20101=用户不存在, 20201=角色不存在 |
| 3 | 三方服务错误 | 数据库、Redis、短信平台等 | 30101=数据库错误, 30301=短信失败 |

### 模块划分（BB 部分）

| 模块码 | 模块名称 | 错误码范围 | 说明 |
|--------|----------|------------|------|
| 00 | 通用模块 | 10000-10099 | 系统级通用错误 |
| 01 | 用户模块 | 20100-20199 | 用户登录、注册、信息管理 |
| 02 | 角色模块 | 20200-20299 | 角色管理、权限分配 |
| 03 | 菜单模块 | 20300-20399 | 菜单管理、权限树 |
| 04 | 部门模块 | 20400-20499 | 部门管理、组织架构 |
| 05 | 字典模块 | 20500-20599 | 字典类型、字典数据 |
| 06 | 认证模块 | 20600-20699 | Token、设备、验证码 |
| 07 | 文件模块 | 20700-20799 | 文件上传、下载、管理 |
| 01 | 数据库 | 30100-30199 | 三方服务：数据库 |
| 02 | Redis | 30200-30299 | 三方服务：Redis |
| 03 | 短信服务 | 30300-30399 | 三方服务：短信 |

### 使用示例

```java
// 成功响应
ApiResponse.success(data);  // code: 0

// 系统级错误
throw new EzBusinessException(ErrorCode.BAD_REQUEST);  // 10001
throw new EzBusinessException(ErrorCode.UNAUTHORIZED);  // 10002

// 业务级错误
throw new EzBusinessException(ErrorCode.USER_NOT_FOUND);  // 20101
throw new EzBusinessException(ErrorCode.ROLE_NOT_FOUND);  // 20201

// 三方服务错误
throw new EzBusinessException(ErrorCode.DATABASE_ERROR);  // 30101
throw new EzBusinessException(ErrorCode.SMS_SEND_ERROR);   // 30301
```

### 响应体格式

```json
{
  "code": 20101,
  "message": "用户不存在",
  "data": null,
  "timestamp": 1705978432000
}
```

## 常用开发命令
- **运行项目**: `mvn spring-boot:run` (由用户执行)
- **跳过测试打包**: `mvn clean package -DskipTests` (由用户执行)
- **代码生成**: 运行 `CodeGenerator.java` (由用户执行)

## 任务清单 (Todo List)

### 统一认证授权中心 (多端适配 + 双Token机制)

#### 核心功能（必须完成）
- [x] 创建 ez-admin-auth 独立模块（auth-api + auth-core） - 2026-01-19
- [x] 重构 framework 模块（提取 Security/Redis/Web 通用能力） - 2026-01-19
- [x] 调整 auth-core 依赖（依赖 framework 而非直接依赖 Security） - 2026-01-19
- [x] 实现双Token机制（Access Token + Refresh Token） - 2026-01-19
- [x] 实现设备管理核心功能（Redis存储设备列表） - 2026-01-19
- [x] 重构聚合服务架构（Controller 移至 api 模块） - 2026-01-19
- [x] 优化聚合服务模块划分（api 仅放 Controller+DTO，使用 common/framework） - 2026-01-19
- [x] **简化项目架构（扁平化）** - 2026-01-23
  - 合并 common + framework 模块
  - 移除 application/domain 层级
  - 移除 api/core 子模块拆分
  - 最终结构：starter + auth + system + common + generator
- [x] **极简化项目架构（包分离）** - 2026-01-23
  - 合并 auth → system 模块
  - 通过包结构区分功能（system.auth、system.system）
  - 最终结构：starter + system + common + generator（仅4个模块）
- [x] **重构错误码设计（5位数字分段式）** - 2026-01-23
  - 采用 ABBCC 格式（A=层级, BB=模块, CC=流水号）
  - 0xxxx=成功, 1xxxx=系统级, 2xxxx=业务级, 3xxxx=三方服务
  - 更新 ErrorCode、ApiResponse、EzBusinessException
  - 成功状态码从 200 改为 0
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
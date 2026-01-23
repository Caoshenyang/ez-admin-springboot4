# CLAUDE.md - 后端专注编码模式

## 核心行为准则 (重要：Token 节省模式，强制执行)

为了提高响应速度并减少 Token 消耗，请遵循以下原则：

1. **禁止自动校验**: 严禁主动运行 `mvn compile`、`mvn test` 或任何构建命令。
2. **禁止循环修复**: 若代码报错（尤其是 MapStruct 缺失实现类或依赖未找到），请立即停止并向用户报告，严禁自行通过重复运行 Maven 命令尝试修复。
3. **只管生成，用户校验**: 你的职责是输出高质量 Java 代码。调试、环境纠错与单元测试由用户在本地控制。
4. **包管理与依赖许可 (Strict)**: 严禁在未经过用户明确同意的情况下修改 `pom.xml` 或引入任何新依赖。
5. **禁止私自提交 (Strict)**: 严禁主动执行 `git commit` 操作。只有在用户明确要求时才执行提交。
6. **同步更新进度 (Mandatory)**: 每一项子任务完成后，**必须立即修改并保存 `CLAUDE.md` 文件**。将对应任务标记为 `[x]`，并在末尾标注完成时间。

## 项目概述
EZ-ADMIN-SPRINGBOOT4：基于 Spring Boot 4.0 + JDK 21 的轻量级 RBAC 后台管理系统，专为个人开发者和小团队设计。

## 架构设计（零模块 - 三层分离）

```
┌─────────────────────────────────────────────────────┐
│              ez-admin-springboot4                    │
│              (零模块 - 项目根即代码根)                │
│                                                      │
│  ┌──────────────────────────────────────────┐      │
│  │  com.ez.admin                            │      │
│  │  ├── EzAdminApplication.java             │      │
│  │  │                                        │      │
│  │  ├── api/               (接口层)           │      │
│  │  │   └── auth/          # 认证接口        │      │
│  │  │                                        │      │
│  │  ├── service/           (业务聚合层)        │      │
│  │  │   └── auth/          # 认证业务        │      │
│  │  │                                        │      │
│  │  ├── modules/           (原子服务层)        │      │
│  │  │   └── system/        # 系统原子服务     │      │
│  │  │       ├── entity/    # 实体            │      │
│  │  │       └── mapper/    # 数据访问         │      │
│  │  │                                        │      │
│  │  ├── dto/               (数据传输对象)       │      │
│  │  │   └── auth/          # 认证 DTO        │      │
│  │  │                                        │      │
│  │  ├── common/            (通用代码)          │      │
│  │  ├── utils/            (工具类)            │      │
│  │  │   └── generator/    (代码生成器)        │      │
│  │  └── config/           (配置类)            │      │
│  └──────────────────────────────────────────┘      │
└─────────────────────────────────────────────────────┘
```

**项目结构**（完全扁平）：
```
ez-admin-springboot4/
├── src/
│   ├── main/
│   │   ├── java/com/ez/admin/
│   │   │   ├── EzAdminApplication.java
│   │   │   ├── api/                    # 所有 REST 接口
│   │   │   │   └── auth/
│   │   │   ├── service/                # 业务服务聚合层
│   │   │   │   └── auth/
│   │   │   ├── modules/                # 原子服务（仅数据库操作）
│   │   │   │   └── system/             # 系统模块实体+Mapper
│   │   │   │       ├── entity/
│   │   │   │       └── mapper/
│   │   │   ├── dto/                    # 所有 DTO
│   │   │   │   └── auth/
│   │   │   ├── common/                 # 通用代码
│   │   │   ├── utils/                  # 工具类
│   │   │   │   └── generator/
│   │   │   └── config/                 # 配置类
│   │   └── resources/
│   └── test/
├── doc/                              # 文档
├── pom.xml                           # 唯一 POM 文件
└── CLAUDE.md
```

**包结构规则**：
| 包路径 | 代码来源 | 职责 | 说明 |
|--------|----------|------|------|
| `com.ez.admin.api` | 手动编写 | 接口层 | 所有 REST Controller，接收请求 |
| `com.ez.admin.service` | 手动编写 | 业务聚合层 | 组合原子服务，实现业务逻辑 |
| `com.ez.admin.modules` | 代码生成 | 原子服务层 | 实体+Mapper，仅提供数据库操作能力 |
| `com.ez.admin.dto` | 手动编写 | 数据传输对象 | 接口请求/响应的数据结构 |
| `com.ez.admin.common` | 手动编写 | 通用代码 | 异常、响应、Redis、Web 配置 |
| `com.ez.admin.utils.generator` | 手动编写 | 代码生成器工具类 |
| `com.ez.admin.config` | 手动编写 | Spring 配置类 |

**分层架构原则**：

1. **api（接口层）**
   - 所有 Controller 放在此处
   - 只负责接收请求、参数校验、返回响应
   - 调用 service 层完成业务逻辑

2. **service（业务聚合层）**
   - 业务服务的聚合层，组合原子服务
   - 实现复杂业务逻辑
   - 依赖 modules 的 Mapper 和 Entity

3. **modules（原子服务层）**
   - 仅提供数据库操作能力
   - Entity + Mapper，无业务逻辑
   - 可被 service 层复用

4. **dto（数据传输对象）**
   - 所有接口的请求/响应 DTO
   - 按业务模块分包（auth、user、role 等）

**设计理念**：
- **零模块**：项目根目录即代码根目录，无任何模块嵌套
- **三层分离**：api → service → modules，职责清晰
- **原子服务**：modules 仅提供数据库操作，无业务逻辑
- **业务聚合**：service 层组合原子服务，实现业务逻辑
- **适合个人项目**：极简配置，开箱即用

## 技术栈规范

### 命名规范 (Strict)
- 严禁使用模糊命名（如 `BaseReq`, `Handle.java`）。
- 必须使用明确的语义化命名：`UserQueryDTO`, `RoleResponseVO`, `MenuTreeService`。

### 代码注释 (Mandatory)
- 必须在 Controller 接口、Service 复杂逻辑处编写清晰注释。
- 注释需解释"为什么这么做"以及核心业务逻辑。

### 对象转换
- 强制使用 MapStruct。若遇到转换逻辑复杂，请在 `common/mapstruct/` 下定义转换器接口。

### MyBatis-Plus 使用规范 (核心架构原则)

#### 2.1 分治主义设计哲学

本规范采用"分治主义"策略，在**开发效率**、**代码质量**与**后期可维护性**之间取得最佳平衡：

- **单表与简单逻辑**：拥抱编程式（MyBatis-Plus Wrapper），利用其类型安全和极致效率
- **多表与复杂逻辑**：回归 XML，利用其结构化能力、ResultMap 映射能力和 SQL 优化空间
- **注解 SQL**：**全面禁用**。注解既缺乏编程的可维护性，又缺乏 XML 的结构化美感

#### 2.2 职责分层规范

**实体层 (Entity / VO)**
- **Entity**：严格对应数据库表，仅使用 MyBatis-Plus 注解（`@TableName`, `@TableId`, `@TableField`）
- **VO (View Object)**：用于复杂查询的结果接收，必须在 XML 中定义对应的 `ResultMap`

**Mapper 接口层**
Mapper 是抹平"编程式"与"XML"差异的关键。Service 层不应感知底层实现细节：
- **简单 CRUD**：直接继承 `BaseMapper<T>`
- **单表复杂查询**：在接口中使用 `default` 关键字编写 LambdaWrapper 逻辑
- **多表联查/原生 SQL**：仅声明方法，具体实现在 XML 中

#### 2.3 技术决策准则（什么时候用什么？）

| 场景类型 | 推荐方案 | 实施方式 |
| :--- | :--- | :--- |
| **基础 CRUD** | **MP 内置方法** | `insert`, `updateById`, `deleteById` 等 |
| **单表动态筛选** | **编程式 (Wrapper)** | Mapper 中的 `default` 方法 + `LambdaQueryWrapper` |
| **2-3 表简单联查** | **编程式 / XML** | 逻辑简单可选用 `default` 封装；涉及多字段映射选 XML |
| **复杂关联/报表** | **XML** | 编写自定义 SQL，配置嵌套 `ResultMap` |
| **高性能/极致优化** | **XML** | 需精确控制 SQL 执行计划或使用特定数据库函数 |

#### 2.4 XML 编写规范

**ResultMap 嵌套映射**
```xml
<mapper namespace="com.example.mapper.UserMapper">
    <sql id="Base_Column_List">
        u.id, u.username, u.age, u.dept_id, u.status
    </sql>

    <resultMap id="UserDetailMap" type="com.example.vo.UserVO">
        <id property="id" column="id"/>
        <result property="username" column="username"/>
        <association property="dept" javaType="com.example.entity.Dept">
            <id property="id" column="d_id"/>
            <result property="name" column="d_name"/>
        </association>
        <collection property="roles" ofType="com.example.entity.Role">
            <id property="id" column="r_id"/>
            <result property="roleName" column="r_name"/>
        </collection>
    </resultMap>

    <select id="selectUserDetail" resultMap="UserDetailMap">
        SELECT
            <include refid="Base_Column_List"/>,
            d.id AS d_id, d.name AS d_name,
            r.id AS r_id, r.role_name AS r_name
        FROM sys_user u
        LEFT JOIN sys_dept d ON u.dept_id = d.id
        LEFT JOIN sys_user_role ur ON u.id = ur.user_id
        LEFT JOIN sys_role r ON ur.role_id = r.id
        WHERE u.id = #{id}
    </select>
</mapper>
```

**Mapper 层封装示例**
```java
@Mapper
public interface UserMapper extends BaseMapper<User> {

    // 【规范】单表复杂查询 - 使用 default 封装，对 Service 屏蔽 Wrapper
    default List<User> selectActiveUsers(String keyword) {
        return this.selectList(new LambdaQueryWrapper<User>()
                .eq(User::getStatus, 1)
                .like(StringUtils.hasText(keyword), User::getUsername, keyword));
    }

    // 【规范】多表联查 - XML 实现
    UserVO selectUserDetail(@Param("id") Long id);
}
```

#### 2.5 团队开发红线（强制执行）

- **【禁止】** 在 Service 层拼装超过 3 个条件的 QueryWrapper。此类逻辑必须下沉至 Mapper 层封装为方法
- **【禁止】** 在项目中使用 `@Select`、`@Update` 等注解编写 SQL
- **【强制】** 必须使用 `LambdaQueryWrapper`，严禁在代码中出现硬编码的数据库字段名
- **【强制】** 多表联查 SQL 必须显式定义 ResultMap，严禁使用 Map 或 JSONObject 接收结果
- **【建议】** SQL 关键字保持大写，提高 XML 代码的视觉可读性

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
- **代码生成**: 在 IDE 中直接运行 `CodeGenerator.main()`，路径：`src/main/java/com/ez/admin/utils/generator/CodeGenerator.java` (由用户执行)

## 任务清单 (Todo List) 

---
*注：每次执行完代码修改后，请确认已勾选上述清单，并告知用户下一项任务是什么。*
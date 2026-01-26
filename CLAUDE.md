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
│  │  │       ├── mapper/    # 数据访问         │      │
│  │  │       └── service/   # 原子服务(代码生成)│     │
│  │  │                                        │      │
│  │  ├── dto/               (数据传输对象)       │      │
│  │  │   └── auth/          # 认证 DTO        │      │
│  │  │                                        │      │
│  │  ├── common/            (通用代码)          │      │
│  │  │   ├── exception/    # 异常处理          │      │
│  │  │   ├── model/        # 通用模型          │      │
│  │  │   ├── redis/        # Redis工具         │      │
│  │  │   └── web/          # Web配置           │      │
│  │  │                                        │      │
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
│   │   │   │   └── auth/               # 认证接口
│   │   │   ├── service/                # 业务服务聚合层（手动编写）
│   │   │   │   └── auth/               # 认证业务聚合
│   │   │   ├── modules/                # 原子服务层（代码生成）
│   │   │   │   └── system/             # 系统管理模块
│   │   │   │       ├── entity/         # 实体类（10个）
│   │   │   │       ├── mapper/         # 数据访问层（10个）
│   │   │   │       └── service/        # 原子服务（10个）
│   │   │   ├── dto/                    # 所有 DTO
│   │   │   │   └── auth/               # 认证 DTO
│   │   │   │       ├── req/            # 请求对象
│   │   │   │       └── vo/             # 响应对象
│   │   │   ├── common/                 # 通用模块
│   │   │   │   ├── exception/          # 异常处理
│   │   │   │   ├── model/              # 统一响应体
│   │   │   │   ├── redis/              # Redis工具
│   │   │   │   └── web/                # Web配置
│   │   │   ├── utils/                  # 工具类
│   │   │   │   └── generator/          # 代码生成器
│   │   │   └── config/                 # 配置类（预留）
│   │   └── resources/
│   │       ├── mapper/                 # MyBatis XML
│   │       ├── application.yml
│   │       └── application-*.yml
│   └── test/
├── doc/                              # 文档
├── pom.xml                           # 唯一 POM 文件
└── CLAUDE.md
```

**包结构规则**：

| 包路径 | 代码来源 | 职责 | 说明 |
|--------|----------|------|------|
| `com.ez.admin.api` | 手动编写 | 接口层 | 所有 REST Controller，接收请求、参数校验、返回响应 |
| `com.ez.admin.service` | 手动编写 | 业务聚合层 | 组合原子服务，实现复杂业务逻辑（如认证、权限判断） |
| `com.ez.admin.modules.system.entity` | 代码生成 | 实体层 | 数据库实体类（SysUser、SysRole、SysMenu 等 10 个） |
| `com.ez.admin.modules.system.mapper` | 代码生成 | 数据访问层 | MyBatis Mapper 接口，仅提供 CRUD 能力 |
| `com.ez.admin.modules.system.service` | 代码生成 | 原子服务层 | 单表 CRUD 服务（SysUserService、SysRoleService 等 10 个） |
| `com.ez.admin.dto.{module}.req` | 手动编写 | 请求对象 | xxxReq（如 LoginReq、UserCreateReq） |
| `com.ez.admin.dto.{module}.vo` | 手动编写 | 响应对象 | xxxVO（如 LoginVO、UserInfoVO） |
| `com.ez.admin.common.exception` | 手动编写 | 异常处理 | ErrorCode、EzBusinessException、GlobalExceptionHandler |
| `com.ez.admin.common.model` | 手动编写 | 通用模型 | 统一响应体 R |
| `com.ez.admin.common.redis` | 手动编写 | Redis 工具 | RedisCache、RedisTemplateConfig |
| `com.ez.admin.common.web` | 手动编写 | Web 配置 | OpenApiConfig、PasswordEncoderConfig |
| `com.ez.admin.utils.generator` | 手动编写 | 代码生成器 | 生成 modules/system 下的实体、Mapper、Service |

**分层架构原则**：

1. **api（接口层）**
   - 所有 Controller 放在此处
   - 只负责接收请求、参数校验、返回响应
   - 调用 service 层完成业务逻辑
   - 不直接访问 modules 层

2. **service（业务聚合层）**
   - 业务服务的聚合层，组合原子服务实现复杂业务逻辑
   - 例如：AuthService（认证）、UserService（用户管理聚合）
   - 依赖 modules 的 Mapper 和 Entity
   - 可以调用 modules/system/service/ 下的原子服务
   - **禁止使用接口层**：直接使用实现类（如 `AuthService`），不创建 `IAuthService` 接口
   - **命名规范**：Service 类直接命名为 `xxxService`（如 `AuthService`），禁止使用 `xxxServiceImpl` 命名

3. **modules（原子服务层）**
   - **modules/system/entity/**：数据库实体类，对应数据库表
   - **modules/system/mapper/**：MyBatis Mapper 接口，仅提供数据访问能力
   - **modules/system/service/**：代码生成的单表 CRUD 服务（SysUserService、SysRoleService 等）
   - 仅提供基础的增删改查能力，无复杂业务逻辑
   - 可被 service 层复用

4. **dto（数据传输对象）**
   - 所有接口的请求/响应对象
   - 按业务模块分包（auth、user、role 等）
   - 每个模块下再分 req（请求）和 vo（响应）
   - 请求对象：xxxReq（如 LoginReq、UserCreateReq）
   - 响应对象：按用途区分命名（见下文 VO 命名规范）
   - 例如：dto/auth/req/LoginReq.java
   - 例如：dto/auth/vo/LoginVO.java

### VO 命名规范 (Strict)

**原则**：根据 VO 的用途明确命名，职责单一，避免歧义。

| VO 类型 | 命名格式 | 用途 | 示例 |
|---------|---------|------|------|
| **详情 VO** | `xxxDetailVO` | 单个资源详情（getById） | `UserDetailVO`, `RoleDetailVO`, `MenuDetailVO` |
| **列表 VO** | `xxxListVO` | 分页列表、简单列表（getPage） | `UserListVO`, `RoleListVO`, `MenuListVO` |
| **树形 VO** | `xxxTreeVO` | 树形结构（getTree） | `DeptTreeVO`, `MenuTreeVO` |
| **其他 VO** | `xxx{用途}VO` | 特定场景（如登录响应） | `LoginVO`, `InstallVO` |

**字段差异**：
- **ListVO**：只包含列表展示需要的字段（如 ID、名称、状态等）
- **DetailVO**：包含完整字段（所有业务字段）
- **TreeVO**：包含完整字段 + children 列表（继承 `TreeNode<TreeVO>`）

**示例**：
```java
// ✅ 正确 - 职责清晰
UserListVO    - 分页列表（userId, username, nickname, status）
UserDetailVO  - 详情（完整字段，包括 email, phone, createTime 等）
UserTreeVO    - 树形（完整字段 + children，继承 TreeNode）

// ❌ 错误 - 命名模糊
UserVO        - 无法区分是列表还是详情
UserInfoVO    - 与 DetailVO 重复且不直观
```

**MapStruct 转换示例**：
```java
@Mapper(componentModel = "spring")
public interface UserConverter {
    // Entity → DetailVO（详情）
    UserDetailVO toDetailVO(SysUser user);

    // Entity → ListVO（列表）
    UserListVO toListVO(SysUser user);

    // Entity → TreeVO（树形）
    UserTreeVO toTreeVO(SysUser user);
}
```

**设计理念**：
- **零模块**：项目根目录即代码根目录，无任何模块嵌套
- **三层分离**：api → service → modules，职责清晰
- **原子服务**：modules 仅提供数据库操作和单表 CRUD，无复杂业务逻辑
- **业务聚合**：service 层组合原子服务，实现复杂业务逻辑
- **代码生成**：modules/system/ 下的代码由代码生成器生成，包含完整 CRUD
- **手动编写**：api、service、dto 层的代码手动编写，实现业务逻辑
- **适合个人项目**：极简配置，开箱即用

## 技术栈规范

### 命名规范 (Strict)
- 严禁使用模糊命名（如 `BaseReq`, `Handle.java`）。
- 必须使用明确的语义化命名：`UserQueryDTO`, `RoleResponseVO`, `MenuTreeService`。

### 代码注释 (Mandatory)

#### @Schema 注解规范 (Strict)
所有 DTO、VO、Query、Response 等数据传输对象**必须**使用 `@Schema` 注解，格式要求：

**基本格式**：
```java
@Schema(description = "字段说明", example = "示例值")
private String fieldName;
```

**规范要求**：
- **description**：必填，清晰说明字段用途和业务含义
- **example**：字段值有固定枚举或典型值时必填（如状态码、ID、关键词等）
- 列表、对象类型字段可不填 example

**示例**：
```java
@Data
@Schema(description = "通用分页查询对象")
public class PageQuery {

    @Schema(description = "当前页码（从 1 开始）", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "快捷搜索关键词（模糊匹配）", example = "admin")
    private String keyword;

    @Schema(description = "高级查询条件列表")
    private List<QueryCondition> conditions;  // 列表类型无需 example
}
```

**字段类型示例值参考**：
| 字段类型 | example 示例 | 说明 |
|---------|-------------|------|
| Integer/Long | `"1"`, `"100"` | 数字使用字符串格式 |
| String | `"admin"`, `"active"` | 典型业务值 |
| Boolean | `"true"`, `"false"` | 布尔值使用字符串格式 |
| LocalDateTime | `"2024-01-01T00:00:00"` | ISO 8601 格式 |
| Enum | `"ACTIVE"`, `"ENABLED"` | 枚举常量 |
| List/Object | 无需填写 | 复杂类型不填 example |

#### 代码注释规范
- 必须在 Controller 接口、Service 复杂逻辑处编写清晰注释
- 注释需解释"为什么这么做"以及核心业务逻辑
- 优先使用 `@Schema` 注解替代 JavaDoc，Swagger 文档会自动提取

### 对象转换
- 强制使用 MapStruct。若遇到转换逻辑复杂，请在 `common/mapstruct/` 下定义转换器接口。

### 对象构造规范 (Strict)

根据对象用途区分注解组合：

#### 1. DTO (Req) - 请求对象（使用 @Data）
- **注解组合**：`@Data` + `@Schema`
- **使用 @Data 的原因**：
  - DTO 通常由框架从 JSON 反序列化而来，很少手动构造
  - `@Data` 提供了所有必要的方法：getter、setter、toString、equals、hashCode
  - 注解简洁，只需 2 个
  - `@Data` 包含无参构造器，Jackson 反序列化无障碍

**示例**：
```java
// ✅ 正确 - DTO (Req)
@Data
@Schema(name = "LoginReq", description = "登录请求")
public class LoginReq {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}

// 使用 - 框架自动从 JSON 反序列化
// POST /auth/login
// Request Body: {"username": "admin", "password": "123456"}
```

```java
// ❌ 不推荐 - DTO 不需要 Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder  // ❌ DTO 很少手动构造，不需要 Builder
@Schema(name = "LoginReq", description = "登录请求")
public class LoginReq {
    private String username;
    private String password;
}
```

#### 2. VO - 响应对象（使用 @Builder）
- **注解组合**：`@Getter` + `@Builder` + `@Schema`
- **使用 Builder 的原因**：
  - 响应对象构造后不应被修改（不可变性）
  - MapStruct 1.6.3 完全支持 Builder 模式
  - 手动构造时代码可读性强
  - 移除 `@Setter`，强制对象不可变

**示例**：
```java
// ✅ 正确 - VO
@Getter
@Builder
@Schema(name = "LoginVO", description = "登录响应")
public class LoginVO {
    private String token;
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
}

// 使用
LoginVO response = LoginVO.builder()
    .token(StpUtil.getTokenValue())
    .userId(user.getUserId())
    .username(user.getUsername())
    .nickname(user.getNickname())
    .avatar(user.getAvatar())
    .build();
```

```java
// ❌ 错误 - VO 不应有 setter
@Getter
@Setter  // ❌ VO 不应该有 setter
@Builder
public class LoginVO {
    private String token;
    private Long userId;
}

// ❌ 错误 - 不应使用 new + setter
LoginVO response = new LoginVO();
response.setToken(StpUtil.getTokenValue());
response.setUserId(user.getUserId());
```

#### 3. Entity 对象 - 保持默认方式
- **注解组合**：`@Data`（自动生成 getter/setter）
- **原因**：
  - MyBatis-Plus 反射需要无参构造器和 setter
  - 框架需要将数据库数据映射到对象

**示例**：
```java
// ✅ 正确 - Entity 使用 @Data
@Data
@TableName("sys_user")
public class SysUser {
    @TableId
    private Long userId;
    private String username;
    private String password;
    // ... @Data 自动生成 getter/setter
}
```

**规范总结**：
| 对象类型 | 推荐注解 | 注解数量 | 构造方式 | 理由 |
|---------|---------|---------|---------|------|
| **DTO (Req)** | `@Data` + `@Schema` | **2 个** | 框架自动反序列化 | 框架自动构造，很少手动构造 |
| **VO** | `@Getter` + `@Builder` + `@Schema` | **3 个** | `Xxx.builder().field(value).build()` | 不可变性、MapStruct 支持 |
| **Entity** | `@Data` + `@TableName` | 2 个 | `new Xxx()` + setter | MyBatis-Plus 反射需要 |

**MapStruct + Builder 集成说明**：
- MapStruct 1.3.0+ 自动识别 `@Builder` 注解
- 本项目使用 MapStruct 1.6.3，**完全支持** Builder 模式
- 无需额外配置，MapStruct 自动使用 builder 构造 VO 对象
- 示例见下文"MapStruct 使用示例"

### MapStruct 使用示例

#### 1. 定义 VO（使用 @Builder）

```java
@Getter
@Builder
@Schema(name = "LoginVO", description = "登录响应")
public class LoginVO {
    private String token;
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
}
```

#### 2. 定义 Converter 接口

```java
@Mapper(componentModel = "spring")
public interface UserConverter {
    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

    // MapStruct 自动识别 @Builder，生成 builder 模式代码
    LoginVO toLoginVO(SysUser user);

    // 支持多参数
    @Mapping(source = "token", target = "token")
    LoginVO toLoginVOWithToken(SysUser user, String token);
}
```

#### 3. MapStruct 生成的实现类（自动）

```java
@Component
public class UserConverterImpl implements UserConverter {

    @Override
    public LoginVO toLoginVO(SysUser user) {
        if (user == null) {
            return null;
        }

        // ✅ MapStruct 自动使用 builder
        return LoginVO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .build();
    }

    @Override
    public LoginVO toLoginVOWithToken(SysUser user, String token) {
        if (user == null) {
            return null;
        }

        return LoginVO.builder()
                .token(token)  // 额外参数
                .userId(user.getUserId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .build();
    }
}
```

#### 4. 使用 Converter

```java
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserConverter userConverter;  // 注入

    public LoginVO login(LoginReq request) {
        SysUser user = userMapper.selectByUsername(request.getUsername());

        // 方式1：手动 builder（当前使用）
        LoginVO response = LoginVO.builder()
                .token(StpUtil.getTokenValue())
                .userId(user.getUserId())
                .username(user.getUsername())
                .build();

        // 方式2：使用 MapStruct（推荐用于复杂转换）
        LoginVO response = userConverter.toLoginVOWithToken(user, StpUtil.getTokenValue());

        return response;
    }
}
```

#### 5. 高级用法

**字段映射**：
```java
@Mapping(source = "userId", target = "id")  // 字段名不同
@Mapping(source = "nickname", target = "nickName")
UserVO toVO(SysUser user);
```

**嵌套对象**：
```java
// MapStruct 会递归使用 builder
UserDetailVO toDetailVO(SysUser user);  // UserDetailVO 中包含 DeptVO、List<RoleVO>
```

**集合映射**：
```java
List<UserVO> toVOList(List<SysUser> users);  // 自动批量转换
```

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
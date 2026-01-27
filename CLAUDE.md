# CLAUDE.md - 后端开发规范

## 核心行为准则

1. **禁止自动校验**: 严禁主动运行 `mvn compile`、`mvn test`
2. **禁止循环修复**: 遇到 MapStruct 等错误立即报告，严禁自行重复运行 Maven
3. **只管生成，用户校验**: 职责是输出高质量 Java 代码，调试由用户控制
4. **包管理与依赖许可**: 严禁未经同意修改 `pom.xml` 或引入新依赖
5. **禁止私自提交**: 严禁主动执行 `git commit`，只有用户明确要求时才执行
6. **类型显式声明**: 禁止使用 `var` 关键字，必须明确声明变量类型

---

## 项目概述

EZ-ADMIN-SPRINGBOOT4：基于 Spring Boot 4.0 + JDK 21 的轻量级 RBAC 后台管理系统。

---

## 架构设计（零模块 - 三层分离）

```
ez-admin-springboot4/
├── src/main/java/com/ez/admin/
│   ├── api/                # 接口层（手动编写）
│   ├── service/            # 业务聚合层（手动编写）
│   ├── modules/system/     # 原子服务层（代码生成）
│   │   ├── entity/         # 实体类
│   │   ├── mapper/         # 数据访问层
│   │   └── service/        # 原子服务
│   ├── dto/                # 数据传输对象（手动编写）
│   │   └── {module}/
│   │       ├── req/        # 请求对象
│   │       └── vo/         # 响应对象
│   └── common/             # 通用模块
└── doc/                    # 详细文档（见 doc/README.md）
```

### 分层职责

| 层级 | 职责 | 说明 |
|------|------|------|
| **api** | 接口层 | 接收请求、参数校验、返回响应 |
| **service** | 业务聚合层 | 组合原子服务，实现复杂业务逻辑 |
| **modules** | 原子服务层 | 单表 CRUD，无复杂业务逻辑 |

---

## 核心技术规范

### 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| VO | 按用途区分 | `UserDetailVO`, `UserListVO`, `UserTreeVO` |
| Service | 直接命名 | `AuthService`（禁止 `IAuthService`） |
| 禁止 | 模糊命名 | 禁止 `BaseReq`, `Handle.java` |

**详细规范**: [doc/vo-naming-spec.md](./doc/vo-naming-spec.md)

### 注解规范

- **所有 DTO、VO 必须使用 `@Schema` 注解**
- description 必填，example 按需填写

**详细规范**: [doc/annotation-spec.md](./doc/annotation-spec.md)

### 对象构造规范

| 对象类型 | 推荐注解 |
|---------|---------|
| DTO (Req) | `@Data` + `@Schema` |
| VO | `@Getter` + `@Builder` + `@Schema` |
| Entity | `@Data` + `@TableName` |

**【禁止】** 使用 `record` 关键字定义数据类。所有数据传输对象必须使用传统类 + Lombok 注解。

**【禁止】** 使用 `var` 关键字声明变量。所有变量必须明确声明类型。

**详细规范**: [doc/object-construction-spec.md](./doc/object-construction-spec.md)

### MapStruct

- 强制使用 MapStruct 进行对象转换
- 所有 Converter 接口放在 `common/mapstruct/` 下

**使用指南**: [doc/mapstruct-guide.md](./doc/mapstruct-guide.md)

### MyBatis-Plus

- **单表与简单逻辑**：拥抱编程式（MyBatis-Plus Wrapper），利用其类型安全和极致效率
- **多表与复杂逻辑**：回归 XML，利用其结构化能力、ResultMap 映射能力和 SQL 优化空间
- **注解 SQL**：**全面禁用**。注解既缺乏编程的可维护性，又缺乏 XML 的结构化美感

- **【禁止】** 在 Service 层拼装超过 3 个条件的 QueryWrapper。此类逻辑必须下沉至 Mapper 层封装为方法
- **【禁止】** 在项目中使用 `@Select`、`@Update` 等注解编写 SQL
- **【强制】** 必须使用 `LambdaQueryWrapper`，严禁在代码中出现硬编码的数据库字段名
- **【强制】** 多表联查 SQL 必须显式定义 ResultMap，严禁使用 Map 或 JSONObject 接收结果
- **【建议】** SQL 关键字保持大写，提高 XML 代码的视觉可读性

## 技术决策准则

| 场景类型 | 推荐方案 | 实施方式 |
|---------|---------|---------|
| **基础 CRUD** | **MP 内置方法** | `insert`, `updateById`, `deleteById` 等 |
| **单表动态筛选** | **编程式 (Wrapper)** | Mapper 中的 `default` 方法 + `LambdaQueryWrapper` |
| **2-3 表简单联查** | **编程式 / XML** | 逻辑简单可选用 `default` 封装；涉及多字段映射选 XML |
| **复杂关联/报表** | **XML** | 编写自定义 SQL，配置嵌套 `ResultMap` |
| **高性能/极致优化** | **XML** | 需精确控制 SQL 执行计划或使用特定数据库函数 |

---

## 错误码设计

| 首位 | 分类 | 示例范围 |
|------|------|---------|
| 0 | 成功 | 00000 |
| 1 | 系统级错误 | 10001-10099 |
| 2 | 业务级错误 | 20101-20799 |
| 3 | 三方服务错误 | 30100-30399 |

**详细规范**: [doc/error-code-design.md](./doc/error-code-design.md)

---

## 常用开发命令

```bash
# 运行项目
mvn spring-boot:run

# 跳过测试打包
mvn clean package -DskipTests

# 代码生成
# 在 IDE 中直接运行 CodeGenerator.main()
```

---

## 详细文档索引

所有详细设计文档、使用指南已归档到 `doc/` 目录：

- [doc/README.md](./doc/README.md) - 文档索引
- [doc/vo-naming-spec.md](./doc/vo-naming-spec.md) - VO 命名规范
- [doc/annotation-spec.md](./doc/annotation-spec.md) - @Schema 注解规范
- [doc/object-construction-spec.md](./doc/object-construction-spec.md) - 对象构造规范
- [doc/mapstruct-guide.md](./doc/mapstruct-guide.md) - MapStruct 使用指南
- [doc/mybatis-plus-guide.md](./doc/mybatis-plus-guide.md) - MyBatis-Plus 使用规范
- [doc/error-code-design.md](./doc/error-code-design.md) - 错误码设计规范

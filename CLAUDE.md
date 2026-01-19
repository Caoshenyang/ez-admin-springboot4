# CLAUDE.md - 后端专注编码模式

## 核心行为准则 (重要：Token 节省模式，强制执行)

为了提高响应速度并减少 Token 消耗，请遵循以下原则：

1. **禁止自动校验**: 严禁主动运行 `mvn compile`、`mvn test` 或任何构建命令。
2. **禁止循环修复**: 若代码报错（尤其是 MapStruct 缺失实现类或依赖未找到），请立即停止并向用户报告，严禁自行通过重复运行 Maven 命令尝试修复。
3. **只管生成，用户校验**: 你的职责是输出高质量 Java 代码。调试、环境纠错与单元测试由用户在本地控制。
4. **包管理与依赖许可 (Strict)**: 严禁在未经过用户明确同意的情况下修改 `pom.xml` 或引入任何新依赖。
5. **同步更新进度 (Mandatory)**: 每一项子任务完成后，**必须立即修改并保存 `CLAUDE.md` 文件**。将对应任务标记为 `[x]`，并在末尾标注完成时间。

## 项目概述
EZ-ADMIN-SPRINGBOOT4：基于 Spring Boot 4.0 + JDK 21 的高效率 Rren wBAC 后台管理系统。

## 技术栈规范
- **命名规范 (Strict)**:
  - 严禁使用模糊命名（如 `BaseReq`, `Handle.java`）。
  - 必须使用明确的语义化命名：`UserQueryDTO`, `RoleResponseVO`, `MenuTreeService`。
- **代码注释 (Mandatory)**: 必须在 Controller 接口、Service 复杂逻辑处编写清晰注释。注释需解释“为什么这么做”以及核心业务逻辑。
- **对象转换**: 强制使用 MapStruct。若遇到转换逻辑复杂，请在 `model/mapstruct/` 下定义转换器接口。

## 常用开发命令
- **运行项目**: `mvn spring-boot:run` (由用户执行)
- **跳过测试打包**: `mvn clean package -DskipTests` (由用户执行)

## 任务清单 (Todo List) 

---
*注：每次执行完代码修改后，请确认已勾选上述清单，并告知用户下一项任务是什么。*
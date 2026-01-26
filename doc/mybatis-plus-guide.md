# MyBatis-Plus 使用规范

## 设计哲学

采用"分治主义"策略，在**开发效率**、**代码质量**与**后期可维护性**之间取得最佳平衡：

- **单表与简单逻辑**：拥抱编程式（MyBatis-Plus Wrapper），利用其类型安全和极致效率
- **多表与复杂逻辑**：回归 XML，利用其结构化能力、ResultMap 映射能力和 SQL 优化空间
- **注解 SQL**：**全面禁用**。注解既缺乏编程的可维护性，又缺乏 XML 的结构化美感

---

## 职责分层规范

### 实体层 (Entity / VO)
- **Entity**：严格对应数据库表，仅使用 MyBatis-Plus 注解（`@TableName`, `@TableId`, `@TableField`）
- **VO (View Object)**：用于复杂查询的结果接收，必须在 XML 中定义对应的 `ResultMap`

### Mapper 接口层
Mapper 是抹平"编程式"与"XML"差异的关键。Service 层不应感知底层实现细节：
- **简单 CRUD**：直接继承 `BaseMapper<T>`
- **单表复杂查询**：在接口中使用 `default` 关键字编写 LambdaWrapper 逻辑
- **多表联查/原生 SQL**：仅声明方法，具体实现在 XML 中

---

## 技术决策准则

| 场景类型 | 推荐方案 | 实施方式 |
|---------|---------|---------|
| **基础 CRUD** | **MP 内置方法** | `insert`, `updateById`, `deleteById` 等 |
| **单表动态筛选** | **编程式 (Wrapper)** | Mapper 中的 `default` 方法 + `LambdaQueryWrapper` |
| **2-3 表简单联查** | **编程式 / XML** | 逻辑简单可选用 `default` 封装；涉及多字段映射选 XML |
| **复杂关联/报表** | **XML** | 编写自定义 SQL，配置嵌套 `ResultMap` |
| **高性能/极致优化** | **XML** | 需精确控制 SQL 执行计划或使用特定数据库函数 |

---

## XML 编写规范

### ResultMap 嵌套映射
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

### Mapper 层封装示例
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

---

## 团队开发红线（强制执行）

- **【禁止】** 在 Service 层拼装超过 3 个条件的 QueryWrapper。此类逻辑必须下沉至 Mapper 层封装为方法
- **【禁止】** 在项目中使用 `@Select`、`@Update` 等注解编写 SQL
- **【强制】** 必须使用 `LambdaQueryWrapper`，严禁在代码中出现硬编码的数据库字段名
- **【强制】** 多表联查 SQL 必须显式定义 ResultMap，严禁使用 Map 或 JSONObject 接收结果
- **【建议】** SQL 关键字保持大写，提高 XML 代码的视觉可读性

---

## 常见问题

### 1. 为什么禁用注解 SQL？
- 注解 SQL 缺乏格式化，可读性差
- 复杂 SQL 在注解中难以维护
- XML 支持复用（`<sql>` 片段）、ResultMap 等高级特性

### 2. 为什么必须在 Mapper 层封装 Wrapper？
- Service 层不应感知底层实现细节
- 封装后可复用，避免重复代码
- 便于后续替换实现（如改为 XML）

### 3. 什么时候用 XML，什么时候用 Wrapper？
- 单表、条件简单 → Wrapper
- 多表、字段映射复杂、需要优化 SQL → XML

# @Schema 注解规范

## 基本格式

```java
@Schema(description = "字段说明", example = "示例值")
private String fieldName;
```

## 规范要求

- **description**：必填，清晰说明字段用途和业务含义
- **example**：字段值有固定枚举或典型值时必填（如状态码、ID、关键词等）
- 列表、对象类型字段可不填 example

## PageQuery 示例

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

## 字段类型示例值参考

| 字段类型 | example 示例 | 说明 |
|---------|-------------|------|
| Integer/Long | `"1"`, `"100"` | 数字使用字符串格式 |
| String | `"admin"`, `"active"` | 典型业务值 |
| Boolean | `"true"`, `"false"` | 布尔值使用字符串格式 |
| LocalDateTime | `"2024-01-01T00:00:00"` | ISO 8601 格式 |
| Enum | `"ACTIVE"`, `"ENABLED"` | 枚举常量 |
| List/Object | 无需填写 | 复杂类型不填 example |

## 完整 VO 示例

```java
@Getter
@Builder
@Schema(name = "UserDetailVO", description = "用户详情响应对象")
public class UserDetailVO {

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "用户名", example = "admin")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "用户状态【0 停用 1 正常】", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
```

## 注意事项

1. **description 必填**：所有字段都必须有描述
2. **example 按需填写**：有典型值时填写，复杂类型省略
3. **优先使用 @Schema**：Swagger 文档会自动提取，减少 JavaDoc 编写
4. **枚举值说明**：在 description 中说明所有可能的值

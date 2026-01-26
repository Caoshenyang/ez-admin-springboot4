# 对象构造规范

## 概述

根据对象用途区分注解组合，确保代码一致性和类型安全。

## 规范总结

| 对象类型 | 推荐注解 | 注解数量 | 构造方式 | 理由 |
|---------|---------|---------|---------|------|
| **DTO (Req)** | `@Data` + `@Schema` | **2 个** | 框架自动反序列化 | 框架自动构造，很少手动构造 |
| **VO** | `@Getter` + `@Builder` + `@Schema` | **3 个** | `Xxx.builder().field(value).build()` | 不可变性、MapStruct 支持 |
| **Entity** | `@Data` + `@TableName` | 2 个 | `new Xxx()` + setter | MyBatis-Plus 反射需要 |

---

## 1. DTO (Req) - 请求对象

### 注解组合
```java
@Data
@Schema(name = "LoginReq", description = "登录请求")
public class LoginReq {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
```

### 为什么使用 @Data
- DTO 通常由框架从 JSON 反序列化而来，很少手动构造
- `@Data` 提供了所有必要的方法：getter、setter、toString、equals、hashCode
- 注解简洁，只需 2 个
- `@Data` 包含无参构造器，Jackson 反序列化无障碍

### 使用方式
```java
// 框架自动从 JSON 反序列化
// POST /auth/login
// Request Body: {"username": "admin", "password": "123456"}
```

### 错误示例
```java
// ❌ 不推荐 - DTO 不需要 Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder  // ❌ DTO 很少手动构造
@Schema(name = "LoginReq", description = "登录请求")
public class LoginReq {
    private String username;
    private String password;
}
```

---

## 2. VO - 响应对象

### 注解组合
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

### 为什么使用 Builder
- 响应对象构造后不应被修改（不可变性）
- MapStruct 1.6.3 完全支持 Builder 模式
- 手动构造时代码可读性强
- 移除 `@Setter`，强制对象不可变

### 使用方式
```java
LoginVO response = LoginVO.builder()
    .token(StpUtil.getTokenValue())
    .userId(user.getUserId())
    .username(user.getUsername())
    .nickname(user.getNickname())
    .avatar(user.getAvatar())
    .build();
```

### 错误示例
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

---

## 3. Entity 对象

### 注解组合
```java
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

### 为什么使用 @Data
- MyBatis-Plus 反射需要无参构造器和 setter
- 框架需要将数据库数据映射到对象

---

## MapStruct + Builder 集成

### MapStruct 版本要求
- MapStruct 1.3.0+ 自动识别 `@Builder` 注解
- 本项目使用 MapStruct 1.6.3，**完全支持** Builder 模式
- 无需额外配置，MapStruct 自动使用 builder 构造 VO 对象

### 定义 Converter 接口
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

### MapStruct 生成的实现类
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

### 使用 Converter
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

### 高级用法

**字段映射**
```java
@Mapping(source = "userId", target = "id")  // 字段名不同
@Mapping(source = "nickname", target = "nickName")
UserVO toVO(SysUser user);
```

**嵌套对象**
```java
// MapStruct 会递归使用 builder
UserDetailVO toDetailVO(SysUser user);  // UserDetailVO 中包含 DeptVO、List<RoleVO>
```

**集合映射**
```java
List<UserVO> toVOList(List<SysUser> users);  // 自动批量转换
```

# MapStruct 使用指南

## 概述

MapStruct 是一个代码生成器，它基于约定优于配置的原则，极大地简化了 Java Bean 类型之间映射的实现。

## 项目配置

### Maven 依赖
```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.6.3</version>
</dependency>
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct-processor</artifactId>
    <version>1.6.3</version>
    <scope>provided</scope>
</dependency>
```

### Maven 插件配置
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.mapstruct</groupId>
                        <artifactId>mapstruct-processor</artifactId>
                        <version>1.6.3</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

---

## 定义 Converter

### 基本格式
```java
@Mapper(componentModel = "spring")
public interface UserConverter {
    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

    // 目标类型 to源类型(源类型)
    UserVO toVO(SysUser user);
}
```

### componentModel 说明
- **spring**：生成的实现类标注 `@Component`，可通过 `@Autowired` 注入
- **default**：默认模式，通过 `Mappers.getMapper()` 获取实例

---

## 基础用法

### 1. 简单映射
```java
@Mapper(componentModel = "spring")
public interface UserConverter {
    UserVO toVO(SysUser user);
}
```

**生成实现**：
```java
@Component
public class UserConverterImpl implements UserConverter {
    @Override
    public UserVO toVO(SysUser user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        userVO.setUserId(user.getUserId());
        userVO.setUsername(user.getUsername());
        // ... 其他字段
        return userVO;
    }
}
```

### 2. 集合映射
```java
List<UserVO> toVOList(List<SysUser> users);
```

### 3. 字段名不同
```java
@Mapping(source = "userId", target = "id")
@Mapping(source = "nickname", target = "nickName")
UserVO toVO(SysUser user);
```

### 4. 多参数映射
```java
@Mapping(source = "user.userId", target = "userId")
@Mapping(source = "token", target = "token")
LoginVO toLoginVO(SysUser user, String token);
```

### 5. 常量映射
```java
@Mapping(source = "status", target = "status", defaultValue = "1")
UserVO toVO(SysUser user);
```

### 6. 忽略字段
```java
@Mapping(target = "password", ignore = true)
UserVO toVO(SysUser user);
```

---

## 高级用法

### 1. 嵌套对象映射
```java
// UserDetailVO 包含 DeptVO 和 List<RoleVO>
UserDetailVO toDetailVO(SysUser user);

// MapStruct 自动递归使用 builder
```

### 2. 自定义转换方法
```java
@Mapper(componentModel = "spring")
public interface UserConverter {
    UserVO toVO(SysUser user);

    @Named("statusConverter")
    default Integer statusToString(Integer status) {
        return status == null ? 0 : status;
    }

    @Mapping(target = "status", qualifiedByName = "statusConverter")
    UserVO toVOWithConverter(SysUser user);
}
```

### 3. 表达式映射
```java
@Mapping(target = "fullName", expression = "java(user.getFirstName() + \" \" + user.getLastName())")
UserVO toVO(SysUser user);
```

---

## Builder 模式集成

### MapStruct 与 Builder
- MapStruct 1.3.0+ 自动识别 `@Builder` 注解
- 本项目使用 MapStruct 1.6.3，**完全支持** Builder 模式
- 无需额外配置，MapStruct 自动使用 builder 构造 VO 对象

### 示例
```java
// VO 使用 @Builder
@Getter
@Builder
@Schema(name = "UserVO", description = "用户响应对象")
public class UserVO {
    private Long userId;
    private String username;
    private String nickname;
}

// Converter
@Mapper(componentModel = "spring")
public interface UserConverter {
    UserVO toVO(SysUser user);
}

// MapStruct 自动生成的实现
@Component
public class UserConverterImpl implements UserConverter {
    @Override
    public UserVO toVO(SysUser user) {
        if (user == null) {
            return null;
        }
        return UserVO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .build();
    }
}
```

---

## 使用示例

### Service 层注入使用
```java
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserConverter userConverter;  // 注入

    public UserVO getUserById(Long userId) {
        SysUser user = userMapper.selectById(userId);
        return userConverter.toVO(user);
    }
}
```

### 非 Spring 环境使用
```java
public class UserService {
    private static final UserConverter CONVERTER = UserConverter.INSTANCE;

    public UserVO getUserById(Long userId) {
        SysUser user = userMapper.selectById(userId);
        return CONVERTER.toVO(user);
    }
}
```

---

## 常见问题

### 1. MapStruct 生成的代码在哪里？
- 在 `target/generated-sources/annotations` 目录
- 编译时自动生成，IDEA 会自动识别

### 2. 找不到生成的实现类？
- 确认 `mapstruct-processor` 已配置
- 执行 `mvn clean compile` 重新编译
- IDEA 中启用注解处理器

### 3. 字段名不匹配如何处理？
- 使用 `@Mapping` 注解指定 source 和 target
- 或配置命名策略（不推荐）

### 4. 如何处理复杂转换逻辑？
- 在 Converter 接口中定义 default 方法
- 使用 `@Named` + `qualifiedByName` 引用
- 或使用 `expression` 直接写 Java 代码

# 环境变量配置指南

## 概述

EZ-ADMIN 使用环境变量管理敏感配置和不同环境的差异化配置，提高安全性和灵活性。

---

## 环境变量列表

### 数据库配置

| 变量名 | 说明 | 默认值 | 必填 |
|--------|------|--------|------|
| `DB_HOST` | 数据库主机地址 | localhost | 否 |
| `DB_PORT` | 数据库端口 | 5432 | 否 |
| `DB_NAME` | 数据库名称 | ez-admin | 否 |
| `DB_USERNAME` | 数据库用户名 | postgres | 否 |
| `DB_PASSWORD` | 数据库密码 | postgres | 否 |

### Redis 配置

| 变量名 | 说明 | 默认值 | 必填 |
|--------|------|--------|------|
| `REDIS_HOST` | Redis 主机地址 | localhost | 否 |
| `REDIS_PORT` | Redis 端口 | 6379 | 否 |
| `REDIS_PASSWORD` | Redis 密码 | 空 | 否 |
| `REDIS_DB` | Redis 数据库索引 | 0 | 否 |

### Sa-Token 配置

| 变量名 | 说明 | 默认值 | 必填 | 生产环境 |
|--------|------|--------|------|---------|
| `SA_TOKEN_JWT_SECRET` | JWT 密钥 | 无 | **是** | **必须设置** |
| `SA_TOKEN_TIMEOUT` | Token 有效期（秒） | 2592000 (30天) | 否 | 可选 |
| `SA_TOKEN_ACTIVE_TIMEOUT` | Token 临时有效期（秒） | 1800 (30分钟) | 否 | 可选 |

---

## 配置方式

### 方式 1：IDE 运行配置（推荐开发环境）

#### IntelliJ IDEA
1. 打开 Run/Debug Configurations
2. 选择 Spring Boot 配置
3. 在 Environment variables 中添加：
   ```
   DB_HOST=localhost;DB_PORT=5432;DB_USERNAME=postgres;DB_PASSWORD=postgres;REDIS_HOST=localhost;SA_TOKEN_JWT_SECRET=your-secret-key
   ```

#### VS Code
在 `launch.json` 中配置：
```json
{
  "env": {
    "DB_HOST": "localhost",
    "DB_PORT": "5432",
    "DB_USERNAME": "postgres",
    "DB_PASSWORD": "postgres",
    "REDIS_HOST": "localhost",
    "SA_TOKEN_JWT_SECRET": "your-secret-key"
  }
}
```

### 方式 2：系统环境变量

#### Windows (PowerShell)
```powershell
$env:SA_TOKEN_JWT_SECRET="your-secret-key"
$env:DB_HOST="localhost"
```

#### Linux/Mac (Bash)
```bash
export SA_TOKEN_JWT_SECRET="your-secret-key"
export DB_HOST="localhost"
```

#### Windows (CMD)
```cmd
set SA_TOKEN_JWT_SECRET=your-secret-key
set DB_HOST=localhost
```

### 方式 3：.env 文件（需要 Spring Boot dotenv 依赖）

1. 复制 `.env.example` 为 `.env`
2. 填入实际值
3. 启动应用

**注意**：Spring Boot 默认不支持 `.env` 文件，需添加依赖：
```xml
<dependency>
    <groupId>me.paulschwarz</groupId>
    <artifactId>spring-dotenv</artifactId>
    <version>4.0.0</version>
</dependency>
```

### 方式 4：Docker Compose（推荐生产环境）

在 `docker-compose.yml` 中配置：
```yaml
services:
  app:
    environment:
      - DB_HOST=postgres
      - DB_PORT=5432
      - DB_USERNAME=postgres
      - DB_PASSWORD=your-password
      - REDIS_HOST=redis
      - REDIS_PORT=6379
      - SA_TOKEN_JWT_SECRET=your-production-secret-key
```

### 方式 5：Kubernetes ConfigMap/Secret

**ConfigMap**（非敏感配置）：
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: ez-admin-config
data:
  DB_HOST: "postgres"
  DB_PORT: "5432"
  REDIS_HOST: "redis"
```

**Secret**（敏感配置）：
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: ez-admin-secret
type: Opaque
stringData:
  DB_PASSWORD: "your-password"
  SA_TOKEN_JWT_SECRET: "your-production-secret-key"
```

---

## 安全建议

### 1. JWT 密钥生成

**生产环境必须使用强随机密钥**，生成方式：

```bash
# OpenSSL
openssl rand -base64 32

# Python
python -c "import secrets; print(secrets.token_urlsafe(32))"

# Node.js
node -e "console.log(require('crypto').randomBytes(32).toString('base64'))"
```

### 2. 密码要求

- **JWT 密钥**：至少 32 字节，推荐使用 Base64 编码的随机字符串
- **数据库密码**：至少 12 字符，包含大小写字母、数字、特殊字符
- **Redis 密码**：至少 16 字符

### 3. 密钥轮换

建议定期轮换 JWT 密钥（建议 3-6 个月）：

```bash
# 生成新密钥
export NEW_JWT_SECRET=$(openssl rand -base64 32)

# 更新环境变量
export SA_TOKEN_JWT_SECRET=$NEW_JWT_SECRET
```

---

## 环境差异化配置

### 开发环境（application-dev.yml）

```yaml
sa-token:
  is-log: true  # 开启日志，便于调试
```

### 生产环境（application-prod.yml）

```yaml
sa-token:
  is-log: false  # 关闭日志，避免日志过多
```

---

## 故障排查

### 问题 1：JWT 密钥未设置

**错误信息**：
```
java.lang.IllegalStateException: jwtSecretKey 不能为空
```

**解决方案**：
```bash
export SA_TOKEN_JWT_SECRET="your-secret-key"
```

### 问题 2：环境变量未生效

**检查方法**：
```java
@Value("${SA_TOKEN_JWT_SECRET:}")
private String jwtSecret;

@PostConstruct
public void init() {
    System.out.println("JWT Secret: " + jwtSecret);
}
```

### 问题 3：不同环境配置冲突

**原则**：
- 环境变量优先级最高
- application-{profile}.yml 次之
- application.yml 最低

---

## 最佳实践

1. **永远不要在代码中硬编码敏感信息**
2. **使用环境变量管理所有敏感配置**
3. **生产环境 JWT 密钥必须使用强随机字符串**
4. **定期轮换密钥和密码**
5. **使用 .gitignore 排除 .env 文件**
6. **提供 .env.example 作为模板**
7. **在不同环境使用不同的 JWT 密钥**

---

## 配置优先级

Spring Boot 配置加载优先级（从高到低）：

1. 命令行参数
2. 系统环境变量
3. `application-{profile}.yml`
4. `application.yml`
5. 默认值

**示例**：
```bash
# 命令行参数（优先级最高）
java -jar app.jar --SA_TOKEN_JWT_SECRET=from-cli

# 环境变量（优先级第二）
export SA_TOKEN_JWT_SECRET=from-env

# 配置文件（优先级第三）
# application.yml: SA_TOKEN_JWT_SECRET: ${SA_TOKEN_JWT_SECRET:default-value}
```

---

## 参考链接

- [Spring Boot Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [Sa-Token 配置文档](https://sa-token.cc/doc.html#/use/jwt-integrate)
- [12-Factor App Config](https://12factor.net/config)

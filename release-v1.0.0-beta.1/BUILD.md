# 打包发布说明

## 当前状态

✅ pom.xml 版本号已更新为 1.0.0-beta.1
✅ 发布目录结构已创建
✅ 文档和脚本已准备完成

## 剩余步骤

### 1. 打包应用（在 IDE 中执行）

在 IDEA 或其他 IDE 中：

**方式一：Maven 面板**
```
右侧 Maven 面板 -> Lifecycle -> clean -> package
（双击执行，跳过测试可在配置中设置 -DskipTests）
```

**方式二：运行命令**
```
打开 IDEA 底部的 Terminal，执行：
mvn clean package -DskipTests
```

**方式三：快捷键**
```
Ctrl + Shift + F10（运行配置）
需要先创建 Maven Run Configuration
```

打包成功后，在 `target/` 目录下会生成：
```
ez-admin-springboot4-1.0.0-beta.1.jar
```

### 2. 复制 JAR 文件到发布目录

```bash
# Windows
copy target\ez-admin-springboot4-1.0.0-beta.1.jar release-v1.0.0-beta.1\

# Linux/Mac
cp target/ez-admin-springboot4-1.0.0-beta.1.jar release-v1.0.0-beta.1/
```

### 3. 创建 Git 标签

```bash
# 添加所有文件
git add .

# 提交版本更新
git commit -m "chore: 发布 v1.0.0-beta.1 测试版"

# 创建标签
git tag -a v1.0.0-beta.1 -m "Release v1.0.0-beta.1 - 测试版"

# 推送到远程
git push origin dev
git push origin v1.0.0-beta.1
```

### 4. 压缩发布包

```bash
# Windows（使用 7-Zip 或 WinRAR）
压缩 release-v1.0.0-beta.1 目录为 zip 文件

# Linux/Mac
tar -czf ez-admin-springboot4-v1.0.0-beta.1.tar.gz release-v1.0.0-beta.1/
zip -r ez-admin-springboot4-v1.0.0-beta.1.zip release-v1.0.0-beta.1/
```

### 5. GitHub Release

1. 访问 GitHub 仓库
2. 点击 "Releases" -> "Create a new release"
3. 选择标签：v1.0.0-beta.1
4. 填写标题：Release v1.0.0-beta.1
5. 粘贴 CHANGELOG.md 内容到描述
6. 上传压缩包（ez-admin-springboot4-v1.0.0-beta.1.zip）
7. 点击 "Publish release"

---

## 测试验证

### 本地测试

```bash
# 进入发布目录
cd release-v1.0.0-beta.1

# 启动应用
./start.sh        # Linux/Mac
start.bat          # Windows

# 或直接运行
java -jar ez-admin-springboot4-1.0.0-beta.1.jar
```

### 功能测试

1. 检查启动日志，确认无错误
2. 访问 http://localhost:8080/actuator/health
3. 访问 http://localhost:8080/swagger-ui/index.html
4. 执行系统初始化
5. 测试登录功能

---

## 发布包内容

```
release-v1.0.0-beta.1/
├── ez-admin-springboot4-1.0.0-beta.1.jar    ⚠️ 需要手动复制
├── start.bat                                ✅ Windows 启动脚本
├── start.sh                                 ✅ Linux/Mac 启动脚本
├── README.md                                ✅ 项目说明
├── CHANGELOG.md                             ✅ 更新日志
├── config/                                  ✅ 配置文件示例
│   ├── application-example.yml
│   └── application-dev-example.yml
├── sql/                                     ✅ 数据库脚本
│   ├── ez-admin-schema-v2-postgres.sql
│   └── menu-init-data-v2-postgres.sql
└── docs/                                    ✅ 文档
    └── INSTALL.md
```

---

## 快捷操作清单

- [ ] 在 IDE 中执行 `mvn clean package -DskipTests`
- [ ] 复制 `target/ez-admin-springboot4-1.0.0-beta.1.jar` 到 `release-v1.0.0-beta.1/`
- [ ] 本地启动测试：`java -jar release-v1.0.0-beta.1/ez-admin-springboot4-1.0.0-beta.1.jar`
- [ ] 验证功能正常
- [ ] 提交代码：`git add . && git commit -m "chore: 发布 v1.0.0-beta.1"`
- [ ] 创建标签：`git tag -a v1.0.0-beta.1 -m "Release v1.0.0-beta.1"`
- [ ] 推送：`git push && git push origin v1.0.0-beta.1`
- [ ] 创建 GitHub Release

---

## 注意事项

1. **打包前检查**：确保所有代码已提交
2. **测试验证**：打包后务必测试 JAR 能否正常运行
3. **版本号管理**：本次为测试版，正式版应去掉 `-beta` 后缀
4. **配置安全**：提醒使用者修改默认密码和 JWT 密钥

---

**打包完成后，可以在本地或测试环境先验证一遍功能，确认无误后再发布！**

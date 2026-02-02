# 开源项目发版流程规范

## 发版前准备

### 1. 代码质量检查

- [ ] 确保所有功能开发完成并测试通过
- [ ] 代码审查完成，无明显问题
- [ ] 更新 `CHANGELOG.md`，记录本版本的变更内容
- [ ] 确保分支是最新的 `dev` 或 `main` 分支

### 2. 版本号规划

遵循语义化版本规范（Semantic Versioning）：`MAJOR.MINOR.PATCH`

- **MAJOR（主版本）**：不兼容的 API 修改
- **MINOR（次版本）**：向下兼容的功能性新增
- **PATCH（修订版）**：向下兼容的问题修正

示例：
- `1.0.0` → `1.0.1`（Bug 修复）
- `1.0.0` → `1.1.0`（新增功能）
- `1.0.0` → `2.0.0`（破坏性变更）

---

## 发版流程步骤

### 步骤 1：准备发布分支

```bash
# 确保在 dev 分支且代码最新
git checkout dev
git pull origin dev

# 创建发布分支（可选，建议用于正式版本）
git checkout -b release-v1.0.0
```

### 步骤 2：更新版本信息

#### 2.1 更新 `pom.xml` 版本号

```xml
<version>1.0.0</version>
```

#### 2.2 更新 `README.md` 和相关文档

- 更新版本号
- 更新功能特性说明
- 检查文档链接是否有效

#### 2.3 更新 `CHANGELOG.md`

```markdown
## [1.0.0] - 2026-01-28

### Added
- 新增功能 A
- 新增功能 B

### Changed
- 优化模块 C

### Fixed
- 修复问题 D
- 修复问题 E

### Removed
- 移除废弃功能 F
```

### 步骤 3：提交版本更新

```bash
git add pom.xml README.md CHANGELOG.md
git commit -m "chore: 准备发布 v1.0.0"

# 如果在发布分支
git push origin release-v1.0.0
```

### 步骤 4：创建 Pull Request（可选）

如果在发布分支上工作：

1. 在 GitHub/GitLab 创建 PR：`release-v1.0.0` → `main`
2. 通过代码审查
3. 合并到 `main` 分支

### 步骤 5：合并到主分支并打标签

```bash
# 切换到 main 分支
git checkout main
git pull origin main

# 合并发布分支（或直接从 dev 合并）
git merge release-v1.0.0
# 或
# git merge dev

# 创建带注释的标签
git tag -a v1.0.0 -m "Release v1.0.0

主要变更：
- 新增用户管理功能
- 优化权限控制
- 修复若干 Bug"

# 推送标签到远程
git push origin v1.0.0
```

### 步骤 6：构建发布包

```bash
# 清理并打包（跳过测试）
mvn clean package -DskipTests

# 或包含测试
mvn clean package
```

### 步骤 7：在 GitHub 创建 Release

1. 进入 GitHub 仓库页面
2. 点击 **Releases** → **Create a new release**
3. 填写信息：
   - **Tag version**: 选择 `v1.0.0`
   - **Release title**: `v1.0.0 - 正式版`
   - **Description**: 复制 `CHANGELOG.md` 中的对应内容
4. 上传附件（可选）：
   - `target/ez-admin-springboot4-1.0.0.jar`
   - SQL 初始化脚本
   - 配置文件示例
5. 勾选 **Set as the latest release**（如果是稳定版）
6. 点击 **Publish release**

### 步骤 8：部署到中央仓库（可选）

如果要发布到 Maven Central：

```bash
# 部署到中央仓库
mvn clean deploy -P release
```

### 步骤 9：发布后清理

```bash
# 将主分支合并回 dev，确保 dev 也包含发布变更
git checkout dev
git merge main
git push origin dev

# 删除本地发布分支（可选）
git branch -d release-v1.0.0
```

### 步骤 10：准备下一版本

更新 `pom.xml` 到下一个开发版本：

```xml
<version>1.0.1-SNAPSHOT</version>
```

提交：

```bash
git add pom.xml
git commit -m "chore: 进入 1.0.1-SNAPSHOT 开发版本"
git push origin dev
```

---

## 版本类型说明

### 稳定版（Stable Release）

- 格式：`v1.0.0`
- 用于生产环境
- 完整测试，稳定可靠

### 测试版（Beta/RC）

- 格式：`v1.0.0-beta.1` 或 `v1.0.0-rc.1`
- 功能基本完成，需要社区测试
- 标记为 **Pre-release**

### 快照版（Snapshot）

- 格式：`1.0.1-SNAPSHOT`
- 开发中的版本
- 不创建 Git Tag
- 不创建 GitHub Release

---

## 紧急修复流程（Hotfix）

如果正式版本发现严重 Bug：

```bash
# 从 main 创建 hotfix 分支
git checkout main
git checkout -b hotfix-v1.0.1

# 修复 Bug
# ...

# 提交并合并
git checkout main
git merge hotfix-v1.0.1
git tag -a v1.0.1 -m "Hotfix v1.0.1 - 修复 XXX 问题"
git push origin v1.0.1

# 合并回 dev
git checkout dev
git merge hotfix-v1.0.1
git push origin dev

# 删除 hotfix 分支
git branch -d hotfix-v1.0.1
```

---

## 发布检查清单

- [ ] 版本号符合语义化规范
- [ ] `CHANGELOG.md` 更新完整
- [ ] 所有测试通过
- [ ] 文档更新（README、API 文档等）
- [ ] 标签注释清晰
- [ ] GitHub Release 描述完整
- [ ] 附件上传正确（JAR、SQL 脚本等）
- [ ] dev 分支已同步最新变更
- [ ] 准备好下一版本开发

---

## 常见问题

### Q1: 是否需要为每次提交都打标签？

**A**: 不需要。只对正式发布版本打标签（v1.0.0、v1.1.0 等），日常开发提交不打标签。

### Q2: 标签是否必须推送到远程？

**A**: 是的。使用 `git push origin <tag-name>` 推送标签，否则 GitHub 无法识别 Release。

### Q3: 如何删除错误的标签？

```bash
# 删除本地标签
git tag -d v1.0.0

# 删除远程标签
git push origin :refs/tags/v1.0.0

# 重新创建正确标签
git tag -a v1.0.0 -m "Release v1.0.0"
git push origin v1.0.0
```

### Q4: GitHub Release 和 Git Tag 的区别？

- **Git Tag**: 版本标记，Git 中的指针
- **GitHub Release**: 基于 Tag 创建的发布页面，包含说明、附件、下载链接等

---

## 参考资源

- [语义化版本规范](https://semver.org/lang/zh-CN/)
- [GitHub Releases 文档](https://docs.github.com/en/repositories/releasing-projects-on-github)
- [Git 标签管理](https://git-scm.com/book/zh/v2/Git-%E5%9F%BA%E7%A1%80-%E6%89%93%E6%A0%87%E7%AD%BE)

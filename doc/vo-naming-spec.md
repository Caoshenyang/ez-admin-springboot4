# VO 命名规范

## 原则

根据 VO 的用途明确命名，职责单一，避免歧义。

## 命名格式

| VO 类型 | 命名格式 | 用途 | 示例 |
|---------|---------|------|------|
| **详情 VO** | `xxxDetailVO` | 单个资源详情（getById） | `UserDetailVO`, `RoleDetailVO`, `MenuDetailVO` |
| **列表 VO** | `xxxListVO` | 分页列表、简单列表（getPage） | `UserListVO`, `RoleListVO`, `MenuListVO` |
| **树形 VO** | `xxxTreeVO` | 树形结构（getTree） | `DeptTreeVO`, `MenuTreeVO` |
| **其他 VO** | `xxx{用途}VO` | 特定场景（如登录响应） | `LoginVO`, `InstallVO` |

## 字段差异

- **ListVO**：只包含列表展示需要的字段（如 ID、名称、状态等）
- **DetailVO**：包含完整字段（所有业务字段）
- **TreeVO**：包含完整字段 + children 列表（继承 `TreeNode<TreeVO>`）

## 正确示例

```java
// ✅ 正确 - 职责清晰
UserListVO    - 分页列表（userId, username, nickname, status）
UserDetailVO  - 详情（完整字段，包括 email, phone, createTime 等）
UserTreeVO    - 树形（完整字段 + children，继承 TreeNode）
```

## 错误示例

```java
// ❌ 错误 - 命名模糊
UserVO        - 无法区分是列表还是详情
UserInfoVO    - 与 DetailVO 重复且不直观
```

## MapStruct 转换示例

```java
@Mapper(componentModel = "spring")
public interface UserConverter {
    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

    // Entity → DetailVO（详情）
    UserDetailVO toDetailVO(SysUser user);

    // Entity → ListVO（列表）
    UserListVO toListVO(SysUser user);

    // Entity → TreeVO（树形）
    UserTreeVO toTreeVO(SysUser user);

    // 批量转换
    List<UserDetailVO> toDetailVOList(List<SysUser> users);
    List<UserListVO> toListVOList(List<SysUser> users);
    List<UserTreeVO> toTreeVOList(List<SysUser> users);
}
```

## 使用场景对照表

| 接口 | 返回类型 | 说明 |
|------|---------|------|
| `GET /user/{id}` | `UserDetailVO` | 单个用户详情 |
| `POST /user/page` | `PageVO<UserListVO>` | 分页列表 |
| `GET /user/tree` | `List<UserTreeVO>` | 树形结构 |
| `POST /auth/login` | `LoginVO` | 特定场景（登录响应） |

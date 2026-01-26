# 树形结构处理指南

## 概述

项目中提供了完善的树形结构处理工具类，位于 `com.ez.admin.common.tree` 包下：

- **TreeNode**: 树形节点抽象基类，提供通用的树形节点操作
- **TreeBuilder**: 树形结构构建器，支持将平铺列表转换为树形结构

## 核心特性

### TreeNode 特性

- **自动管理子节点**: 继承即可获得 `children` 字段
- **节点操作**: `addChild`、`removeChild`、`clearChildren` 等
- **树形遍历**: `forEach`、`forEachChildren` 支持深度优先遍历
- **节点查找**: `find` 方法支持条件查找
- **统计功能**: `countDescendants`、`getDepth` 等

### TreeBuilder 特性

- **高性能**: 使用 LinkedHashMap 缓存，时间复杂度 O(n)
- **灵活配置**: 支持自定义根节点 ID、排序、过滤等
- **链式调用**: 支持流式编程，代码可读性强
- **丰富工具**: `flatten`、`filterTree`、`forEach`、`find`、`count`、`getDepth` 等

## 使用示例

### 1. 定义树形 VO

```java
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "MenuVO", description = "菜单响应（树形结构）")
public class MenuVO extends TreeNode {

    @Schema(description = "菜单ID")
    private Long menuId;

    @Schema(description = "父级菜单ID")
    private Long parentId;

    @Schema(description = "菜单排序")
    private Integer menuSort;

    // ... 其他字段

    /**
     * 获取节点 ID（必须实现）
     */
    @Override
    public Long getNodeId() {
        return this.menuId;
    }

    /**
     * 获取父节点 ID（必须实现）
     */
    @Override
    public Long getParentId() {
        return this.parentId;
    }

    /**
     * 获取排序字段（可选，用于排序）
     */
    @Override
    public Integer getSort() {
        return this.menuSort;
    }

    /**
     * 类型安全的子节点 getter（推荐）
     */
    @SuppressWarnings("unchecked")
    public List<MenuVO> getChildren() {
        return (List<MenuVO>) super.getChildren();
    }
}
```

### 2. 简单构建树形结构

```java
@Service
@RequiredArgsConstructor
public class MenuService {

    private final SysMenuMapper menuMapper;
    private final MenuConverter menuConverter;

    /**
     * 获取菜单树（简单用法）
     */
    public List<MenuVO> getMenuTree() {
        // 1. 从数据库查询菜单列表
        List<SysMenu> menuList = menuMapper.selectList(null);

        // 2. 转换为 VO
        List<MenuVO> menuVOList = menuConverter.toVOList(menuList);

        // 3. 构建树形结构（自动识别根节点、保持顺序）
        List<MenuVO> menuTree = TreeBuilder.build(menuVOList);

        return menuTree;
    }
}
```

### 3. 高级构建（排序、过滤）

```java
/**
 * 获取菜单树（高级用法）
 */
public List<MenuVO> getMenuTree() {
    List<SysMenu> menuList = menuMapper.selectList(null);
    List<MenuVO> menuVOList = menuConverter.toVOList(menuList);

    // 构建树形结构：指定根节点 ID、排序、过滤
    List<MenuVO> menuTree = TreeBuilder.of(menuVOList)
        .rootNodeId(0L)           // 指定根节点 ID 为 0
        .enableSort()             // 按 getSort() 方法排序（即 menuSort）
        .filter(menu ->           // 只保留正常状态的菜单
            menu.getStatus() == 1
        )
        .build();

    return menuTree;
}
```

### 4. 自定义排序规则

```java
/**
 * 获取菜单树（自定义排序）
 */
public List<MenuVO> getMenuTree() {
    List<SysMenu> menuList = menuMapper.selectList(null);
    List<MenuVO> menuVOList = menuConverter.toVOList(menuList);

    // 使用自定义排序规则
    List<MenuVO> menuTree = TreeBuilder.of(menuVOList)
        .sortBy(menu -> {
            // 先按菜单类型排序，再按排序字段排序
            return menu.getMenuType() * 1000 + menu.getMenuSort();
        })
        .build();

    return menuTree;
}
```

### 5. 树形结构操作

```java
/**
 * 树形结构操作示例
 */
public void treeOperations() {
    List<MenuVO> menuTree = getMenuTree();

    // 1. 统计节点总数
    int totalCount = TreeBuilder.count(menuTree);
    log.info("菜单总数: {}", totalCount);

    // 2. 获取树的深度
    int depth = TreeBuilder.getDepth(menuTree);
    log.info("菜单树深度: {}", depth);

    // 3. 查找指定节点
    MenuVO targetMenu = TreeBuilder.find(menuTree, menu ->
        menu.getMenuId().equals(100L)
    );

    // 4. 遍历所有节点
    TreeBuilder.forEach(menuTree, menu -> {
        log.info("菜单: {} - {}", menu.getMenuId(), menu.getMenuName());
    });

    // 5. 将树形结构平铺为列表
    List<MenuVO> flatList = TreeBuilder.flatten(menuTree);

    // 6. 过滤树形结构
    List<MenuVO> filteredTree = TreeBuilder.filterTree(menuTree, menu ->
        menu.getMenuType() != 3  // 过滤掉按钮类型
    );
}
```

### 6. 单个节点的树形操作

```java
/**
 * 单个节点的树形操作示例
 */
public void nodeOperations() {
    MenuVO menu = getMenuById(100L);

    // 1. 判断是否为根节点
    boolean isRoot = menu.isRoot();

    // 2. 判断是否有子节点
    boolean hasChildren = menu.hasChildren();

    // 3. 统计子孙节点数量
    int descendantCount = menu.countDescendants();

    // 4. 获取子树的深度
    int depth = menu.getDepth();

    // 5. 遍历当前节点及所有子孙节点
    menu.forEach(node -> {
        log.info("节点: {} - {}", node.getMenuId(), node.getMenuName());
    });

    // 6. 查找符合条件的子孙节点
    MenuVO found = menu.find(node ->
        node.getMenuName().contains("用户")
    );

    // 7. 添加子节点
    MenuVO newMenu = new MenuVO();
    // ... 设置属性
    menu.addChild(newMenu);
}
```

## TreeBuilder 方法参考

### 静态方法

| 方法 | 说明 | 示例 |
|------|------|------|
| `build(dataList)` | 简单构建树形结构 | `TreeBuilder.build(menuList)` |
| `build(dataList, rootNodeId)` | 指定根节点 ID | `TreeBuilder.build(menuList, 0L)` |
| `build(dataList, filter)` | 带过滤 | `TreeBuilder.build(menuList, m -> m.getStatus() == 1)` |
| `build(dataList, sorter)` | 带排序 | `TreeBuilder.build(menuList, MenuVO::getMenuSort)` |
| `of(dataList)` | 创建流式构建器 | `TreeBuilder.of(menuList).enableSort().build()` |
| `flatten(tree)` | 树形结构平铺为列表 | `TreeBuilder.flatten(menuTree)` |
| `filterTree(tree, filter)` | 过滤树形结构 | `TreeBuilder.filterTree(menuTree, m -> m.getStatus() == 1)` |
| `forEach(tree, action)` | 遍历树形结构 | `TreeBuilder.forEach(menuTree, m -> log.info(m.getName()))` |
| `find(tree, filter)` | 查找节点 | `TreeBuilder.find(menuTree, m -> m.getId() == 1)` |
| `count(tree)` | 统计节点总数 | `TreeBuilder.count(menuTree)` |
| `getDepth(tree)` | 获取树的深度 | `TreeBuilder.getDepth(menuTree)` |

### TreeBuilderConfig 方法

| 方法 | 说明 | 返回值 |
|------|------|--------|
| `rootNodeId(Long id)` | 设置根节点 ID | this |
| `enableSort()` | 启用排序（使用 getSort()） | this |
| `sortBy(Function<T, Integer>)` | 设置自定义排序规则 | this |
| `filter(Predicate<T>)` | 设置过滤条件 | this |
| `build()` | 构建树形结构 | List<T> |

## TreeNode 方法参考

| 方法 | 说明 | 返回值 |
|------|------|--------|
| `getNodeId()` | 获取节点 ID（抽象方法，必须实现） | Long |
| `getParentId()` | 获取父节点 ID（抽象方法，必须实现） | Long |
| `getSort()` | 获取排序字段（可选，默认返回 null） | Integer |
| `isRoot()` | 判断是否为根节点 | boolean |
| `hasChildren()` | 判断是否有子节点 | boolean |
| `addChild(TreeNode)` | 添加子节点 | void |
| `removeChild(int)` | 移除指定索引的子节点 | TreeNode |
| `clearChildren()` | 清空子节点 | void |
| `forEach(Consumer)` | 遍历当前节点及所有子孙节点 | void |
| `forEachChildren(Consumer)` | 仅遍历子节点 | void |
| `find(Predicate)` | 查找符合条件的节点 | TreeNode |
| `countDescendants()` | 统计子孙节点数量 | int |
| `getDepth()` | 获取子树深度 | int |

## 实际应用场景

### 场景 1：菜单管理

```java
@RestController
@RequestMapping("/api/system/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    /**
     * 获取菜单树
     */
    @GetMapping("/tree")
    public R<List<MenuVO>> getMenuTree() {
        List<MenuVO> menuTree = menuService.getUserMenuTree();
        return R.success(menuTree);
    }
}
```

### 场景 2：部门管理

```java
@RestController
@RequestMapping("/api/system/dept")
@RequiredArgsConstructor
public class DeptController {

    private final DeptService deptService;

    /**
     * 获取部门树
     */
    @GetMapping("/tree")
    public R<List<DeptVO>> getDeptTree() {
        List<DeptVO> deptTree = deptService.getDeptTree();
        return R.success(deptTree);
    }
}
```

### 场景 3：权限树（过滤按钮类型）

```java
/**
 * 获取用户权限树（不包括按钮）
 */
public List<MenuVO> getUserPermissionTree() {
    List<SysMenu> menuList = menuMapper.selectList(null);
    List<MenuVO> menuVOList = menuConverter.toVOList(menuList);

    // 只保留目录和菜单类型，不过滤按钮
    List<MenuVO> permissionTree = TreeBuilder.of(menuVOList)
        .enableSort()
        .filter(menu -> menu.getMenuType() != 3)
        .build();

    return permissionTree;
}
```

### 场景 4：根据用户权限动态过滤

```java
/**
 * 获取用户有权限的菜单树
 */
public List<MenuVO> getUserMenuTree(Long userId) {
    // 1. 查询用户所有菜单
    List<SysMenu> menuList = menuMapper.selectMenusByUserId(userId);
    List<MenuVO> menuVOList = menuConverter.toVOList(menuList);

    // 2. 构建树形结构
    List<MenuVO> menuTree = TreeBuilder.of(menuVOList)
        .enableSort()
        .filter(menu -> menu.getStatus() == 1)  // 只保留正常状态的菜单
        .build();

    return menuTree;
}
```

## 注意事项

### 1. VO 类必须实现的方法

```java
// ✅ 必须实现
@Override
public Long getNodeId() {
    return this.menuId;  // 返回当前节点的 ID
}

@Override
public Long getParentId() {
    return this.parentId;  // 返回父节点的 ID
}

// ⭐ 可选实现（用于排序）
@Override
public Integer getSort() {
    return this.menuSort;  // 返回排序字段
}
```

### 2. 类型安全的子节点获取

```java
/**
 * 推荐做法：提供类型安全的 getter
 */
@SuppressWarnings("unchecked")
public List<MenuVO> getChildren() {
    return (List<MenuVO>) super.getChildren();
}
```

### 3. MapStruct 转换器配置

如果使用 MapStruct 转换，TreeBuilder 会自动处理子节点的转换：

```java
@Mapper(componentModel = "spring")
public interface MenuConverter {
    MenuConverter INSTANCE = Mappers.getMapper(MenuConverter.class);

    // Entity -> VO
    MenuVO toVO(SysMenu entity);

    // 批量转换
    List<MenuVO> toVOList(List<SysMenu> entities);
}
```

### 4. 根节点识别规则

TreeBuilder 默认的根节点识别规则（优先级从高到低）：

1. 如果指定了 `rootNodeId`，则 `parentId == rootNodeId` 的节点为根节点
2. 如果 `parentId == null`，则为根节点
3. 如果 `parentId == 0`，则为根节点
4. 如果在列表中找不到对应的父节点，则为根节点

### 5. 性能考虑

- **时间复杂度**: O(n)，n 为节点数量
- **空间复杂度**: O(n)，使用 LinkedHashMap 缓存节点
- **适用规模**: 适用于数千个节点的树形结构，如果节点数超过 10000，建议考虑分页或懒加载

## 常见问题

### Q1: 如何处理循环引用？

TreeBuilder 不会检测循环引用，如果数据中存在循环引用（A 的父节点是 B，B 的父节点是 A），会导致无限递归。请在数据源层面确保数据正确性。

### Q2: 如何处理父节点被过滤的情况？

如果父节点被过滤，子节点会自动成为根节点：

```java
// 过滤掉 status != 1 的节点
List<MenuVO> filteredTree = TreeBuilder.of(menuList)
    .filter(menu -> menu.getStatus() == 1)
    .build();

// 如果某个节点的父节点被过滤，该节点会成为新的根节点
```

### Q3: 如何实现懒加载？

对于大型树形结构，建议实现懒加载：

```java
/**
 * 懒加载：只加载第一层子节点
 */
public List<MenuVO> getMenuTreeLazy() {
    List<SysMenu> menuList = menuMapper.selectList(
        new LambdaQueryWrapper<SysMenu>()
            .eq(SysMenu::getParentId, 0)  // 只查询根节点
    );

    List<MenuVO> menuVOList = menuConverter.toVOList(menuList);
    return menuVOList;  // 返回根节点列表，前端点击时再加载子节点
}
```

### Q4: 如何同时使用多个过滤条件？

使用 `&&` 连接多个条件：

```java
List<MenuVO> filteredTree = TreeBuilder.of(menuList)
    .filter(menu ->
        menu.getStatus() == 1 &&           // 正常状态
        menu.getMenuType() != 3 &&         // 不是按钮
        menu.getMenuName().contains("用户") // 名称包含"用户"
    )
    .build();
```

## 总结

树形结构工具类提供了强大且易用的树形结构处理能力，适用于菜单、部门、分类等各种树形场景。通过继承 TreeNode 和使用 TreeBuilder，可以轻松实现树形结构的构建、遍历、查找、过滤等操作。

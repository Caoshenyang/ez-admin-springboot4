# 查询元数据系统使用指南

## 概述

查询元数据系统允许前端动态获取资源的查询字段信息，包括字段名、类型、支持的操作符、示例值等，从而实现动态筛选组件的自动渲染。

## 架构设计

```
┌─────────────────────────────────────────────────────┐
│                   前端应用                          │
│                                                     │
│  1. GET /api/metadata/user                         │
│     ↓                                              │
│  2. 接收元数据（FieldMetadata 列表）               │
│     ↓                                              │
│  3. 动态渲染筛选组件                               │
│     - String → 文本输入框                          │
│     - Integer → 数字输入框                         │
│     - dictType → 下拉选择                          │
│     ↓                                              │
│  4. 用户输入筛选条件                               │
│     ↓                                              │
│  5. POST /api/user/page                            │
│     { pageNum, pageSize, conditions }              │
└─────────────────────────────────────────────────────┘
```

---

## 1. 前端使用

### 1.1 获取元数据

```javascript
// 获取用户查询元数据
const response = await fetch('/api/metadata/user');
const { data } = await response.json();

console.log(data);
// {
//   "resource": "user",
//   "description": "用户",
//   "fields": [
//     {
//       "field": "username",
//       "type": "String",
//       "description": "用户账号",
//       "operators": ["EQ", "LIKE", "IN"],
//       "required": false,
//       "example": "admin"
//     },
//     {
//       "field": "status",
//       "type": "Integer",
//       "description": "用户状态",
//       "operators": ["EQ", "IN"],
//       "required": false,
//       "example": 1,
//       "dictType": "sys_user_status"
//     }
//   ]
// }
```

### 1.2 动态渲染筛选组件

```javascript
/**
 * 根据字段类型渲染不同的输入组件
 */
function renderFilterField(field) {
  const container = document.createElement('div');

  // 字段描述
  const label = document.createElement('label');
  label.textContent = field.description;
  container.appendChild(label);

  // 根据类型渲染组件
  if (field.dictType) {
    // 有字典类型，渲染下拉选择
    const select = renderDictSelect(field.dictType);
    container.appendChild(select);
  } else if (field.type === 'String') {
    // 字符串类型，渲染文本输入框
    const input = document.createElement('input');
    input.type = 'text';
    input.placeholder = field.example || `请输入${field.description}`;
    container.appendChild(input);
  } else if (field.type === 'Integer' || field.type === 'Long') {
    // 数值类型，渲染数字输入框
    const input = document.createElement('input');
    input.type = 'number';
    input.placeholder = field.example || `请输入${field.description}`;
    container.appendChild(input);
  }

  // 渲染操作符选择器
  const operatorSelect = renderOperatorSelect(field.operators);
  container.appendChild(operatorSelect);

  return container;
}

/**
 * 渲染操作符下拉选择
 */
function renderOperatorSelect(operators) {
  const select = document.createElement('select');

  const operatorLabels = {
    'EQ': '等于',
    'NE': '不等于',
    'GT': '大于',
    'LT': '小于',
    'LIKE': '模糊匹配',
    'IN': '包含于',
  };

  operators.forEach(op => {
    const option = document.createElement('option');
    option.value = op;
    option.textContent = operatorLabels[op] || op;
    select.appendChild(option);
  });

  return select;
}

/**
 * 渲染字典下拉选择
 */
async function renderDictSelect(dictType) {
  const select = document.createElement('select');

  // 从字典服务获取字典选项
  const response = await fetch(`/api/dict/${dictType}`);
  const { data } = await response.json();

  data.forEach(item => {
    const option = document.createElement('option');
    option.value = item.value;
    option.textContent = item.label;
    select.appendChild(option);
  });

  return select;
}

/**
 * 初始化筛选组件
 */
async function initFilterComponent() {
  const response = await fetch('/api/metadata/user');
  const { data } = await response.json();

  const container = document.getElementById('filter-container');
  data.fields.forEach(field => {
    const fieldComponent = renderFilterField(field);
    container.appendChild(fieldComponent);
  });
}
```

### 1.3 构建查询请求

```javascript
/**
 * 收集筛选条件并构建请求
 */
function buildQueryRequest() {
  const conditions = [];

  // 收集所有筛选字段的值
  document.querySelectorAll('.filter-field').forEach(field => {
    const fieldName = field.dataset.field;
    const operator = field.querySelector('.operator-select').value;
    const value = field.querySelector('.value-input').value;

    if (value) {
      conditions.push({
        field: fieldName,
        operator: operator,
        value: value
      });
    }
  });

  return {
    pageNum: 1,
    pageSize: 10,
    conditions: conditions
  };
}

/**
 * 发起查询请求
 */
async function searchUsers() {
  const query = buildQueryRequest();

  const response = await fetch('/api/user/page', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(query)
  });

  const { data } = await response.json();
  console.log('查询结果：', data);
}
```

---

## 2. 后端扩展

### 2.1 添加新的查询元数据枚举

假设需要为角色模块添加查询元数据：

```java
package com.ez.admin.common.metadata;

import com.ez.admin.common.enums.Operator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色查询元数据枚举
 */
@Getter
@AllArgsConstructor
public enum RoleQueryMetadata implements QueryMetadata {

    ROLE_NAME(
        "roleName",
        "String",
        "角色名称",
        Arrays.asList(Operator.EQ, Operator.LIKE)
    ) {
        @Override
        public Object getExample() {
            return "管理员";
        }
    },

    ROLE_CODE(
        "roleCode",
        "String",
        "角色编码",
        Arrays.asList(Operator.EQ, Operator.IN)
    ) {
        @Override
        public Object getExample() {
            return "ROLE_ADMIN";
        }
    },

    STATUS(
        "status",
        "Integer",
        "状态",
        Arrays.asList(Operator.EQ, Operator.IN)
    ) {
        @Override
        public String getDictType() {
            return "sys_role_status";
        }

        @Override
        public Object getExample() {
            return 1;
        }
    };

    private final String field;
    private final String type;
    private final String description;
    private final List<Operator> operators;

    public String getDictType() {
        return null;
    }

    public Object getExample() {
        return null;
    }

    public Boolean isRequired() {
        return false;
    }

    @Override
    public String getDescription() {
        return "角色";
    }

    @Override
    public List<FieldMetadata> getFields() {
        return Arrays.stream(values())
                .map(this::toFieldMetadata)
                .collect(Collectors.toList());
    }

    private FieldMetadata toFieldMetadata(RoleQueryMetadata metadata) {
        return FieldMetadata.builder()
                .field(metadata.getField())
                .type(metadata.getType())
                .description(metadata.getDescription())
                .operators(metadata.getOperators().stream()
                        .map(Operator::getCode)
                        .collect(Collectors.toList()))
                .required(metadata.isRequired())
                .example(metadata.getExample())
                .dictType(metadata.getDictType())
                .build();
    }
}
```

### 2.2 在 MetadataController 中注册路由

```java
private QueryMetadata getMetadataByResource(String resource) {
    return switch (resource) {
        case "user", "users" -> UserQueryMetadata.USERNAME;
        case "role", "roles" -> RoleQueryMetadata.ROLE_NAME;  // 新增
        // case "menu", "menus" -> MenuQueryMetadata.MENU_NAME;
        default -> null;
    };
}
```

---

## 3. 元数据字段说明

### 3.1 FieldMetadata 属性

| 属性 | 类型 | 必填 | 说明 |
|------|------|------|------|
| field | String | 是 | 数据库字段名（驼峰命名） |
| type | String | 是 | Java 类型（String, Integer, LocalDateTime 等） |
| description | String | 是 | 中文描述，用于前端显示 |
| operators | List<String> | 是 | 支持的操作符列表（EQ, LIKE, IN 等） |
| required | Boolean | 否 | 是否必填（默认 false） |
| example | Object | 否 | 示例值，用于前端 placeholder |
| dictType | String | 否 | 字典类型，用于渲染下拉选择 |

### 3.2 支持的操作符

| 操作符 | 代码 | 说明 | 适用类型 |
|--------|------|------|----------|
| 等于 | EQ | 精确匹配 | 所有类型 |
| 不等于 | NE | 不等于 | 所有类型 |
| 大于 | GT | 大于 | 数值、日期 |
| 小于 | LT | 小于 | 数值、日期 |
| 大于等于 | GE | 大于等于 | 数值、日期 |
| 小于等于 | LE | 小于等于 | 数值、日期 |
| 模糊匹配 | LIKE | 包含（%value%） | String |
| 不包含 | NOT_LIKE | 不包含 | String |
| 包含于 | IN | 在列表中 | 所有类型 |
| 不包含于 | NOT_IN | 不在列表中 | 所有类型 |

---

## 4. 完整示例

### 4.1 前端 Vue 3 示例

```vue
<template>
  <div class="filter-container">
    <div v-for="field in fields" :key="field.field" class="filter-field">
      <label>{{ field.description }}</label>

      <!-- 有字典类型，使用下拉选择 -->
      <select v-if="field.dictType" v-model="conditions[field.field].operator">
        <option value="EQ">等于</option>
        <option value="IN">包含于</option>
      </select>

      <!-- 无字典类型，根据类型渲染输入框 -->
      <input
        v-else
        :type="field.type === 'Integer' ? 'number' : 'text'"
        v-model="conditions[field.field].value"
        :placeholder="field.example"
      />

      <!-- 操作符选择 -->
      <select v-model="conditions[field.field].operator">
        <option
          v-for="op in field.operators"
          :key="op"
          :value="op"
        >
          {{ operatorLabels[op] }}
        </option>
      </select>
    </div>

    <button @click="search">查询</button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

const fields = ref([]);
const conditions = ref({});

const operatorLabels = {
  'EQ': '等于',
  'LIKE': '模糊匹配',
  'IN': '包含于',
  'GT': '大于',
  'LT': '小于',
};

onMounted(async () => {
  const response = await fetch('/api/metadata/user');
  const { data } = await response.json();
  fields.value = data.fields;

  // 初始化条件对象
  data.fields.forEach(field => {
    conditions.value[field.field] = {
      operator: field.operators[0],
      value: null
    };
  });
});

const search = async () => {
  const query = {
    pageNum: 1,
    pageSize: 10,
    conditions: Object.entries(conditions.value)
      .filter(([_, cond]) => cond.value)
      .map(([field, cond]) => ({
        field,
        operator: cond.operator,
        value: cond.value
      }))
  };

  const response = await fetch('/api/user/page', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(query)
  });

  const { data } = await response.json();
  console.log('查询结果：', data);
};
</script>
```

---

## 5. 注意事项

1. **字段安全性**：只有枚举中定义的字段才能被查询，防止前端传入任意字段导致 SQL 注入
2. **操作符验证**：前端只能使用枚举中定义的操作符，后端会再次验证
3. **类型转换**：前端传入的值是字符串，后端会根据字段类型自动转换
4. **字典联动**：如果字段有 `dictType`，前端需要先调用字典接口获取选项
5. **性能优化**：元数据可以缓存，避免每次查询都请求元数据接口

---

## 6. 总结

通过查询元数据系统：

✅ **前端**：动态渲染筛选组件，无需硬编码字段
✅ **后端**：集中管理查询字段，类型安全
✅ **维护性**：新增字段只需修改枚举，前后端自动同步
✅ **可扩展**：轻松添加新的资源和字段

package com.ezadmin.model.vo;

import com.ezadmin.common.response.tree.TreeNode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 部门树节点
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DeptTreeVO extends TreeNode {

    private Long deptId;
    private Long parentId;
    private String deptName;
    private Integer deptSort;
    private Integer status;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @JsonIgnore
    @Override
    public Long getNodeId() {
        return deptId;
    }

    @Override
    public Long getParentId() {
        return parentId;
    }
}

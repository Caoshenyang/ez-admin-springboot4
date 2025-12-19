package com.ezadmin.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门更新
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DeptUpdateDTO extends DeptCreateDTO {

    @Schema(description = "部门ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long deptId;
}

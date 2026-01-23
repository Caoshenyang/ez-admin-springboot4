package com.ez.admin.dto.user.req;

import com.ez.admin.dto.common.Filter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户查询条件对象
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Data
@Schema(name = "UserQueryReq", description = "用户查询条件")
public class UserQueryReq {

    @Schema(description = "用户名（模糊查询）", example = "admin")
    private String username;

    @Schema(description = "昵称（模糊查询）", example = "管理员")
    private String nickname;

    @Schema(description = "手机号（精确查询）", example = "13800000000")
    private String phoneNumber;

    @Schema(description = "状态（0=禁用，1=正常）", example = "1")
    private Integer status;

    @Schema(description = "部门ID", example = "1")
    private Long deptId;

    @Schema(description = "开始时间（创建时间范围查询）", example = "2024-01-01")
    private String startTime;

    @Schema(description = "结束时间（创建时间范围查询）", example = "2024-12-31")
    private String endTime;

    @Schema(description = "动态查询条件列表（前端控制）", example = "[{\"field\":\"USERNAME\",\"operator\":\"LIKE\",\"value\":\"admin\"}]")
    private List<Filter> filters;
}

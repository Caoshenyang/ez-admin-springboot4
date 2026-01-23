package com.ez.admin.api.install;

import com.ez.admin.common.model.R;
import com.ez.admin.dto.install.req.InstallReq;
import com.ez.admin.dto.install.vo.InstallStatusVO;
import com.ez.admin.dto.install.vo.InstallVO;
import com.ez.admin.service.install.InstallService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 系统安装控制器
 * <p>
 * 提供系统首次初始化相关接口，用于创建默认管理员账号
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Slf4j
@RestController
@RequestMapping("/install")
@RequiredArgsConstructor
@Tag(name = "系统安装接口", description = "系统首次初始化相关接口")
public class InstallController {

    private final InstallService installService;

    /**
     * 检查系统初始化状态
     * <p>
     * 通过查询数据库中的用户记录判断系统是否已初始化
     * </p>
     *
     * @return 初始化状态响应
     */
    @GetMapping("/status")
    @Operation(summary = "检查初始化状态", description = "查询系统是否已完成初始化")
    public R<InstallStatusVO> getInstallStatus() {
        log.info("查询系统初始化状态");
        InstallStatusVO response = installService.checkInstallStatus();
        return R.success(response);
    }

    /**
     * 初始化系统
     * <p>
     * 创建默认管理员账号和超级管理员角色
     * 只有在系统未初始化时（数据库无用户记录）才能执行
     * </p>
     *
     * @param request 初始化请求
     * @return 初始化响应
     */
    @PostMapping
    @Operation(summary = "初始化系统", description = "创建默认管理员账号和超级管理员角色")
    public R<InstallVO> install(@Valid @RequestBody InstallReq request) {
        log.info("开始初始化系统，管理员用户名：{}", request.getUsername());
        InstallVO response = installService.installSystem(request);
        return R.success("系统初始化成功", response);
    }
}

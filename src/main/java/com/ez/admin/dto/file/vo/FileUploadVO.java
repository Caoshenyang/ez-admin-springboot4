package com.ez.admin.dto.file.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 文件上传响应对象
 *
 * @author ez-admin
 * @since 2026-01-27
 */
@Getter
@Builder
@Schema(name = "FileUploadVO", description = "文件上传响应")
public class FileUploadVO {

    @Schema(description = "文件访问URL", example = "http://example.com/uploads/avatar.jpg")
    private String url;

    @Schema(description = "文件名", example = "avatar.jpg")
    private String filename;

    @Schema(description = "文件大小（字节）", example = "102400")
    private Long size;
}

package com.ez.admin.service.file;

import com.ez.admin.common.exception.EzBusinessException;
import com.ez.admin.common.exception.ErrorCode;
import com.ez.admin.dto.file.vo.FileUploadVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件服务
 * <p>
 * 提供文件上传、下载等功能
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-27
 */
@Slf4j
@Service
public class FileService {

    /**
     * 允许上传的图片类型
     */
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/png", "image/gif", "image/webp");

    /**
     * 文件大小限制（5MB）
     */
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @Value("${app.upload.path:/tmp/ez-admin/uploads}")
    private String uploadPath;

    @Value("${app.upload.domain:http://localhost:8080}")
    private String uploadDomain;

    /**
     * 上传图片
     *
     * @param file 文件
     * @return 文件上传响应
     */
    public FileUploadVO uploadImage(MultipartFile file) {
        // 1. 检查文件是否为空
        if (file == null || file.isEmpty()) {
            throw new EzBusinessException(ErrorCode.FILE_UPLOAD_ERROR, "文件不能为空");
        }

        // 2. 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new EzBusinessException(ErrorCode.FILE_TYPE_NOT_ALLOWED, "只支持 JPG、PNG、GIF、WEBP 格式的图片");
        }

        // 3. 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new EzBusinessException(ErrorCode.FILE_SIZE_EXCEEDED, "文件大小不能超过 5MB");
        }

        try {
            // 4. 生成文件路径（按日期分组）
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ?
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String newFilename = UUID.randomUUID() + extension;

            // 5. 创建目录
            Path targetPath = Paths.get(uploadPath, datePath);
            Files.createDirectories(targetPath);

            // 6. 保存文件
            Path filePath = targetPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath);

            // 7. 构建访问 URL
            String url = uploadDomain + "/uploads/" + datePath + "/" + newFilename;

            log.info("文件上传成功，文件名：{}，大小：{}，路径：{}", newFilename, file.getSize(), filePath);

            return FileUploadVO.builder()
                    .url(url)
                    .filename(newFilename)
                    .size(file.getSize())
                    .build();

        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new EzBusinessException(ErrorCode.FILE_UPLOAD_ERROR, "文件上传失败：" + e.getMessage());
        }
    }
}

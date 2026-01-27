package com.ez.admin.common.data.mapstruct;

import com.ez.admin.dto.log.vo.OperationLogListVO;
import com.ez.admin.modules.system.entity.SysOperationLog;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 操作日志对象转换器
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Mapper(componentModel = "spring")
public interface OperationLogConverter {

    /**
     * 获取转换器实例（非 Spring 环境使用）
     */
    OperationLogConverter INSTANCE = Mappers.getMapper(OperationLogConverter.class);

    /**
     * SysOperationLog 转 OperationLogListVO
     *
     * @param log 操作日志实体
     * @return 操作日志列表 VO
     */
    OperationLogListVO toListVO(SysOperationLog log);

    /**
     * SysOperationLog 列表转 OperationLogListVO 列表
     *
     * @param logs 操作日志实体列表
     * @return 操作日志列表 VO
     */
    List<OperationLogListVO> toListVOList(List<SysOperationLog> logs);
}

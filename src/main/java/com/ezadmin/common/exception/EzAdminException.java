package com.ezadmin.common.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author 曹申阳
 * @since 2025-12-15 10:49:35
 */
@Setter
@Getter
public class EzAdminException extends RuntimeException{

    /**
     * 异常枚举
     */
    private final ExceptionEnum exceptionEnum;

    /**
     * 构造方法
     * @param exceptionEnum 异常枚举
     */
    public EzAdminException(ExceptionEnum exceptionEnum) {
        this.exceptionEnum = exceptionEnum;
    }


    /**
     * 获取异常枚举的错误代码
     * @return 错误代码
     */
    public Integer getCode() {
        return exceptionEnum.getCode();
    }

     /**
     * 获取异常枚举的错误消息
     * @return 错误消息
     */
    @Override
    public String getMessage() {
        return exceptionEnum.getMessage();
    }

}

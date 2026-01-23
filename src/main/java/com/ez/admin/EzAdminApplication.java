package com.ez.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * EZ Admin 应用程序入口类
 *
 * @author ez-admin
 */
@MapperScan("com.ez.admin.modules.**.mapper")
@SpringBootApplication(scanBasePackages = "com.ez.admin")
public class EzAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(EzAdminApplication.class, args);
    }

}

package com.ezadmin;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EzAdminSpringboot4Application {

    public static void main(String[] args) {
        // 在 Spring Boot 启动前加载 .env 文件到系统属性
        // 这样 application.yaml 中的 ${DB_HOST:localhost} 等占位符就可以读取到 .env 文件中的值
        Dotenv dotenv = Dotenv.configure()
                .directory("src/main/resources") // 指定 .env 文件所在目录
                .ignoreIfMissing() // 如果文件不存在，不抛出异常
                .load();
        
        // 将 .env 文件中的变量设置到系统属性中，供 Spring Boot 使用
        dotenv.entries().forEach(entry -> {
            String key = entry.getKey();
            String value = entry.getValue();
            // 只有当系统属性中不存在该变量时，才设置（环境变量优先级更高）
            if (System.getProperty(key) == null && System.getenv(key) == null) {
                System.setProperty(key, value);
            }
        });
        
        SpringApplication.run(EzAdminSpringboot4Application.class, args);
    }

}

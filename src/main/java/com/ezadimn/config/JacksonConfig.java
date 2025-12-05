package com.ezadimn.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * Jackson 配置类
 * 用于自定义 LocalDateTime 的序列化和反序列化格式
 * 1. 自定义 LocalDateTime 格式为 "yyyy-MM-dd HH:mm:ss"
 * 2. 注册 LocalDateTime 序列化器和反序列化器
 * 3. 将 Long 类型序列化为字符串，避免前端精度丢失
 */
@Configuration
public class JacksonConfig {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Bean
    public JacksonJsonHttpMessageConverter mappingJacksonHttpMessageConverter() {
        SimpleModule simpleModule = new SimpleModule();

        // 配置 LocalDateTime 的序列化（对象转JSON）和反序列化（JSON转对象）
        simpleModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(FORMATTER));
        simpleModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(FORMATTER));

        // 可选：将 Long 类型序列化为字符串，避免前端精度丢失
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);

        // 创建 JsonMapper 并注册配置好的模块
        JsonMapper jsonMapper = JsonMapper.builder().addModule(simpleModule).build();
        return new JacksonJsonHttpMessageConverter(jsonMapper);
    }

}

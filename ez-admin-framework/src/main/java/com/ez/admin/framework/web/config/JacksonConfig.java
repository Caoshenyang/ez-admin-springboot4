package com.ez.admin.framework.web.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * Jackson 配置类
 * <p>
 * Spring Boot 4.x 兼容配置
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-19
 */
@Configuration
public class JacksonConfig {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 配置 ObjectMapper
     * <p>
     * Spring Boot 4.x 使用 Jackson2ObjectMapperBuilderCustomizer 自定义配置
     * </p>
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return new Jackson2ObjectMapperBuilderCustomizer() {
            @Override
            public void customize(Jackson2ObjectMapperBuilder builder) {
                // 配置时区
                builder.timeZone(TimeZone.getDefault());

                // 配置日期时间序列化
                JavaTimeModule javaTimeModule = new JavaTimeModule();
                javaTimeModule.addSerializer(LocalDateTime.class,
                        new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
                javaTimeModule.addDeserializer(LocalDateTime.class,
                        new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
                builder.modules(javaTimeModule);

                // 序列化配置
                builder.featuresToDisable(
                        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,  // 禁用时间戳
                        SerializationFeature.FAIL_ON_EMPTY_BEANS,         // 允许空对象
                        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES // 忽略未知属性
                );

                // 其他配置
                builder.featuresToEnable(
                        DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY  // 允许单值转数组
                );
            }
        };
    }

    /**
     * 配置 ObjectMapper Bean（可选）
     * <p>
     * 如果需要在其他地方直接注入使用
     * </p>
     */
    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        return builder.createXmlMapper(false).build();
    }
}

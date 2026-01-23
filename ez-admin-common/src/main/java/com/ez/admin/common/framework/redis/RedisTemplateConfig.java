package com.ez.admin.common.framework.redis;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置类
 * <p>
 * 配置 RedisTemplate 的序列化方式，使用 JSON 序列化存储对象
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-19
 */
@Configuration
public class RedisTemplateConfig {
    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 使用 StringRedisSerializer 来序列化和反序列化 redis 的 key 值
        template.setKeySerializer(new StringRedisSerializer());

        // 使用GenericJackson2JsonRedisSerializer来序列化和反序列化redis的value值
        GenericJacksonJsonRedisSerializer serializer = getGenericJackson2JsonRedisSerializer();
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }

    private static GenericJacksonJsonRedisSerializer getGenericJackson2JsonRedisSerializer() {
        return GenericJacksonJsonRedisSerializer.builder()
                .enableUnsafeDefaultTyping()
                .build();
    }
}

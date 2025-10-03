package com.kaidev99.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        // Cấu hình serializer cho key của Redis (ví dụ: "cart:kaidev99")
        template.setKeySerializer(new StringRedisSerializer());
        // Cấu hình serializer cho key của Hash (ví dụ: "productId:1")
        template.setHashKeySerializer(new StringRedisSerializer());
        // Cấu hình serializer cho value của Hash (ví dụ: "2")
        template.setHashValueSerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
}
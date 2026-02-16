package com.example.blog.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RedisConfig {
    /*@Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.password}")
    private String redisPassword;*/

    @Value("${expire.defaultTime}")
    private Long expiredDefault;

    @Value("${expire.postTime}")
    private Long expiredPost;

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        // RedisCacheManagerBuilderCustomizer is fnctional interface -> reteurn lambda function
        return builder -> {

            // Default Setting
            RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofSeconds(expiredDefault))
                    .disableCachingNullValues()
                    .serializeKeysWith(
                            RedisSerializationContext.SerializationPair
                                    .fromSerializer(new StringRedisSerializer()))
                    .serializeValuesWith(
                            RedisSerializationContext.SerializationPair
                                    .fromSerializer(new GenericJackson2JsonRedisSerializer()));

            // Separate TTLs for each cache
            Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

            cacheConfigurations.put("postCache",
                    defaultConfig.entryTtl(Duration.ofSeconds(expiredPost)));

            cacheConfigurations.put("userCache",
                    defaultConfig.entryTtl(Duration.ofSeconds(expiredDefault)));

            builder
                    .cacheDefaults(defaultConfig)
                    .withInitialCacheConfigurations(cacheConfigurations);
        };
    }

    /*@Bean
    public ObjectMapper objectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModules(new JavaTimeModule(), new Jdk8Module());
        return objectMapper;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisHost);
        redisStandaloneConfiguration.setPort(redisPort);
        redisStandaloneConfiguration.setPassword(redisPassword);
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);
        return lettuceConnectionFactory;
    }

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory, ObjectMapper objectMapper){
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                                    .disableCachingNullValues()
                                    .entryTtl(Duration.ofSeconds(expiredTime))
                                    .serializeKeysWith(
                                        RedisSerializationContext.SerializationPair
                                                            .fromSerializer(new StringRedisSerializer()))
                                    .serializeValuesWith(
                                            RedisSerializationContext.SerializationPair
                                                            .fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)));
        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(redisConnectionFactory).cacheDefaults(config).build();
    }*/
}

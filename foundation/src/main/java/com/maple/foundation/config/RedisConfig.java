package com.maple.foundation.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.Duration;

/**
 * @author qifeng.b.chen
 * @version 1.0
 * @date 2022/3/21-14:01
 * @since 1.8
 */
@Configuration
@EnableTransactionManagement
public class RedisConfig {
  @Bean
  RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {

    // key的序列化方式
    RedisSerializer<String> stringSerializer = new StringRedisSerializer();
    // value的序列化方式
    Jackson2JsonRedisSerializer<Object> jsonSerializer =
        new Jackson2JsonRedisSerializer(Object.class);
    // 解决查询缓存转换异常的问题
    ObjectMapper om = new ObjectMapper();
    om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    jsonSerializer.setObjectMapper(om);
    // 缓存默认配置
    RedisCacheConfiguration config =
        RedisCacheConfiguration.defaultCacheConfig()
            // 1天过期
            .entryTtl(Duration.ofDays(1))
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(stringSerializer))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
            // 不缓存 null
            .disableCachingNullValues();

    return RedisCacheManager.builder(redisConnectionFactory).cacheDefaults(config).build();
  }

  @Bean
  public RedisTemplate<Object, Object> redisTemplate(
      RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<Object, Object> template = new RedisTemplate();
    // key 使用 string序列化器
    template.setKeySerializer(new StringRedisSerializer());
    // value 使用JSON序列化
    template.setValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));
    template.setConnectionFactory(redisConnectionFactory);
    return template;
  }

  @Bean
  public RedisTemplate<Object, Object> transactionRedisTemplate(
      RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<Object, Object> template = new RedisTemplate();
    template.setConnectionFactory(redisConnectionFactory);
    // key 使用 string序列化器
    template.setKeySerializer(new StringRedisSerializer());
    // value 使用JSON序列化
    template.setValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));
    // TODO 需要验证一下，开启spring声明式事务注解
    template.setEnableTransactionSupport(true);
    return template;
  }
}

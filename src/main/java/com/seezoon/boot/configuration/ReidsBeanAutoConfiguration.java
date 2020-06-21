package com.seezoon.boot.configuration;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.nio.charset.StandardCharsets;

/**
 * @author hdf
 */
@Configuration
public class ReidsBeanAutoConfiguration {

    @Value("${spring.application.name}")
    private String namespace;

    /**
     * redisTemplate 序列化使用的jdkSerializeable, 存储二进制字节码, 所以自定义序列化类
     *
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<String, ?> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, ?> redisTemplate = new RedisTemplate<>();
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new NameSpaceStringRedisSerializer(namespace));
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        //使用fast json 序列化
        GenericFastJsonRedisSerializer redisSerializer = new GenericFastJsonRedisSerializer();
        //GenericJackson2JsonRedisSerializer redisSerializer = new GenericJackson2JsonRedisSerializer();
        redisTemplate.setDefaultSerializer(redisSerializer);
        redisTemplate.setValueSerializer(redisSerializer);
        redisTemplate.setHashValueSerializer(redisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * 当普通string kv  namespace 需要覆盖reids starter 自带实现
     *
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(redisConnectionFactory);
        stringRedisTemplate.setKeySerializer(new NameSpaceStringRedisSerializer(namespace));
        stringRedisTemplate.afterPropertiesSet();
        return stringRedisTemplate;
    }

    /**
     * 主key 统一支持namespace
     *
     * @author hdf
     */
    public static class NameSpaceStringRedisSerializer extends StringRedisSerializer {

        private String namespace;

        public NameSpaceStringRedisSerializer(String namespace) {
            this.namespace = namespace;
        }

        @Override
        public byte[] serialize(String string) {
            return (string == null ? null : (namespace + ":" + string).getBytes(StandardCharsets.UTF_8));
        }
    }
}

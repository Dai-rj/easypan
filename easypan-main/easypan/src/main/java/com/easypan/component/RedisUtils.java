package com.easypan.component;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis工具类，提供通用的Redis操作方法
 * @param <V> 存储在Redis中的值的类型
 */
@Component("redisUtils")
public class RedisUtils<V> {
    // 注入RedisTemplate模板，用于操作Redis
    @Resource
    private RedisTemplate<String, V> redisTemplate;

    // 日志对象，用于记录操作Redis时的错误信息
    private static final Logger logger = LoggerFactory.getLogger(RedisUtils.class);

    /**
     * 根据键获取Redis中的值
     * @param key Redis中的键
     * @return 如果键存在，则返回对应的值；否则返回null
     */
    public V get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 将键值对设置到Redis中
     *
     * @param key   Redis中的键
     * @param value Redis中的值
     */
    public void set(String key, V value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            logger.error("设置redisKey:{},value:{}失败", key, value);
        }
    }

    /**
     * 将键值对设置到Redis中，并指定过期时间
     * 如果时间小于等于0，则调用set方法，不设置过期时间
     *
     * @param key   Redis中的键
     * @param value Redis中的值
     * @param time  过期时间（秒）
     */
    public void sites(String key, V value, long time) {
        try {
            // 根据时间参数决定是否设置过期时间
            if (time > 0) {
                // 设置键值对，并指定过期时间
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                // 不设置过期时间，调用set方法
                set(key, value);
            }
        } catch (Exception e) {
//            // 记录设置键值对失败的日志
//            logger.error("设置redisKey:{},value:{}失败", key, value);
        }
    }
}

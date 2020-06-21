package com.seezoon.boot.common.utils;

import com.seezoon.boot.common.Constants;
import com.seezoon.boot.context.utils.MdcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

public class TaskLockSupport {

    private Logger logger = LoggerFactory.getLogger(TaskLockSupport.class);

    private String taskKey;

    private RedisTemplate<String, String> redisTemplate;
    private StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

    public TaskLockSupport(String taskKey, RedisTemplate<String, String> redisTemplate) {
        Assert.hasLength(taskKey, "taskKey is empty");
        Assert.notNull(redisTemplate, "redisTemplate is null");
        this.taskKey = taskKey;
        this.redisTemplate = redisTemplate;
    }

    public void start(TaskHandler taskHandler) {
        MdcUtil.push();
       /*  无法保证原子性
       try {
            if (!redisTemplate.opsForValue().setIfAbsent(taskKey, Constants.YES)) {
                return ;
            }
        } finally {
            redisTemplate.expire(taskKey, 60, TimeUnit.SECONDS);
        }*/

        Boolean executed = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.set(stringRedisSerializer.serialize(taskKey), stringRedisSerializer.serialize(Constants.YES), Expiration.from(60, TimeUnit.SECONDS), RedisStringCommands.SetOption.SET_IF_ABSENT);
            }
        });
        if (!executed) {
            return;
        }

        try {
            taskHandler.invoker();
        } catch (Exception e) {
            logger.error("task error taskKey:{}", taskKey, e);
        } finally {
            redisTemplate.delete(taskKey);
        }
        MdcUtil.clear();
    }

    public static interface TaskHandler {
        public void taskRule();

        public void invoker();
    }
}

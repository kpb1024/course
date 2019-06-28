package cn.kangpb.course.utils.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisUtil.class);

    @Resource
    private RedisTemplate<Serializable, Serializable> redisTemplate;

    public static final String KEY_PREFIX_VALUE = "kangpb:course:value:";

    public boolean cacheValue(String k, Serializable v, long time) {
        String key = KEY_PREFIX_VALUE + k;
        try {
            ValueOperations<Serializable, Serializable> valueOperations = redisTemplate.opsForValue();
            valueOperations.set(key, v);
            if (time > 0) redisTemplate.expire(key, time, TimeUnit.SECONDS);
            return true;
        } catch (Throwable t) {
            LOGGER.error("缓存[{}]失败, value[{}]",key,v);
            LOGGER.error(t.toString());
        }
        return false;
    }

    public  boolean cacheValue(String k, Serializable v, long time,TimeUnit unit) {
        String key = KEY_PREFIX_VALUE + k;
        try {
            ValueOperations<Serializable, Serializable> valueOps =  redisTemplate.opsForValue();
            valueOps.set(key, v);
            if (time > 0) redisTemplate.expire(key, time, unit);
            return true;
        } catch (Throwable t) {
            LOGGER.error("缓存[{}]失败, value[{}]",key,v,t);
        }
        return false;
    }

    public boolean cacheValue(String k, Serializable v) { return cacheValue(k,v,-1);}

    public boolean containsValueKey(String k) {
        String key = KEY_PREFIX_VALUE + k;
        try {
            return redisTemplate.hasKey(key);
        } catch (Throwable t) {
            LOGGER.error("判断缓存存在失败key[" + key + ", error[" + t + "]");
        }
        return false;
    }

    public Serializable getValue(String k) {
        try {
            ValueOperations<Serializable, Serializable> valueOperations = redisTemplate.opsForValue();
            return valueOperations.get(KEY_PREFIX_VALUE + k);
        } catch (Throwable t) {
            LOGGER.error("获取缓存失败key[" + KEY_PREFIX_VALUE + k + ", error[" + t + "]");
        }
        return null;
    }

    public boolean removeValue(String k) {
        String key = KEY_PREFIX_VALUE + k;
        try {
            redisTemplate.delete(key);
            return true;
        } catch (Throwable t){
            LOGGER.error("获取缓存失败key[" + key + ", error[" + t + "]");
        }
        return false;
    }

}

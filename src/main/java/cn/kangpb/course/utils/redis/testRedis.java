package cn.kangpb.course.utils.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class testRedis {
    public static void main(String[] args) {
        RedisClient redisClient = RedisClient.create("redis://13148899@129.204.15.237:6379/0");
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> syncCommands = connection.sync();
        System.out.println("Connection Built");
        syncCommands.set("0622TEST", "Hello, Redis!");
        connection.close();
        redisClient.shutdown();
    }
}

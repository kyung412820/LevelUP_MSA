package com.sparta.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisConfigChecker implements CommandLineRunner {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void run(String... args) throws Exception {
        String notifyConfig = redisTemplate.execute((RedisConnection connection) -> {
            Object value = connection.getConfig("notify-keyspace-events").get("notify-keyspace-events");
            return value != null ? value.toString() : "Not Set";
        });

        System.out.println("Redis notify-keyspace-events 설정 값: " + notifyConfig);
    }
}

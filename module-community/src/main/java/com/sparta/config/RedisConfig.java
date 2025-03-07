package com.sparta.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.sparta.levelup_backend.config.RedisExpireListener;
import com.sparta.levelup_backend.domain.bill.service.BillStatusSubscriber;
import com.sparta.levelup_backend.domain.chat.service.RedisSubscriber;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.password}")
    private String redisPassword;

    // 포트 설정 (설정 안해도 Spring 기본 제공 포트인 127.0.0.1 6379로 접속)
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration(host);
        redisConfiguration.setPort(port);
        redisConfiguration.setPassword(redisPassword);
        return new LettuceConnectionFactory(redisConfiguration);
    }

    @Bean // 기본 Serializer - 필요할 시 추가 해야함
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    /**
     * Redis 메시지 구성 설정
     * @param redisConnectionFactory Redis 연결
     * @param redisSubscriber 수신된 메시지 처리 서비스
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
        RedisConnectionFactory redisConnectionFactory,
        RedisSubscriber redisSubscriber
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(redisSubscriber, new PatternTopic("chatroom:*"));
        return container;
    }

    // Redis 리스너 설정 추가 (주문 생성 자동삭제)
    @Bean
    public RedisMessageListenerContainer expireEventListener(
        RedisConnectionFactory redisConnectionFactory,
        RedisExpireListener redisExpireListener
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(redisExpireListener, new PatternTopic("__keyevent@*__:expired"));
        return container;
    }

    // Redis 리스너 설정 추가 (결제 관련 알림)
    @Bean
    public RedisMessageListenerContainer redisMessageListener(
        RedisConnectionFactory connectionFactory,
        MessageListenerAdapter messageListenerAdapter,
        ChannelTopic BillStatusChannel) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(messageListenerAdapter, BillStatusChannel);
        return container;
    }

    @Bean
    public MessageListenerAdapter messageListenerAdapter(BillStatusSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "onMessage");
    }

    @Bean
    public ChannelTopic BillStatusChannel() {
        return new ChannelTopic("billStatusChannel");
    }
}

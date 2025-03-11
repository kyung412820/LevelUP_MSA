package com.sparta.levelup_backend.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaProducerConfig {

	private static final String BOOTSTRAP_SERVERS = "kafka1:9092,kafka2:9093,kafka3:9094";

	@Bean
	public ProducerFactory<String, String> producerFactory() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

		// Exactly-Once 전송을 위한 설정 추가
		configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true); // 중복 방지
		configProps.put(ProducerConfig.ACKS_CONFIG, "all");  // 모든 브로커에서 확인 후 응답
		configProps.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE); // 무제한 재시도 가능하도록 설정
		configProps.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "transactional-producer-1"); // 트랜잭션 ID 설정 (각 Producer 고유해야 함)

		// 최적화 설정
		configProps.put(ProducerConfig.LINGER_MS_CONFIG, 10);
		configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
		configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");

		DefaultKafkaProducerFactory<String, String> factory = new DefaultKafkaProducerFactory<>(configProps);
		factory.setTransactionIdPrefix("tx-"); // 트랜잭션 ID 접두사 추가 (멀티 프로듀서 환경 고려)

		return factory;
	}

	@Bean
	public KafkaTemplate<String, String> kafkaTemplate() {
		KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory());
		kafkaTemplate.setTransactionIdPrefix("tx-"); // 트랜잭션 사용
		return kafkaTemplate;
	}
}

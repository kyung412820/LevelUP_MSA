package com.sparta.config;

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

	private static final String BOOTSTRAP_SERVERS = "kafka1:9092,kafka2:9093,kafka3:9094"; // ✅ 여러 개의 브로커 추가

	@Bean
	public ProducerFactory<String, String> producerFactory() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

		configProps.put(ProducerConfig.ACKS_CONFIG, "all");  // 데이터 손실 방지 (모든 브로커에서 확인 후 응답)
		configProps.put(ProducerConfig.RETRIES_CONFIG, 5);   // 네트워크 장애 발생 시 재시도 횟수 증가
		configProps.put(ProducerConfig.LINGER_MS_CONFIG, 10); // 메시지를 모아서 전송 (배치 처리 최적화)
		configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384); // 배치 크기 증가 (기본값: 16KB)
		configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy"); // Snappy 압축 적용 (네트워크 대역폭 절약)

		return new DefaultKafkaProducerFactory<>(configProps);
	}

	@Bean
	public KafkaTemplate<String, String> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}
}

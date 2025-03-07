package com.sparta.Authentication.kafkaProducer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CommunityProducer {
	private final KafkaTemplate<String, Object> kafkaTemplate;

	public CommunityProducer(KafkaTemplate<String, Object> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void sendCreateCommunityEvent(Long userId, CommnunityCreateRequestDto dto) {
		Map<String, Object> payload = Map.of(
			"userId", userId,
			"dto", dto
		);
		kafkaTemplate.send("community_create", payload);
	}

	public void sendUpdateCommunityEvent(Long userId, CommunityUpdateRequestDto dto) {
		Map<String, Object> payload = Map.of(
			"userId", userId,
			"dto", dto
		);
		kafkaTemplate.send("community_update", payload);
	}

	public void sendDeleteCommunityEvent(Long userId, Long communityId) {
		Map<String, Object> payload = Map.of(
			"userId", userId,
			"communityId", communityId
		);
		kafkaTemplate.send("community_delete", payload);
	}
}

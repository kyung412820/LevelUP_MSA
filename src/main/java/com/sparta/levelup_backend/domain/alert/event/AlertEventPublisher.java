package com.sparta.levelup_backend.domain.alert.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.sparta.levelup_backend.domain.alert.entity.AlertMessageEntity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class AlertEventPublisher {
	private final ApplicationEventPublisher applicationEventPublisher;

	public void publisher(Long userId, Long logId, AlertMessageEntity alertMessageEntity) {
		applicationEventPublisher.publishEvent(new AlertEvent(userId, logId, alertMessageEntity));
	}
}

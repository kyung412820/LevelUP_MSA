package com.sparta.levelup_backend.domain.alert.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.sparta.levelup_backend.domain.alert.entity.AlertMessageEntity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class AlertEventPublisher {
	private final ApplicationEventPublisher applicationEventPublisher;
	/**
	 * 알림 및 알림 로그 기능
	 * @param  userId, logId, alertMessageEntity (유저 id, 로그id, alertMessageEntity를 받습니다.)
	 * @return void
	 */
	public void publisher(Long userId, Long logId, AlertMessageEntity alertMessageEntity) {
		applicationEventPublisher.publishEvent(new AlertEvent(userId, logId, alertMessageEntity));
	}
}

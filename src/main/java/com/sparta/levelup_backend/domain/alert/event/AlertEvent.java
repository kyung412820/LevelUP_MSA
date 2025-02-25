package com.sparta.levelup_backend.domain.alert.event;

import com.sparta.levelup_backend.domain.alert.entity.AlertMessageEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlertEvent {
	Long userId;
	Long logId;
	AlertMessageEntity alertMessageEntity;
}

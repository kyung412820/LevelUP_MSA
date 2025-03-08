package com.sparta.levelup_backend.domain.alert.controller;

import static com.sparta.levelup_backend.common.ApiResMessage.*;
import static com.sparta.levelup_backend.common.ApiResponse.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.sparta.levelup_backend.common.ApiResponse;
import com.sparta.levelup_backend.domain.alert.dto.request.UserAuthenticationRequestDto;
import com.sparta.levelup_backend.domain.alert.dto.response.AlertLogResponseDto;
import com.sparta.levelup_backend.domain.alert.service.AlertService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v3")
@RequiredArgsConstructor
public class AlertController {
	private final AlertService alertService;

	@GetMapping(value = "/alert", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public ResponseEntity<SseEmitter> userChangeAlert(HttpServletRequest request,
		@RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {

		String encodedAuth = request.getHeader("UserAuthentication");
		UserAuthenticationRequestDto authRequest = UserAuthenticationRequestDto.from(encodedAuth);
		SseEmitter alert = alertService.alertSubscribe(authRequest.getId(), lastEventId);

		return new ResponseEntity(alert, HttpStatus.OK);
	}

	@PostMapping("/alert/allRead")
	public ApiResponse<Void> readAllAlert(HttpServletRequest request) {

		String encodedAuth = request.getHeader("UserAuthentication");
		UserAuthenticationRequestDto authRequest = UserAuthenticationRequestDto.from(encodedAuth);
		alertService.readAllAlert(authRequest.getId());

		return success(HttpStatus.OK, ALERT_ALL_READ_SUCCESS);
	}

	@PostMapping("/alert/read/{alertId}")
	public ApiResponse<Void> readAlert(HttpServletRequest request,
		@PathVariable Long alertId) {
		String encodedAuth = request.getHeader("UserAuthentication");
		UserAuthenticationRequestDto authRequest = UserAuthenticationRequestDto.from(encodedAuth);
		alertService.readAlert(authRequest.getId(), alertId);

		return success(HttpStatus.OK, ALERT_READ_SUCCESS);
	}

	@GetMapping("/admin/alert/log/{userId}")
	public ApiResponse<Page<AlertLogResponseDto>> findLogByuserId(@PageableDefault Pageable pageable,
		@PathVariable Long userId) {
		Page<AlertLogResponseDto> results = alertService.findLogByuserId(userId, pageable);
		return success(HttpStatus.OK, ALERT_LOG_READ_SUCCESS, results);
	}
}

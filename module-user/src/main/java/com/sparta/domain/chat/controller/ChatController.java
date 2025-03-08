package com.sparta.domain.chat.controller;

import static com.sparta.common.ApiResMessage.MESSAGE_SAVE_SUCCESS;
import static com.sparta.common.ApiResponse.success;
import static org.springframework.http.HttpStatus.CREATED;

import com.sparta.common.ApiResponse;
import com.sparta.domain.chat.dto.ChatMessageDto;
import com.sparta.domain.chat.service.ChatService;

import com.sparta.domain.user.dto.request.UserAuthenticationRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

	private final ChatService chatService;

	/**
	 * WebSocket 메시지 처리
	 * 메시지 수신 후 -> 구독자에게 전달
	 * @param chatroomId 채팅방 ID
	 * @param dto 메시지 전송 객체 (닉네임, 메시지 포함)
	 */
	@MessageMapping("/chats/{chatroomId}") // 메시지 전송 endpoint
	public ChatMessageDto handleMessage(
		@DestinationVariable String chatroomId,
		@Payload ChatMessageDto dto,
		HttpServletRequest request
	) {
		String encodedAuth = request.getHeader("UserAuthentication");
		UserAuthenticationRequestDto userAuthenticationRequestDto = UserAuthenticationRequestDto.from(encodedAuth);
		return chatService.handleMessage(chatroomId, dto, userAuthenticationRequestDto);
	}

	/**
	 * 메시지 기록 저장 API
	 */
	@GetMapping("/v1/chats/{chatroomId}/history")
	public ApiResponse<List<ChatMessageDto>> findChatHistory(@PathVariable String chatroomId) {
		return success(CREATED, MESSAGE_SAVE_SUCCESS, chatService.findChatHistory(chatroomId));
	}
}

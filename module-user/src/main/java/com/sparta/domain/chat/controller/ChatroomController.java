package com.sparta.domain.chat.controller;



import static com.sparta.common.ApiResMessage.*;
import static com.sparta.common.ApiResponse.success;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.sparta.common.ApiResponse;
import com.sparta.domain.chat.dto.ChatroomCreateResponseDto;
import com.sparta.domain.chat.dto.ChatroomListResponseDto;
import com.sparta.domain.chat.service.ChatroomService;
import com.sparta.domain.user.dto.request.UserAuthenticationRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/chats")
@RequiredArgsConstructor
public class ChatroomController {

	private final ChatroomService chatroomService;

	/**
	 * 채팅방 생생 API
	 * @param targetUserId 참가대상 유저 ID
	 * @return
	 */
	@PostMapping
	public ApiResponse<ChatroomCreateResponseDto> createChatroom(
		HttpServletRequest request,
		@RequestParam Long targetUserId,
		@RequestParam(required = false) String title
	) {
		String encodedAuth = request.getHeader("UserAuthentication");
		UserAuthenticationRequestDto authUser = UserAuthenticationRequestDto.from(encodedAuth);
		return success(CREATED, CHATROOM_CREATE, chatroomService.createChatroom(authUser.getId(), targetUserId, title));
	}

	/**
	 * 채팅방 나가기 API
	 */
	@DeleteMapping("/{chatroomId}")
	public ApiResponse<Void> leaveChatroom(HttpServletRequest request, @PathVariable String chatroomId) {
		String encodedAuth = request.getHeader("UserAuthentication");
		UserAuthenticationRequestDto authUser = UserAuthenticationRequestDto.from(encodedAuth);
		chatroomService.leaveChatroom(authUser.getId(), chatroomId);
		return success(OK, CHATROOM_LEAVE);
	}

	/**
	 * 채팅방 목록 API
	 */
	@GetMapping
	public ApiResponse<List<ChatroomListResponseDto>> findChatrooms(HttpServletRequest request) {
		String encodedAuth = request.getHeader("UserAuthentication");
		UserAuthenticationRequestDto authUser = UserAuthenticationRequestDto.from(encodedAuth);
		return success(OK, CHATROOM_FIND ,chatroomService.findChatrooms(authUser.getId()));
	}

}

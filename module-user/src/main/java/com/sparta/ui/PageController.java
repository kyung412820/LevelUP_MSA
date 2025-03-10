package com.sparta.ui;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sparta.domain.chat.repository.ChatroomMongoRepository;
import com.sparta.domain.chat.service.ChatroomService;
import com.sparta.ui.dto.requestDto.UserAuthenticationRequestDto;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PageController {

	private final ChatroomMongoRepository chatroomMongoRepository;
	private final ChatroomService chatroomService;


	// 메인 페이지
	@GetMapping("/chat-main")
	public String chatMainPage() {
		return "chatMain";
	}

	// 채팅방 페이지
	@GetMapping("/chatroom")
	public String getChatroomPage(@RequestParam String chatroomId,
		HttpServletRequest request,
		Model model,
		RedirectAttributes redirectAttributes) {

		String encodedAuth = request.getHeader("UserAuthentication");
		UserAuthenticationRequestDto authRequest = UserAuthenticationRequestDto.from(encodedAuth);

		// 현재 사용자가 해당 채팅방의 참가자인지 확인
		if (!chatroomMongoRepository.findByUserIdAndChatroomId(authRequest.getId(), chatroomId).isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "참여하지 않은 채팅방에 접근하실 수 없습니다.");
		}

		// 채팅방 접속 시 안 읽음 수 0으로 초기화
		chatroomService.updateUnreadCountZero(chatroomId, authRequest.getId());

		model.addAttribute("chatroomId", chatroomId);
		model.addAttribute("nickname", authRequest.getNickName());
		return "chatroom";
	}

	// 채팅방 목록 페이지
	@GetMapping("/chatroomList")
	public String chatroomList() {
		return "chatroomList";
	}

}

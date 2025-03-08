package com.sparta.domain.chat.service;


import com.sparta.domain.chat.dto.ChatMessageDto;

import com.sparta.domain.user.dto.request.UserAuthenticationRequestDto;
import java.util.List;

public interface ChatService {
	ChatMessageDto handleMessage(String chatroomId, ChatMessageDto dto, UserAuthenticationRequestDto userAuthenticationRequestDto);
	List<ChatMessageDto> findChatHistory(String chatroomId);
}


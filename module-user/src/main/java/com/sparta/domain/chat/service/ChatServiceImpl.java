package com.sparta.domain.chat.service;


import com.sparta.domain.chat.document.ChatMessage;
import com.sparta.domain.chat.dto.ChatMessageDto;
import com.sparta.domain.chat.repository.ChatMongoRepository;

import com.sparta.domain.user.dto.request.UserAuthenticationRequestDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

	private final ChatMongoRepository chatMongoRepository;
	private final RedisPublisher redisPublisher;
	private final ChatroomService chatroomService;

	@Override
	public ChatMessageDto handleMessage(String chatroomId, ChatMessageDto dto, UserAuthenticationRequestDto userAuthenticationRequestDto) {


		ChatMessageDto messageDto = new ChatMessageDto(
			chatroomId,
			userAuthenticationRequestDto.getNickName(),
			dto.getMessage()
		);

		ChatMessage chatMessage = ChatMessage.builder()
			.chatroomId(chatroomId)
			.nickname(messageDto.getNickname())
			.message(messageDto.getMessage())
			.timestamp(LocalDateTime.now())
			.build();

		// 메시지 발행시 마지막 메시지, 안 읽음수 업데이트
		chatroomService.updateUnreadCountAndLastMessage(chatroomId, userAuthenticationRequestDto.getId(), dto.getMessage());

		redisPublisher.publish(getTopic(chatroomId), messageDto);
		chatMongoRepository.save(chatMessage);
		return messageDto;
	}

	@Override
	public List<ChatMessageDto> findChatHistory(String chatroomId) {
		List<ChatMessage> messages = chatMongoRepository.findByChatroomId(chatroomId);
		return messages.stream()
			.map(msg -> {
				ChatMessageDto dto = new ChatMessageDto(
					chatroomId,
					msg.getNickname(),
					msg.getMessage()
				);
				return dto;
			})
			.collect(Collectors.toList());
	}

	public ChannelTopic getTopic(String chatroomId) {
		return new ChannelTopic("chatroom:" + chatroomId);
	}
}

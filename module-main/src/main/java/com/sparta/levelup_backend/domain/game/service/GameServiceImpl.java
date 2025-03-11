package com.sparta.levelup_backend.domain.game.service;

import static com.sparta.levelup_backend.exception.common.ErrorCode.*;

import com.sparta.levelup_backend.domain.game.dto.responseDto.GameEntityResponseDto;
import java.util.List;
import java.util.Objects;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.levelup_backend.domain.game.dto.requestDto.UpdateGameRequestDto;
import com.sparta.levelup_backend.domain.game.dto.responseDto.GameListResponseDto;
import com.sparta.levelup_backend.domain.game.dto.responseDto.GameResponseDto;
import com.sparta.levelup_backend.domain.game.entity.GameEntity;
import com.sparta.levelup_backend.domain.game.repository.GameRepository;
import com.sparta.levelup_backend.client.UserServiceClient;
import com.sparta.levelup_backend.domain.review.dto.response.UserEntityResponseDto;
import com.sparta.levelup_backend.exception.common.DuplicateException;
import com.sparta.levelup_backend.exception.common.ForbiddenException;
import com.sparta.levelup_backend.exception.common.NetworkTimeoutException;
import com.sparta.levelup_backend.utill.UserRole;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
	private final GameRepository gameRepository;
	private final UserServiceClient userServiceClient;

	private final KafkaTemplate<String, String> kafkaTemplate;
	private static final String TOPIC = "game-delete-events";


	@Override
	public GameEntity saveGame(String name, String imgUrl, String genre, Long userId) {
		UserEntityResponseDto user = getUser(userId);
		checkAdminAuth(user);

		return gameRepository.save(
			GameEntity.builder()
				.name(name)
				.imgUrl(imgUrl)
				.genre(genre)
				.userId(user.getId())
				.build());
	}

	@Transactional(readOnly = true)
	@Override
	public GameEntity findGame(Long userId, Long gameId) {
		UserEntityResponseDto user = getUser(userId);
		checkAdminAuth(user);

		GameEntity game = gameRepository.findByIdOrElseThrow(gameId);
		checkIsDeleted(game);

		return game;
	}

	@Override
	public GameEntity updateGame(Long userId, Long gameId, UpdateGameRequestDto dto) {
		UserEntityResponseDto user = getUser(userId);
		checkAdminAuth(user);

		GameEntity game = gameRepository.findByIdOrElseThrow(gameId);
		checkIsDeleted(game);

		if (Objects.nonNull(dto.getName()))
			game.updateName(dto.getName());
		if (Objects.nonNull(dto.getImgUrl()))
			game.updateImgUrl(dto.getImgUrl());
		if (Objects.nonNull(dto.getGenre()))
			game.updateGenre(dto.getGenre());

		return game;
	}

	@Override
	public GameListResponseDto findGames() {
		return new GameListResponseDto(gameRepository.findAll()
			.stream()
			.filter(game -> !game.getIsDeleted())
			.map(game -> new GameResponseDto(game.getName(), game.getImgUrl(),
				game.getGenre()))
			.toList());
	}

	@Override
	public void deleteGame(Long userId, Long gameId) {
		UserEntityResponseDto user = getUser(userId);
		checkAdminAuth(user);

		GameEntity game = gameRepository.findByIdOrElseThrow(gameId);
		checkIsDeleted(game);

		game.deleteGame();

		try {
			String key = String.valueOf(game.getId());
			kafkaTemplate.send(TOPIC, key, "Game deleted: " + game.getId());
			log.info("Kafka 메시지 전송 성공: userId = {}", game.getId());
		} catch (Exception e) {
			log.error("Kafka 메시지 전송 실패: userId = {}, 오류: {}", game.getId(), e.getMessage());
		}
	}

	private void checkAdminAuth(UserEntityResponseDto user) {
		if (!user.getRole().equals(UserRole.ADMIN)) {
			throw new ForbiddenException(FORBIDDEN_ACCESS);
		}
	}

	private void checkIsDeleted(GameEntity game) {
		if (game.getIsDeleted()) {
			throw new DuplicateException(GAME_ISDELETED);
		}
	}

	@Override
	public GameEntityResponseDto findCommunityGameById(Long gameId) {
		GameEntity game = gameRepository.findByIdOrElseThrow(gameId);
		UserEntityResponseDto user = getUser(game.getUserId());
		return GameEntityResponseDto.from(game);
	}

	@Override
	public List<GameEntityResponseDto> findCommunityAllGames(List<Long> gameIds) {
		return gameRepository.findAllByIdIn(gameIds);
	}

	public UserEntityResponseDto getUser(Long userId) {
		try{
			return userServiceClient.findUserById(userId);
		}catch(FeignException e){
			throw new NetworkTimeoutException(e.contentUTF8());
		}
	}

	public List<UserEntityResponseDto> findAllUsers(List<Long> userIds) {
		try{
			return userServiceClient.findAllUsers(userIds);
		}catch(FeignException e){
			throw new NetworkTimeoutException(e.contentUTF8());
		}
	}
}

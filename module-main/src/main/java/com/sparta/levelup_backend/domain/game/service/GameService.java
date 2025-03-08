package com.sparta.levelup_backend.domain.game.service;

import com.sparta.levelup_backend.domain.game.dto.requestDto.UpdateGameRequestDto;
import com.sparta.levelup_backend.domain.game.dto.responseDto.GameEntityResponseDto;
import com.sparta.levelup_backend.domain.game.dto.responseDto.GameListResponseDto;
import com.sparta.levelup_backend.domain.game.entity.GameEntity;
import java.util.List;

public interface GameService {
	GameEntity saveGame(String name, String imgUrl, String genre, Long userId);

	GameEntity findGame(Long userId, Long gameId);

	void deleteGame(Long userId, Long gameId);

	GameEntity updateGame(Long userId, Long gameId, UpdateGameRequestDto dto);

	GameListResponseDto findGames();

    GameEntityResponseDto findCommunityGameById(Long gameId);

	List<GameEntityResponseDto> findCommunityAllGames(List<Long> gameIds);
}

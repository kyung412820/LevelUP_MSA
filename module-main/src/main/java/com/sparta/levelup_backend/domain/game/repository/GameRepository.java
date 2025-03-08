package com.sparta.levelup_backend.domain.game.repository;

import static com.sparta.levelup_backend.exception.common.ErrorCode.*;

import com.sparta.levelup_backend.domain.game.dto.responseDto.GameEntityResponseDto;
import com.sparta.levelup_backend.domain.game.entity.GameEntity;
import com.sparta.levelup_backend.exception.common.NotFoundException;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<GameEntity, Long> {
	default GameEntity findByIdOrElseThrow(Long id){
		return findById(id).orElseThrow(() -> new NotFoundException(GAME_NOT_FOUND));
	}

	List<GameEntityResponseDto> findAllByIdIn(List<Long> gameIds);

}

package com.sparta.levelup_backend.domain.game.dto.responseDto;

import com.sparta.levelup_backend.domain.game.entity.GameEntity;
import com.sparta.levelup_backend.domain.review.dto.response.UserResponseDto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameEntityResponseDto {
	private Long id;
	private String name;
	private String imgUrl;
	private String genre;
	private Long userId;
	private Boolean isDeleted = false;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public GameEntityResponseDto(Long id, String name, String imgUrl, String genre, Long userId, Boolean isDeleted, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.id = id;
		this.name = name;
		this.imgUrl = imgUrl;
		this.genre = genre;
		this.userId = userId;
		this.isDeleted = isDeleted;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public static GameEntityResponseDto from(GameEntity game) {
		return new GameEntityResponseDto(
			game.getId(),
			game.getName(),
			game.getImgUrl(),
			game.getGenre(),
			game.getUserId(),
			game.getIsDeleted(),
			game.getCreatedAt(),
			game.getUpdatedAt()
		);
	}
}

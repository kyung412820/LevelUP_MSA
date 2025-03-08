package com.sparta.levelup_backend.domain.game.dto.responseDto;

import com.sparta.levelup_backend.domain.game.entity.GameEntity;
import com.sparta.levelup_backend.domain.user.dto.response.UserEntityResponseDto;
import com.sparta.levelup_backend.domain.user.entity.UserEntity;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameEntityResponseDto {
	private Long id;
	private String name;
	private String imgUrl;
	private String genre;
	private UserEntityResponseDto user;
	private Boolean isDeleted = false;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public GameEntityResponseDto(Long id, String name, String imgUrl, String genre, UserEntity user, Boolean isDeleted, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.id = id;
		this.name = name;
		this.imgUrl = imgUrl;
		this.genre = genre;
		this.user = UserEntityResponseDto.from(user);
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
			game.getUser(),
			game.getIsDeleted(),
			game.getCreatedAt(),
			game.getUpdatedAt()
		);
	}
}

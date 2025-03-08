package com.sparta.domain.community.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GameResponseDto {
	private Long id;
	private String name;
	private String imgUrl;
	private String genre;
	private Long userId;
	private Boolean isDeleted = false;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public GameResponseDto(Long id, String name, String imgUrl, String genre, Long userId, Boolean isDeleted, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.id = id;
		this.name = name;
		this.imgUrl = imgUrl;
		this.genre = genre;
		this.userId = userId;
		this.isDeleted = isDeleted;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
}

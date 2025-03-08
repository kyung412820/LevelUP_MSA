package com.sparta.levelup_backend.domain.review.dto.response;


import com.sparta.levelup_backend.utill.UserRole;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserResponseDto {
	private Long id;
	private String email;
	private String nickName;
	private String imgUrl;
	private UserRole role;
	private String password;
	private String phoneNumber;
	private String provider;
	private String customerKey;
	private Boolean isDeleted = false;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public UserResponseDto(Long id, String email, String nickName, String imgUrl,
		UserRole role, String password, String phoneNumber, String provider, String customerKey, Boolean isDeleted, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.id = id;
		this.email = email;
		this.nickName = nickName;
		this.imgUrl = imgUrl;
		this.role = role;
		this.password = password;
		this.phoneNumber = phoneNumber;
		this.provider = provider;
		this.customerKey = customerKey;
		this.isDeleted = isDeleted;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

}

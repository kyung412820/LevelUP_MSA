package com.sparta.domain.user.dto.response;

import com.sparta.domain.user.entity.UserEntity;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import com.sparta.domain.user.enums.*;

@Getter
@Setter
@Builder
public class UserEntityResponseDto {
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

	public UserEntityResponseDto(Long id, String email, String nickName, String imgUrl,
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

	public static UserEntityResponseDto from(UserEntity user) {
		return new UserEntityResponseDto(user.getId(),
			user.getEmail(),
			user.getNickName(),
			user.getImgUrl(),
			user.getRole(),
			user.getPassword(),
			user.getPhoneNumber(),
			user.getProvider(),
			user.getCustomerKey(),
			user.getIsDeleted(),
			user.getCreatedAt(),
			user.getUpdatedAt());
	}

}

package com.sparta.domain.chat.document;

import com.sparta.domain.user.entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Participant {

	private Long userId;
	private String nickname;
	private String profileImgUrl;

	public Participant(UserEntity userEntity) {
		this.userId = userEntity.getId();
		this.nickname = userEntity.getNickName();
		this.profileImgUrl = userEntity.getImgUrl();
	}
}

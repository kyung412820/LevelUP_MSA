package com.sparta.levelup_backend.domain.user.dto.response;

import com.sparta.levelup_backend.domain.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserUpdateResponseDto {

    private final String email;

    private final String nickName;

    private final String imgUrl;

    private final String phoneNumber;

    public static UserUpdateResponseDto from(UserEntity user) {

        return new UserUpdateResponseDto(
            user.getEmail(),
            user.getNickName(),
            user.getImgUrl(),
            user.getPhoneNumber()
        );
    }
}

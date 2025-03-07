package com.sparta.Authentication.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserAuthenticationResponseDto {

    private final Long id;

    private final String email;

    private final String nickName;

    private final String role;



}

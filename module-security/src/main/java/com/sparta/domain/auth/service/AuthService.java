package com.sparta.domain.auth.service;

import com.sparta.domain.auth.dto.request.*;

import org.springframework.http.HttpHeaders;
import org.springframework.transaction.annotation.Transactional;

public interface AuthService {

	@Transactional
	void signUpUser(SignUpUserRequestDto signUpUserRequestDto);

	@Transactional
	void oAuth2signUpUser(OAuthUserRequestDto dto);

	HttpHeaders authenticate(SignInUserRequestDto dto);
}

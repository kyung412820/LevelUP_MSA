package com.sparta.domain.auth.service;

import com.sparta.domain.auth.dto.response.UserEntityResponseDto;
import com.sparta.exception.common.NetworkTimeoutException;
import com.sparta.exception.common.PasswordIncorrectException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.domain.auth.dto.request.OAuthUserRequestDto;
import com.sparta.domain.auth.dto.request.SignInUserRequestDto;
import com.sparta.domain.auth.dto.request.SignUpUserRequestDto;
import com.sparta.util.JwtUtils;
import com.sparta.client.UserServiceClient;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final JwtUtils jwtUtils;
	private final UserServiceClient userServiceClient;

	@Transactional
	@Override
	public void signUpUser(SignUpUserRequestDto signUpUserRequestDto) {

		// ✅ user-service에 회원가입 요청 (FeignClient 사용)
		createUser(signUpUserRequestDto); // ✅ 토큰 추가

		log.info("✅ signUpUser - User created via user-service: {}", signUpUserRequestDto.getEmail());
	}

	@Transactional
	@Override
	public void oAuth2signUpUser(OAuthUserRequestDto dto) {
		// ✅ user-service에 OAuth 사용자 업데이트 요청 (FeignClient 사용)
		updateOAuthUser(dto);

		log.info("✅ oAuth2signUpUser - OAuth user updated via user-service: {}", dto.getEmail());
	}


	@Override
	public HttpHeaders authenticate(SignInUserRequestDto dto) {
		// ✅ user-service에서 사용자 정보 조회 (FeignClient 사용)
		UserEntityResponseDto user = getUser(dto.getEmail());

		// ✅ 비밀번호 검증
		if (!bCryptPasswordEncoder.matches(dto.getPassword(), user.getPassword())) {
			throw new PasswordIncorrectException();
		}

		// ✅ JWT 토큰 생성
		String accessToken = jwtUtils.createAccessToken(
			user.getEmail(), user.getId(), user.getNickName(), String.valueOf(user.getRole())
		);
		String refreshToken = jwtUtils.createRefreshToken(
			user.getEmail(), user.getId(), user.getNickName(), String.valueOf(user.getRole())
		);

		// ✅ 쿠키로 응답
		ResponseCookie accessCookie = createCookie("accessToken", accessToken, 30 * 60);
		ResponseCookie refreshCookie = createCookie("refreshToken", refreshToken, 12 * 60 * 60);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", accessToken);
		headers.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
		headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());

		log.info("✅ authenticate - JWT tokens issued for: {}", user.getEmail());
		return headers;
	}


	private ResponseCookie createCookie(String name, String token, long maxAge) {
		return ResponseCookie.from(name, jwtUtils.substringToken(token))
			.path("/")
			.maxAge(maxAge)
			.build();
	}

	public UserEntityResponseDto getUser(String email) {
		try{
			return userServiceClient.getUserByEmail(email);
		}catch(FeignException e){
			throw new NetworkTimeoutException(e.contentUTF8());
		}
	}

	public void createUser(SignUpUserRequestDto signUpUserRequestDto) {
		try {
			userServiceClient.createUser(signUpUserRequestDto);
		} catch (FeignException e) {
			throw new NetworkTimeoutException(e.contentUTF8());
		}
	}

		public void updateOAuthUser(OAuthUserRequestDto oAuthUserRequestDto) {
		try{
			userServiceClient.updateOAuthUser(oAuthUserRequestDto);
		}catch(FeignException e){
			throw new NetworkTimeoutException(e.contentUTF8());
		}
	}
}
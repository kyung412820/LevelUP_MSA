package com.sparta.domain.auth.service;

import com.sparta.client.UserServiceClient;
import com.sparta.config.CustomOAuth2User;
import com.sparta.domain.auth.dto.request.OAuthUserRequestDto;
import com.sparta.domain.auth.dto.response.GoogleResponseDto;
import com.sparta.domain.auth.dto.response.KakaoResponseDto;
import com.sparta.domain.auth.dto.response.NaverResponseDto;
import com.sparta.domain.auth.dto.response.OAuth2ResponseDto;
import com.sparta.domain.auth.dto.response.UserEntityResponseDto;
import com.sparta.domain.auth.enums.UserRole;
import com.sparta.exception.common.ErrorCode;
import com.sparta.exception.common.NetworkTimeoutException;

import java.util.Map;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final UserServiceClient userServiceClient;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		OAuth2User oAuth2User = super.loadUser(userRequest);
		OAuth2ResponseDto oAuth2ResponseDto;

		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		if ("naver".equals(registrationId)) {
			oAuth2ResponseDto = new NaverResponseDto(
				(Map<String, Object>) oAuth2User.getAttributes().get("response")
			);
		} else if ("google".equals(registrationId)) {
			oAuth2ResponseDto = new GoogleResponseDto(oAuth2User.getAttributes());
		} else {
			oAuth2ResponseDto = new KakaoResponseDto(oAuth2User.getAttributes());
		}

		UserEntityResponseDto user;

		try {

			user = getUser(oAuth2ResponseDto.getEmail());


			if (user.getIsDeleted()) {
				throw new OAuth2AuthenticationException(ErrorCode.ALREADY_DELETED_USER.toString());
			}


			if (!user.getProvider().startsWith(registrationId)) {
				throw new OAuth2AuthenticationException(ErrorCode.AUTH_TYPE_MISMATCH.toString());
			}

		} catch (Exception e) {
			// ✅ user-service에서 사용자를 찾지 못한 경우, 새로운 OAuth 사용자를 생성
			OAuthUserRequestDto newUserDto = OAuthUserRequestDto.builder()
				.email(oAuth2ResponseDto.getEmail())
				.nickName(oAuth2ResponseDto.getNickName())
				.role(String.valueOf(UserRole.USER))
				.provider(registrationId + "new")
				.build();

			createOAuthUser(newUserDto);


			user = getUser(oAuth2ResponseDto.getEmail());
		}

		return new CustomOAuth2User(user);
	}

	public UserEntityResponseDto getUser(String email) {
		try{
			return userServiceClient.getUserByEmail(email);
		}catch(FeignException e){
			throw new NetworkTimeoutException(e.contentUTF8());
		}
	}

	public void createOAuthUser(OAuthUserRequestDto oAuthUserRequestDto) {
		try{
			userServiceClient.createOAuthUser(oAuthUserRequestDto);
		}catch(FeignException e){
			throw new NetworkTimeoutException(e.contentUTF8());
		}
	}

}

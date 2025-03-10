package com.sparta.domain.auth.service;

import static com.sparta.exception.common.ErrorCode.AUTH_TYPE_MISMATCH;

import com.sparta.client.UserServiceClient;
import com.sparta.config.CustomUserDetails;
import com.sparta.domain.auth.dto.response.UserEntityResponseDto;
import com.sparta.exception.common.AlreadyDeletedUserException;
import com.sparta.exception.common.MismatchException;
import com.sparta.exception.common.NetworkTimeoutException;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserServiceClient userServiceClient;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// ✅ user-service에서 사용자 정보 조회 (FeignClient 사용)
		UserEntityResponseDto user = getUser(email);

		if (user.getIsDeleted()) {
			throw new AlreadyDeletedUserException();
		}

		String provider = user.getProvider();
		if (!provider.startsWith("none")) {
			throw new MismatchException(AUTH_TYPE_MISMATCH);
		}

		return new CustomUserDetails(user);
	}

	public UserEntityResponseDto getUser(String email) {
		try{
			return userServiceClient.getUserByEmail(email);
		}catch(FeignException e){
			throw new NetworkTimeoutException(e.contentUTF8());
		}
	}
}

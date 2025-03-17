package com.sparta.client;

import com.sparta.config.CustomErrorDecoder;
import com.sparta.config.FeignConfig;
import com.sparta.domain.auth.dto.request.OAuthUserRequestDto;
import com.sparta.domain.auth.dto.request.SignUpUserRequestDto;
import com.sparta.domain.auth.dto.response.UserEntityResponseDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "module-user", configuration = {CustomErrorDecoder.class, FeignConfig.class})
public interface UserServiceClient {

	@PostMapping("/v2/users/intra/create-user")
	void createUser(@RequestBody SignUpUserRequestDto requestDto);

	@PutMapping("/v2/users/intra/update-oauth")
	void updateOAuthUser(@RequestBody OAuthUserRequestDto dto);

	@GetMapping("/v2/users/intra/{email}")
	UserEntityResponseDto getUserByEmail(@PathVariable("email") String email);

	@PostMapping("/v2/users/intra/create-oauth")
	void createOAuthUser(@RequestBody OAuthUserRequestDto dto);

}

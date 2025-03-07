package com.sparta.domain.community.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "user-service", url = "http://localhost:8080")
public interface UserServiceClient {

	@PostMapping("/create")
	ResponseEntity<UserResponseDto> createUser(
		@RequestHeader("Authorization") String token,
		@RequestBody SignUpUserRequestDto requestDto
	);

	@PutMapping("/update-oauth")
	void updateOAuthUser(@RequestBody OAuthUserRequestDto dto);

	@GetMapping("/{email}")
	UserResponseDto getUserByEmail(@PathVariable("email") String email);

	@PostMapping("/create-oauth")
	void createOAuthUser(@RequestBody OAuthUserRequestDto dto);

}

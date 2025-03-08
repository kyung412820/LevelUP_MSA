package com.sparta.levelup_backend.domain.user.controller;

import static com.sparta.levelup_backend.common.ApiResMessage.*;
import static com.sparta.levelup_backend.common.ApiResponse.*;

import com.sparta.levelup_backend.domain.user.dto.request.UserAuthenticationRequestDto;
import com.sparta.levelup_backend.domain.user.dto.response.UserEntityResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.levelup_backend.common.ApiResponse;
import com.sparta.levelup_backend.domain.user.dto.request.ChangePasswordDto;
import com.sparta.levelup_backend.domain.user.dto.request.DeleteUserRequestDto;
import com.sparta.levelup_backend.domain.user.dto.request.ResetPasswordConfirmDto;
import com.sparta.levelup_backend.domain.user.dto.request.ResetPasswordDto;
import com.sparta.levelup_backend.domain.user.dto.request.UpdateUserImgUrlReqeustDto;
import com.sparta.levelup_backend.domain.user.dto.request.UpdateUserRequestDto;
import com.sparta.levelup_backend.domain.user.dto.response.UserResponseDto;
import com.sparta.levelup_backend.domain.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v2")
public class UserController {

	private final UserService userService;

    @GetMapping("/users/findUserById/{userId}")
	UserEntityResponseDto findUserById(@PathVariable("userId") Long userId) {
        return userService.findCommunityUserById(userId);
    }

    @PostMapping("/users/findAllUsers")
    List<UserEntityResponseDto> findAllUsers(@RequestBody List<Long> userIds) {
        return userService.findAllCommunityUsers(userIds);
    }


    @GetMapping("/admin/users/{userId}")
	public ApiResponse<UserResponseDto> findUserById(
		HttpServletRequest request, @PathVariable Long userId) {

		String encodedAuth = request.getHeader("UserAuthentication");
		UserAuthenticationRequestDto authRequest = UserAuthenticationRequestDto.from(encodedAuth);

		UserResponseDto responseDto = userService.findUserById(authRequest.getRole(), userId);

		return success(HttpStatus.OK, FIND_SUCCESS, responseDto);
	}

	@GetMapping("/users")
	public ApiResponse<UserResponseDto> findUser(
		HttpServletRequest request
	) {
		String encodedAuth = request.getHeader("UserAuthentication");
		UserAuthenticationRequestDto authRequest = UserAuthenticationRequestDto.from(encodedAuth);
		UserResponseDto responseDto = userService.findUser(authRequest.getId());

		return success(HttpStatus.OK, FIND_SUCCESS, responseDto);
	}

	@PatchMapping("/users")
	public ApiResponse<UserResponseDto> updateUser(
		HttpServletRequest request,
		@Valid @RequestBody UpdateUserRequestDto dto
	) {

		String encodedAuth = request.getHeader("UserAuthentication");
		UserAuthenticationRequestDto authRequest = UserAuthenticationRequestDto.from(encodedAuth);
		UserResponseDto responseDto = userService.updateUser(authRequest.getId(), dto);

		return success(HttpStatus.OK, UPDATE_SUCCESS, responseDto);
	}

	@PatchMapping("/users/changingPassword")
	public ApiResponse<Void> changePassword(
		HttpServletRequest request,
		@Valid @RequestBody ChangePasswordDto dto) {

		String encodedAuth = request.getHeader("UserAuthentication");
		UserAuthenticationRequestDto authRequest = UserAuthenticationRequestDto.from(encodedAuth);
		userService.changePassword(authRequest.getId(), dto);

		return success(HttpStatus.OK, PASSWORD_CHANGE_SUCCESS);
	}

	@PatchMapping("/users/profileImage")
	public ApiResponse<UserResponseDto> updateImgUrl(
		HttpServletRequest request,
		@Valid @RequestBody UpdateUserImgUrlReqeustDto dto
	) {
		String encodedAuth = request.getHeader("UserAuthentication");
		UserAuthenticationRequestDto authRequest = UserAuthenticationRequestDto.from(encodedAuth);
		UserResponseDto responseDto = userService.updateImgUrl(authRequest.getId(), dto);

		return success(HttpStatus.OK, UPDATE_SUCCESS, responseDto);
	}

	@DeleteMapping("/users")
	public ApiResponse<Void> deleteUser(
		HttpServletRequest request,
		@Valid @RequestBody DeleteUserRequestDto dto
	) {
		String encodedAuth = request.getHeader("UserAuthentication");
		UserAuthenticationRequestDto authRequest = UserAuthenticationRequestDto.from(encodedAuth);
		userService.deleteUser(authRequest.getId(), dto);

		return success(HttpStatus.OK, DELETE_SUCCESS);
	}

	@PostMapping("/users/resetPassword")
	public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordDto dto) {
		userService.resetPassword(dto);
		return success(HttpStatus.OK, RESET_EMAIL_SEND_SUCCESS);
	}

	@PostMapping("/users/resetPasswordConfirm")
	public ApiResponse<Void> resetPasswordConfirm(@Valid @RequestBody ResetPasswordConfirmDto dto) {
		userService.resetPasswordConfirm(dto);
		return success(HttpStatus.OK, RESET_PASSWORD_SUCCESS);
	}
}

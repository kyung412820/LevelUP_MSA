package com.sparta.domain.user.service;


import com.sparta.domain.user.dto.request.ChangePasswordDto;
import com.sparta.domain.user.dto.request.DeleteUserRequestDto;
import com.sparta.domain.user.dto.request.OAuthUserRequestDto;
import com.sparta.domain.user.dto.request.ResetPasswordConfirmDto;
import com.sparta.domain.user.dto.request.ResetPasswordDto;
import com.sparta.domain.user.dto.request.SignUpUserRequestDto;
import com.sparta.domain.user.dto.request.UpdateUserImgUrlReqeustDto;
import com.sparta.domain.user.dto.request.UserRequestDto;
import com.sparta.domain.user.dto.response.UserEntityResponseDto;
import com.sparta.domain.user.dto.response.UserResponseDto;
import java.util.List;


public interface UserService {

	UserResponseDto findUserById(String role, Long id);

	UserResponseDto findUser(Long id);

	UserResponseDto updateUser(Long id, UserRequestDto dto);

	void changePassword(Long id, ChangePasswordDto dto);

	UserResponseDto updateImgUrl(Long id, UpdateUserImgUrlReqeustDto dto);

	void deleteUser(Long id, DeleteUserRequestDto dto);

	void resetPassword(ResetPasswordDto dto);

	void resetPasswordConfirm(ResetPasswordConfirmDto dto);

	void createUser(SignUpUserRequestDto requestDto);

	void updateOAuthUser(OAuthUserRequestDto dto);

	UserEntityResponseDto findUserByEmail(String email);

	void createOAuthUser(OAuthUserRequestDto dto);

	UserEntityResponseDto findCommunityUserById(Long userId);

	List<UserEntityResponseDto> findAllCommunityUsers(List<Long> userIds);
}

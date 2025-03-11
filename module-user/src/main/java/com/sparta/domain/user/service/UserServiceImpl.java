package com.sparta.domain.user.service;

import static com.sparta.domain.user.dto.UserMessage.*;
import static com.sparta.exception.common.ErrorCode.*;

import com.sparta.domain.user.dto.request.*;
import com.sparta.domain.user.dto.response.UserEntityResponseDto;
import com.sparta.domain.user.dto.response.UserResponseDto;
import com.sparta.domain.user.entity.UserEntity;
import com.sparta.domain.user.enums.UserRole;
import com.sparta.exception.common.CurrentPasswordNotMatchedException;
import com.sparta.exception.common.ForbiddenException;
import com.sparta.exception.common.MismatchException;
import com.sparta.exception.common.PasswordConfirmNotMatchedException;
import com.sparta.domain.user.repository.UserRepository;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "user-delete-events";

    @Override
    public UserResponseDto findUserById(String role, Long id) {

        if (role.equals("ROLE_ADMIN")) {
            UserEntity user = userRepository.findByIdOrElseThrow(id);

            return UserResponseDto.from(user);
        }
        throw new ForbiddenException(FORBIDDEN_ACCESS);
    }

    @Override
    public UserResponseDto findUser(Long id) {
        UserEntity user = userRepository.findByIdOrElseThrow(id);

        return UserResponseDto.from(user);
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(Long id, UserRequestDto dto) {

        UserEntity user = userRepository.findByIdOrElseThrow(id);

        if (dto.getEmail() != null) {
            userRepository.existsByEmailOrElseThrow(dto.getEmail());
            user.updateEmail(dto.getEmail());
        }

        if (dto.getNickName() != null) {
            user.updateNickName(dto.getNickName());
        }

        if (dto.getImgUrl() != null) {
            user.updateImgUrl(dto.getImgUrl());
        }

        if (dto.getPhoneNumber() != null) {
            user.updatePhoneNumber(dto.getPhoneNumber());
        }

        return UserResponseDto.from(user);
    }

    @Override
    @Transactional
    public void changePassword(Long id, ChangePasswordDto dto) {
        UserEntity user = userRepository.findByIdOrElseThrow(id);

        if (bCryptPasswordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            if (dto.getNewPassword().equals(dto.getPasswordConfirm())) {
                user.changePassword(bCryptPasswordEncoder.encode(dto.getNewPassword()));
            } else {
                throw new PasswordConfirmNotMatchedException();
            }
        } else {
            throw new CurrentPasswordNotMatchedException();
        }

    }

    @Override
    @Transactional
    public UserResponseDto updateImgUrl(Long id, UpdateUserImgUrlReqeustDto dto) {

        UserEntity user = userRepository.findByIdOrElseThrow(id);
        user.updateImgUrl(dto.getImgUrl());


        return UserResponseDto.from(user);

    }

    @Override
    @Transactional
    public void deleteUser(Long id, DeleteUserRequestDto dto) {

        UserEntity user = userRepository.findByIdOrElseThrow(id);

        if (user.getPassword() == null || bCryptPasswordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            user.delete();
            try {
                String key = String.valueOf(user.getId());
                kafkaTemplate.send(TOPIC, key, String.valueOf(user.getId()));
                log.info("Kafka 메시지 전송 성공: userId = {}", user.getId());
            } catch (Exception e) {
                log.error("Kafka 메시지 전송 실패: userId = {}, 오류: {}", user.getId(), e.getMessage());
            }

        } else {
            throw new CurrentPasswordNotMatchedException();
        }

    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordDto dto) {
        UserEntity user = userRepository.findByEmailOrElseThrow(dto.getEmail());

        if (!user.getProvider().startsWith("none")) {
            throw new MismatchException(AUTH_TYPE_NOT_GENERAL);
        }

        if (user.getNickName().equals(dto.getNickName())) {
            String passwordResetCode = UUID.randomUUID().toString();
            ValueOperations<String, Object> passwordResetCodes = redisTemplate.opsForValue();
            passwordResetCodes.set(PASSWORD_RESET_CODE_PREFIX + dto.getEmail(), passwordResetCode,
                Duration.ofSeconds(300));
        } else {
            throw new MismatchException(INVALID_NICKNAME);
        }
    }

    @Override
    @Transactional
    public void resetPasswordConfirm(ResetPasswordConfirmDto dto) {

        ValueOperations<String, Object> passwordResetCodes = redisTemplate.opsForValue();
        String passwordResetCode = (String) passwordResetCodes.get(
            PASSWORD_RESET_CODE_PREFIX + dto.getEmail());
        UserEntity user = userRepository.findByEmailOrElseThrow(dto.getEmail());

        if (dto.getResetCode().equals(passwordResetCode)) {
            if (dto.getNewPassword().equals(dto.getPasswordConfirm())) {
                user.changePassword(bCryptPasswordEncoder.encode(dto.getNewPassword()));
                passwordResetCodes.set(PASSWORD_RESET_CODE_PREFIX + dto.getEmail(),
                    passwordResetCode,
                    Duration.ofSeconds(1));
            } else {
                throw new PasswordConfirmNotMatchedException();
            }
        } else {
            throw new MismatchException(INVALID_RESETCODE);
        }

    }

    @Override
    public void createUser(SignUpUserRequestDto requestDto) {
        userRepository.existsByEmailOrElseThrow(requestDto.getEmail());
        UserEntity user = UserEntity.builder()
            .email(requestDto.getEmail())
            .nickName(requestDto.getNickName())
            .imgUrl(requestDto.getImgUrl())
            .phoneNumber(requestDto.getPhoneNumber())
            .password(bCryptPasswordEncoder.encode(requestDto.getPassword()))
            .role(UserRole.USER)
            .build();

        userRepository.save(user);

    }

    @Override
    @Transactional
    public void updateOAuthUser(OAuthUserRequestDto requestDto) {
        UserEntity user = userRepository.findByEmailOrElseThrow(requestDto.getEmail());
        user.updateNickName(requestDto.getNickName());
        user.updatePhoneNumber(requestDto.getPhoneNumber());
        user.updateProvider(requestDto.getProvider());
    }

    @Override
    public UserEntityResponseDto findUserByEmail(String email) {
        UserEntity user = userRepository.findByEmailOrElseThrow(email);
        return UserEntityResponseDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .nickName(user.getNickName())
            .imgUrl(user.getImgUrl())
            .password(user.getPassword())
            .role(user.getRole())
            .phoneNumber(user.getPhoneNumber())
            .customerKey(user.getCustomerKey())
            .isDeleted(user.getIsDeleted())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }

    @Override
    public void createOAuthUser(OAuthUserRequestDto requestDto) {
        userRepository.existsByEmailOrElseThrow(requestDto.getEmail());
        UserEntity user = UserEntity.builder()
            .email(requestDto.getEmail())
            .nickName(requestDto.getNickName())
            .phoneNumber(requestDto.getPhoneNumber())
            .role(UserRole.USER)
            .provider(requestDto.getProvider())
            .build();

        userRepository.save(user);
    }

    @Override
    public UserEntityResponseDto findCommunityUserById(Long userId) {
        return UserEntityResponseDto.from(userRepository.findByIdOrElseThrow(userId));
    }

    @Override
    public List<UserEntityResponseDto> findAllCommunityUsers(List<Long> userIds) {
        return userRepository.findAllByIdIn(userIds);
    }
}

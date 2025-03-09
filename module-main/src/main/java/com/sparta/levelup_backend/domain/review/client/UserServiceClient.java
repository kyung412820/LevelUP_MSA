package com.sparta.levelup_backend.domain.review.client;

import com.sparta.levelup_backend.domain.review.dto.response.UserResponseDto;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "module-user")
public interface UserServiceClient {

    @GetMapping("/v2/users/intra/findUserById/{userId}")
    UserResponseDto findUserById(@PathVariable("userId") Long userId);

    @PostMapping("/v2/users/intra/findAllUsers")
    List<UserResponseDto> findAllUsers(@RequestBody List<Long> userIds);
    ;
}

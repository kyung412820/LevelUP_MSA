package com.sparta.levelup_backend.client;

import com.sparta.levelup_backend.domain.review.dto.response.UserEntityResponseDto;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "module-user")
public interface UserServiceClient {

    @GetMapping("/v2/users/intra/findUserById/{userId}")
    UserEntityResponseDto findUserById(@PathVariable("userId") Long userId);

    @PostMapping("/v2/users/intra/findAllUsers")
    List<UserEntityResponseDto> findAllUsers(@RequestBody List<Long> userIds);
    ;
}

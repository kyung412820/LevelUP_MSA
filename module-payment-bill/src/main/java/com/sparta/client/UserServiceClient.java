package com.sparta.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.sparta.domain.bill.dto.responseDto.UserResponseDto;

@FeignClient(name = "module-user")
public interface UserServiceClient {

    @GetMapping("/v2/users/intra/findUserById/{userId}")
    UserResponseDto findUserById(@PathVariable("userId") Long userId);

    @PostMapping("/v2/users/intra/findAllUsers")
    List<UserResponseDto> findAllUsers(@RequestBody List<Long> userIds);


}

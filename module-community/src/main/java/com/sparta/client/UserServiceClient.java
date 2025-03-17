package com.sparta.client;

import com.sparta.config.CustomErrorDecoder;
import com.sparta.config.FeignConfig;
import com.sparta.domain.community.dto.response.UserEntityResponseDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "module-user", configuration = {CustomErrorDecoder.class, FeignConfig.class})
public interface UserServiceClient {

    @GetMapping("/v2/users/intra/findUserById/{userId}")
    UserEntityResponseDto findUserById(@PathVariable("userId") Long userId);

    @PostMapping("/v2/users/intra/findAllUsers")
    List<UserEntityResponseDto> findAllUsers(@RequestBody List<Long> userIds);


}

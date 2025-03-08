package com.sparta.domain.community.client;

import com.sparta.domain.community.dto.response.GameResponseDto;
import com.sparta.domain.community.dto.response.UserResponseDto;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "module-main")
public interface EntityServiceClient {

    @GetMapping("/v2/users/findUserById/{userId}")
    UserResponseDto findUserById(@PathVariable("userId") Long userId);

    @GetMapping("/v1/games/findGameById/{gameId}")
    GameResponseDto findGameById(@PathVariable("gameId") Long gameId);

    @PostMapping("/v2/users/findAllUsers")
    List<UserResponseDto> findAllUsers(@RequestBody List<Long> userIds);

    @PostMapping("/v1/games/findAllGames")
    List<GameResponseDto> findAllGames(@RequestBody List<Long> gameIds);
}

package com.sparta.domain.community.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.sparta.domain.community.dto.response.GameResponseDto;

@FeignClient(name = "module-main")
public interface GameServiceClient {

	@GetMapping("/v1/admin/games/intra/findGameById/{gameId}")
	GameResponseDto findGameById(@PathVariable("gameId") Long gameId);

	@PostMapping("/v1/admin/games/intra/findAllGames")
	List<GameResponseDto> findAllGames(@RequestBody List<Long> gameIds);
}

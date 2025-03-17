package com.sparta.client;

import com.sparta.config.CustomErrorDecoder;
import com.sparta.config.FeignConfig;
import com.sparta.domain.community.dto.response.GameEntityResponseDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "module-main", configuration = {CustomErrorDecoder.class, FeignConfig.class})
public interface GameServiceClient {

	@GetMapping("/v1/admin/games/intra/findGameById/{gameId}")
	GameEntityResponseDto findGameById(@PathVariable("gameId") Long gameId);

	@PostMapping("/v1/admin/games/intra/findAllGames")
	List<GameEntityResponseDto> findAllGames(@RequestBody List<Long> gameIds);
}

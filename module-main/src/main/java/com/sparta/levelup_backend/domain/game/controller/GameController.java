package com.sparta.levelup_backend.domain.game.controller;

import static com.sparta.levelup_backend.common.ApiResMessage.*;
import static com.sparta.levelup_backend.common.ApiResponse.*;
import static org.springframework.http.HttpStatus.*;

import com.sparta.levelup_backend.domain.game.dto.requestDto.UserAuthenticationRequestDto;
import com.sparta.levelup_backend.domain.game.dto.responseDto.GameEntityResponseDto;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.levelup_backend.common.ApiResponse;
import com.sparta.levelup_backend.domain.game.dto.requestDto.CreateGameRequestDto;
import com.sparta.levelup_backend.domain.game.dto.requestDto.UpdateGameRequestDto;
import com.sparta.levelup_backend.domain.game.dto.responseDto.GameListResponseDto;
import com.sparta.levelup_backend.domain.game.dto.responseDto.GameResponseDto;
import com.sparta.levelup_backend.domain.game.entity.GameEntity;
import com.sparta.levelup_backend.domain.game.service.GameService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class GameController {

	private final GameService gameService;

	@GetMapping("/games/intra/findGameById/{gameId}")
	GameEntityResponseDto findCommunityGameById(@PathVariable("gameId") Long gameId){
		return gameService.findCommunityGameById(gameId);
	}

	@PostMapping("/games/intra/findAllGames")
	List<GameEntityResponseDto> findCommunityAllGames(@RequestBody List<Long> gameIds){
			return gameService.findCommunityAllGames(gameIds);
	}

	@PostMapping("/admin/games")
	public ApiResponse<GameResponseDto> saveGame(HttpServletRequest request,
		@RequestBody CreateGameRequestDto dto) {

		String encodedAuth = request.getHeader("UserAuthentication");
		UserAuthenticationRequestDto authRequest = UserAuthenticationRequestDto.from(encodedAuth);

		Long userId = authRequest.getId();
		GameEntity game = gameService.saveGame(dto.getName(), dto.getImgUrl(), dto.getGenre(), userId);

		return success(CREATED, GAME_SAVE_SUCCESS, GameResponseDto.from(game));
	}

	@GetMapping("/admin/games/{gameId}")
	public ApiResponse<GameResponseDto> findGame(HttpServletRequest request,
		@PathVariable Long gameId) {

		String encodedAuth = request.getHeader("UserAuthentication");
		UserAuthenticationRequestDto authRequest = UserAuthenticationRequestDto.from(encodedAuth);

		Long userId = authRequest.getId();
		GameEntity game = gameService.findGame(userId, gameId);

		return success(OK, GAME_FOUND_SUCCESS, GameResponseDto.from(game));
	}

	@PatchMapping("/admin/games/{gameId}")
	public ApiResponse<GameResponseDto> updateGame(HttpServletRequest request,
		@PathVariable Long gameId, @RequestBody UpdateGameRequestDto dto) {

		String encodedAuth = request.getHeader("UserAuthentication");
		UserAuthenticationRequestDto authRequest = UserAuthenticationRequestDto.from(encodedAuth);

		Long userId = authRequest.getId();
		GameEntity game = gameService.updateGame(userId, gameId, dto);

		return success(OK, GAME_UPDATE_SUCCESS, GameResponseDto.from(game));
	}

	@DeleteMapping("/admin/games/{gameId}")
	public ApiResponse<Void> deleteGame(HttpServletRequest request,
		@PathVariable Long gameId) {

		String encodedAuth = request.getHeader("UserAuthentication");
		UserAuthenticationRequestDto authRequest = UserAuthenticationRequestDto.from(encodedAuth);

		Long userId = authRequest.getId();
		gameService.deleteGame(userId, gameId);

		return success(OK, GAME_DELETE_SUCCESS);
	}

	@GetMapping("/games")
	public ApiResponse<GameListResponseDto> findGames(){
		GameListResponseDto listDto = gameService.findGames();

		return success(OK, GAME_FOUND_SUCCESS, listDto);
	}
}


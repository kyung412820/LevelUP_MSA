package com.sparta.levelup_backend.domain.community.service;

import static com.sparta.levelup_backend.exception.common.ErrorCode.*;
import static com.sparta.levelup_backend.utill.UserRole.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.levelup_backend.domain.community.document.CommunityDocument;
import com.sparta.levelup_backend.domain.community.dto.request.CommnunityCreateRequestDto;
import com.sparta.levelup_backend.domain.community.dto.request.CommunityUpdateRequestDto;
import com.sparta.levelup_backend.domain.community.dto.response.CommunityListResponseDto;
import com.sparta.levelup_backend.domain.community.dto.response.CommunityReadResponseDto;
import com.sparta.levelup_backend.domain.community.dto.response.CommunityResponseDto;
import com.sparta.levelup_backend.domain.community.entity.CommunityEntity;
import com.sparta.levelup_backend.domain.community.repository.CommunityRepository;
import com.sparta.levelup_backend.domain.community.repositoryES.CommunityESRepository;
import com.sparta.levelup_backend.domain.game.entity.GameEntity;
import com.sparta.levelup_backend.domain.game.repository.GameRepository;
import com.sparta.levelup_backend.domain.user.entity.UserEntity;
import com.sparta.levelup_backend.domain.user.repository.UserRepository;
import com.sparta.levelup_backend.exception.common.DuplicateException;
import com.sparta.levelup_backend.exception.common.ForbiddenException;
import com.sparta.levelup_backend.exception.common.NotFoundException;
import com.sparta.levelup_backend.exception.common.PageOutOfBoundsException;

import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class CommunityServiceImpl implements CommunityService {
	private final UserRepository userRepository;
	private final CommunityRepository communityRepository;
	private final GameRepository gameRepository;

	private final CommunityESRepository communityESRepository;
	private final RedisTemplate redisTemplate;

	private final String COMMUNITY_CACHE_KEY = "community:";
	private final String COMMUNITY_ZSET_KEY = "community_view";

	@Override
	public CommunityResponseDto saveCommunity(Long userId, CommnunityCreateRequestDto dto) {
		UserEntity user = userRepository.findByIdOrElseThrow(userId);
		GameEntity game = gameRepository.findByIdOrElseThrow(dto.getGameId());
		checkGameIsDeleted(game);

		CommunityEntity community = communityRepository.save(
			new CommunityEntity(dto.getTitle(), dto.getContent(), user, game));

		return CommunityResponseDto.of(community, user, game);
	}

	@Transactional(readOnly = true)
	@Override
	public CommunityListResponseDto findAll(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<CommunityEntity> communityPage = communityRepository.findAllByIsDeletedFalse(pageable);

		CommunityListResponseDto responseDto = new CommunityListResponseDto(
			communityPage.stream()
				.map(community -> CommunityReadResponseDto.of(community, community.getUser(), community.getGame()))
				.toList()
		);

		if (communityPage.getTotalPages() <= page) {
			throw new PageOutOfBoundsException(PAGE_OUT_OF_BOUNDS);
		}

		if (responseDto.getCommunityList().isEmpty()) {
			throw new NotFoundException(COMMUNITY_NOT_FOUND);
		}

		return responseDto;
	}

	@Override
	public CommunityResponseDto update(Long userId, CommunityUpdateRequestDto dto) {
		CommunityEntity community = communityRepository.findByIdOrElseThrow(dto.getCommunityId());
		checkAuth(community, userId);
		checkCommunityIsDeleted(community);

		if (Objects.nonNull(dto.getTitle())) {
			community.updateTitle(dto.getTitle());
		}
		if (Objects.nonNull(dto.getContent())) {
			community.updateContent(dto.getContent());
		}

		return CommunityResponseDto.from(community);
	}

	@Override
	public void delete(Long userId, Long communityId) {
		CommunityEntity community = communityRepository.findByIdOrElseThrow(communityId);
		checkAuth(community, userId);
		checkCommunityIsDeleted(community);

		community.deleteCommunity();
	}

	@Override
	public CommunityResponseDto saveCommunityES(Long userId, CommnunityCreateRequestDto dto) {
		UserEntity user = userRepository.findByIdOrElseThrow(userId);
		GameEntity game = gameRepository.findByIdOrElseThrow(dto.getGameId());
		CommunityEntity community = communityRepository.save(
			new CommunityEntity(dto.getTitle(), dto.getContent(), user, game));
		CommunityDocument communityDocument = communityESRepository.save(CommunityDocument.from(community));

		return CommunityResponseDto.from(communityDocument);
	}

	@Override
	public CommunityListResponseDto findCommunitiesES(String searchKeyword, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<CommunityDocument> communityDocuments = communityESRepository.findByTitleAndIsDeletedFalse(searchKeyword,
			pageable);

		CommunityListResponseDto responseDto = new CommunityListResponseDto(communityDocuments.stream()
			.map(CommunityReadResponseDto::from)
			.toList());

		if (communityDocuments.getTotalPages() <= page) {
			throw new PageOutOfBoundsException(PAGE_OUT_OF_BOUNDS);
		}

		if (responseDto.getCommunityList().isEmpty()) {
			throw new NotFoundException(COMMUNITY_NOT_FOUND);
		}

		return responseDto;
	}

	@Override
	public CommunityResponseDto findCommunityES(String communityId) {
		CommunityDocument communityDocument = communityESRepository.findByIdOrElseThrow(communityId);
		return CommunityResponseDto.from(communityDocument);
	}

	@Override
	public CommunityResponseDto updateCommunityES(Long userId, CommunityUpdateRequestDto dto) {
		CommunityEntity community = communityRepository.findByIdOrElseThrow(dto.getCommunityId());
		CommunityDocument communityDocument = communityESRepository.findByIdOrElseThrow(
			String.valueOf(dto.getCommunityId()));
		checkAuth(community, userId);
		checkCommunityIsDeleted(community);

		if (communityDocument.getIsDeleted()) {
			throw new DuplicateException(COMMUNITY_ISDELETED);
		}

		if (Objects.nonNull(dto.getTitle())) {
			community.updateTitle(dto.getTitle());
			communityDocument.updateTitle(dto.getTitle());
		}
		if (Objects.nonNull(dto.getContent())) {
			community.updateContent(dto.getContent());
			communityDocument.updateContent(dto.getContent());
		}

		communityESRepository.save(communityDocument);

		return CommunityResponseDto.from(communityDocument);
	}

	@Override
	public void deleteCommunityES(Long userId, Long communityId) {
		CommunityEntity community = communityRepository.findByIdOrElseThrow(communityId);
		CommunityDocument communityDocument = communityESRepository.findByIdOrElseThrow(String.valueOf(communityId));
		checkAuth(community, userId);
		checkCommunityIsDeleted(community);

		community.deleteCommunity();
		communityDocument.updateIsDeleted(true);
		communityESRepository.save(communityDocument);
	}

	@Override
	public CommunityResponseDto saveCommunityRedis(Long userId, CommnunityCreateRequestDto dto) {
		UserEntity user = userRepository.findByIdOrElseThrow(userId);
		GameEntity game = gameRepository.findByIdOrElseThrow(dto.getGameId());
		checkGameIsDeleted(game);
		CommunityEntity community = communityRepository.save(
			new CommunityEntity(dto.getTitle(), dto.getContent(), user, game));

		String redisKey = COMMUNITY_CACHE_KEY + community.getId();

		Map<String, Object> communityMap = Map.of(
			"communityId", community.getId(),
			"title", community.getTitle(),
			"userEmail", user.getEmail(),
			"gameName", game.getName()
		);
		redisTemplate.opsForHash().putAll(redisKey, communityMap);
		redisTemplate.opsForZSet().add(COMMUNITY_ZSET_KEY, redisKey, 0);
		return CommunityResponseDto.of(community, user, game);
	}

	@Override
	public CommunityListResponseDto findCommunityRedis(String searchKeyword, int page, int size) {
		Set<String> keys = redisTemplate.keys(COMMUNITY_CACHE_KEY + "*");
		List<String> matchedArticles = new ArrayList<>();

		if (keys == null) {
			throw new NotFoundException(COMMUNITY_NOT_FOUND);
		}

		for (String key : keys) {
			Map<String, Object> community = redisTemplate.opsForHash().entries(key);
			String title = community.get("title").toString();

			if (title.toLowerCase().contains(searchKeyword.toLowerCase())) {
				matchedArticles.add(key);
			}
		}

		if (matchedArticles.isEmpty()) {
			throw new NotFoundException(COMMUNITY_NOT_FOUND);
		}

		if (page * size >= matchedArticles.size()) {
			throw new PageOutOfBoundsException(PAGE_OUT_OF_BOUNDS);
		}

		matchedArticles.sort((a, b) -> {
			Double scoreA = redisTemplate.opsForZSet().score(COMMUNITY_ZSET_KEY, a);
			Double scoreB = redisTemplate.opsForZSet().score(COMMUNITY_ZSET_KEY, b);

			scoreA = (scoreA != null) ? scoreA : 0.0;
			scoreB = (scoreB != null) ? scoreB : 0.0;

			return Double.compare(scoreB, scoreA);
		});

		List<CommunityReadResponseDto> results = new ArrayList<>();
		for (String key : matchedArticles.stream().skip((long)page * size).limit(size).toList()) {
			Map<String, Object> result = redisTemplate.opsForHash().entries(key);
			results.add(new CommunityReadResponseDto(
				String.valueOf(result.get("communityId")),
				(String)result.get("title"),
				(String)result.get("userEmail"),
				(String)result.get("gameName")));
			incrementViews(key);
		}

		return new CommunityListResponseDto(results);
	}

	@Override
	public CommunityResponseDto updateCommunityRedis(Long userId, CommunityUpdateRequestDto dto) {
		CommunityEntity community = communityRepository.findByIdOrElseThrow(dto.getCommunityId());

		String key = COMMUNITY_CACHE_KEY + community.getId();
		Map<String, Object> communityMap = redisTemplate.opsForHash().entries(key);
		checkAuth(community, userId);
		checkCommunityIsDeleted(community);

		if (Objects.nonNull(dto.getTitle())) {
			community.updateTitle(dto.getTitle());
			communityMap.put("title", dto.getTitle());
		}
		if (Objects.nonNull(dto.getContent())) {
			community.updateContent(dto.getContent());
		}

		redisTemplate.opsForHash().putAll(key, communityMap);
		communityRepository.save(community);

		return CommunityResponseDto.from(community);
	}

	@Override
	public void deleteCommunityRedis(Long userId, Long communityId) {
		String key = COMMUNITY_CACHE_KEY + communityId;
		CommunityEntity community = communityRepository.findByIdOrElseThrow(communityId);
		checkAuth(community, userId);
		checkCommunityIsDeleted(community);

		redisTemplate.delete(key);
		community.deleteCommunity();

	}

	public void incrementViews(String communityKey) {
		redisTemplate.opsForZSet().incrementScore(COMMUNITY_ZSET_KEY, communityKey, 1);
	}

	private void checkGameIsDeleted(GameEntity game) {
		if (game.getIsDeleted()) {
			throw new DuplicateException(GAME_ISDELETED);
		}
	}

	private void checkCommunityIsDeleted(CommunityEntity community) {
		if (community.getIsDeleted()) {
			throw new DuplicateException(COMMUNITY_ISDELETED);
		}
	}

	private void checkAuth(CommunityEntity community, Long userId) {
		UserEntity user = userRepository.findByIdOrElseThrow(userId);
		if (!community.getUser().getId().equals(userId) && !user.getRole().equals(ADMIN)) {
			throw new ForbiddenException(FORBIDDEN_ACCESS);
		}
	}
}

package com.sparta.domain.community.service;

import static com.sparta.domain.community.enums.UserRole.ADMIN;
import static com.sparta.exception.common.ErrorCode.COMMUNITY_ISDELETED;
import static com.sparta.exception.common.ErrorCode.COMMUNITY_NOT_FOUND;
import static com.sparta.exception.common.ErrorCode.FORBIDDEN_ACCESS;
import static com.sparta.exception.common.ErrorCode.GAME_ISDELETED;
import static com.sparta.exception.common.ErrorCode.PAGE_OUT_OF_BOUNDS;

import com.sparta.domain.community.client.EntityServiceClient;
import com.sparta.domain.community.document.CommunityDocument;
import com.sparta.domain.community.dto.request.CommnunityCreateRequestDto;
import com.sparta.domain.community.dto.request.CommunityUpdateRequestDto;
import com.sparta.domain.community.dto.response.CommunityListResponseDto;
import com.sparta.domain.community.dto.response.CommunityReadResponseDto;
import com.sparta.domain.community.dto.response.CommunityResponseDto;
import com.sparta.domain.community.dto.response.GameResponseDto;
import com.sparta.domain.community.dto.response.UserResponseDto;
import com.sparta.domain.community.entity.CommunityEntity;
import com.sparta.domain.community.repository.CommunityRepository;
import com.sparta.domain.community.repositoryES.CommunityESRepository;
import com.sparta.exception.common.DuplicateException;
import com.sparta.exception.common.ForbiddenException;
import com.sparta.exception.common.NotFoundException;
import com.sparta.exception.common.PageOutOfBoundsException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class CommunityServiceImpl implements CommunityService {
	private final EntityServiceClient entityServiceClient;
	private final CommunityRepository communityRepository;

	private final CommunityESRepository communityESRepository;
	private final RedisTemplate<String, Object> redisTemplate;

	private final String COMMUNITY_CACHE_KEY = "community:";
	private final String COMMUNITY_ZSET_KEY = "community_view";

	@Override
	public CommunityResponseDto saveCommunity(Long userId, CommnunityCreateRequestDto dto) {
		UserResponseDto user = entityServiceClient.findUserById(userId);
		GameResponseDto game = entityServiceClient.findGameById(dto.getGameId());
		checkGameIsDeleted(game);

		CommunityEntity community = communityRepository.save(
			new CommunityEntity(dto.getTitle(), dto.getContent(), user.getId(), game.getId()));

		return CommunityResponseDto.of(community, user, game);
	}

	@Transactional(readOnly = true)
	@Override
	public CommunityListResponseDto findAll(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<CommunityEntity> communityPage = communityRepository.findAllByIsDeletedFalse(pageable);

		List<CommunityEntity> communityEntityList = communityPage.getContent();
		List<Long> userIdList = new ArrayList<>();
		List<Long> gameIdList = new ArrayList<>();
		for(CommunityEntity communityEntity :communityEntityList){
			userIdList.add(communityEntity.getId());
			gameIdList.add(communityEntity.getGameId());
		}

		Map<Long,GameResponseDto> allGames = entityServiceClient.findAllGames(gameIdList).stream().collect(Collectors.
			toMap(game -> game.getId(), game -> game));
		Map<Long,UserResponseDto> allUsers = entityServiceClient.findAllUsers(userIdList).stream().collect(Collectors.
			toMap(user -> user.getId(), user -> user));


		CommunityListResponseDto responseDto = new CommunityListResponseDto(
			communityPage.stream()
				.map(community -> CommunityReadResponseDto.of(community, allUsers.get(community.getUserId()), allGames.get(community.getGameId())))
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
		return CommunityResponseDto.from(community,
			entityServiceClient.findUserById(community.getUserId()),
			entityServiceClient.findGameById(community.getGameId()));
	}

	@Override
	public void delete(Long userId, Long communityId) {
		CommunityEntity community = communityRepository.findByIdOrElseThrow(communityId);
		checkAuth(community, userId);
		checkCommunityIsDeleted(community);

		community.deleteCommunity();
	}

	// community 생성(elasticSearch 사용)
	@Override
	public CommunityResponseDto saveCommunityES(Long userId, CommnunityCreateRequestDto dto) {

		UserResponseDto user = entityServiceClient.findUserById(userId);
		GameResponseDto game = entityServiceClient.findGameById(dto.getGameId());
		CommunityEntity community = communityRepository.save(
			new CommunityEntity(dto.getTitle(), dto.getContent(), user.getId(), game.getId()));
		CommunityDocument communityDocument = communityESRepository.save(CommunityDocument.from(community, user, game));

		return CommunityResponseDto.from(communityDocument);
	}

	// community 목록 검색(elasticSearch 사용)
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

	// community 단건 조회(elasticSearch 사용)
	@Override
	public CommunityResponseDto findCommunityES(String communityId) {
		CommunityDocument communityDocument = communityESRepository.findByIdOrElseThrow(communityId);
		return CommunityResponseDto.from(communityDocument);
	}

	// community 수정(elasticSearch 사용)
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

	// community 삭제(elasticSearch 사용)
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

	/**
	 * community 생성(redis활용)
	 * @param userId 사용자 Id
	 * @param dto title, content, gameId
	 * @return CommunityResponseDto
	 */
	@Override
	public CommunityResponseDto saveCommunityRedis(Long userId, CommnunityCreateRequestDto dto) {
		UserResponseDto user = entityServiceClient.findUserById(userId);
		GameResponseDto game = entityServiceClient.findGameById(dto.getGameId());
		checkGameIsDeleted(game);
		CommunityEntity community = communityRepository.save(
			new CommunityEntity(dto.getTitle(), dto.getContent(), user.getId(), game.getId()));

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

	/**
	 * community 검색(redis 활용)
	 * @param searchKeyword 검색할 단어
	 * @param page 페이지 수
	 * @param size 한 페이지에 표시할 데이터 수
	 * @return CommunityListResponseDto
	 */
	@Override
	public CommunityListResponseDto findCommunityRedis(String searchKeyword, int page, int size) {
		Set<String> keys = redisTemplate.keys(COMMUNITY_CACHE_KEY + "*");
		List<String> matchedCommunities = new ArrayList<>();

		if (keys == null) {
			throw new NotFoundException(COMMUNITY_NOT_FOUND);
		}

		for (String key : keys) {
			Map<String, Object> community = redisTemplate.<String, Object>opsForHash().entries(key);
			String title = community.get("title").toString();

			if (title.toLowerCase().contains(searchKeyword.toLowerCase())) {
				matchedCommunities.add(key);
			}
		}

		if (matchedCommunities.isEmpty()) {
			throw new NotFoundException(COMMUNITY_NOT_FOUND);
		}

		if (page * size >= matchedCommunities.size()) {
			throw new PageOutOfBoundsException(PAGE_OUT_OF_BOUNDS);
		}

		matchedCommunities.sort((a, b) -> {
			Double scoreA = redisTemplate.opsForZSet().score(COMMUNITY_ZSET_KEY, a);
			Double scoreB = redisTemplate.opsForZSet().score(COMMUNITY_ZSET_KEY, b);

			scoreA = (scoreA != null) ? scoreA : 0.0;
			scoreB = (scoreB != null) ? scoreB : 0.0;

			return Double.compare(scoreB, scoreA);
		});

		List<CommunityReadResponseDto> results = new ArrayList<>();
		for (String key : matchedCommunities.stream().skip((long)page * size).limit(size).toList()) {
			Map<String, Object> result = redisTemplate.<String, Object>opsForHash().entries(key);
			results.add(new CommunityReadResponseDto(
				String.valueOf(result.get("communityId")),
				(String)result.get("title"),
				(String)result.get("userEmail"),
				(String)result.get("gameName")));
			incrementViews(key);
		}

		return new CommunityListResponseDto(results);
	}

	/**
	 * community 수정(redis 활용)
	 * @param userId 사용자 Id
	 * @param dto communityId, title, content
	 * @return CommunityResponseDto
	 */
	@Override
	public CommunityResponseDto updateCommunityRedis(Long userId, CommunityUpdateRequestDto dto) {
		CommunityEntity community = communityRepository.findByIdOrElseThrow(dto.getCommunityId());

		String key = COMMUNITY_CACHE_KEY + community.getId();
		Map<String, Object> communityMap = redisTemplate.<String, Object>opsForHash().entries(key);
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

		return CommunityResponseDto.from(community,
			entityServiceClient.findUserById(community.getUserId()),
			entityServiceClient.findGameById(community.getGameId()));
	}

	/**
	 * community 삭제(redis 활용)
	 * @param userId 사용자 Id
	 * @param communityId community Id
	 */
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

	private void checkGameIsDeleted(GameResponseDto game) {
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
		UserResponseDto user = entityServiceClient.findUserById(userId);
		if (!community.getUserId().equals(userId) && !user.getRole().equals(ADMIN)) {
			throw new ForbiddenException(FORBIDDEN_ACCESS);
		}
	}
}

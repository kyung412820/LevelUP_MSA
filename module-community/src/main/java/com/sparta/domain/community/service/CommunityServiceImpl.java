package com.sparta.domain.community.service;

import com.sparta.domain.community.document.CommunityDocument;
import com.sparta.domain.community.dto.request.CommnunityCreateRequestDto;
import com.sparta.domain.community.dto.request.CommunityUpdateRequestDto;
import com.sparta.domain.community.dto.response.CommunityListResponseDto;
import com.sparta.domain.community.dto.response.CommunityReadResponseDto;
import com.sparta.domain.community.dto.response.CommunityResponseDto;
import com.sparta.domain.community.entity.CommunityEntity;
import com.sparta.domain.community.repository.CommunityRepository;
import com.sparta.domain.community.repositoryES.CommunityESRepository;
import com.sparta.domain.game.entity.GameEntity;
import com.sparta.domain.game.repository.GameRepository;
import com.sparta.domain.user.entity.UserEntity;
import com.sparta.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.sparta.exception.common.ErrorCode.*;
import static com.sparta.utill.UserRole.*;

@Transactional
@RequiredArgsConstructor
@Service
public class CommunityServiceImpl implements CommunityService {
	private final UserRepository userRepository;
	private final CommunityRepository communityRepository;
	private final GameRepository gameRepository;

	private final CommunityESRepository communityESRepository;
	private final RedisTemplate<String, Object> redisTemplate;

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

	// community 생성(elasticSearch 사용)
	@Override
	public CommunityResponseDto saveCommunityES(Long userId, CommnunityCreateRequestDto dto) {
		UserEntity user = userRepository.findByIdOrElseThrow(userId);
		GameEntity game = gameRepository.findByIdOrElseThrow(dto.getGameId());
		CommunityEntity community = communityRepository.save(
			new CommunityEntity(dto.getTitle(), dto.getContent(), user, game));
		CommunityDocument communityDocument = communityESRepository.save(CommunityDocument.from(community));

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

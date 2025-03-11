package com.sparta.domain.community.repository;

import com.sparta.domain.community.entity.CommunityEntity;

import com.sparta.exception.common.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import static com.sparta.exception.common.ErrorCode.*;

public interface CommunityRepository extends JpaRepository<CommunityEntity, Long> {
	Page<CommunityEntity> findAllByIsDeletedFalse(Pageable pageable);

	default CommunityEntity findByIdOrElseThrow(Long id) {
		return findById(id).orElseThrow(() -> new NotFoundException(COMMUNITY_NOT_FOUND));
	}

	default CommunityEntity findByuserIdOrElseThrow(String userId){
		return findById(Long.valueOf(userId)).orElseThrow(() -> new NotFoundException(COMMUNITY_NOT_FOUND));
	}

	default CommunityEntity findBygameIdOrElseThrow(String gameId){
		return findById(Long.valueOf(gameId)).orElseThrow(() -> new NotFoundException(COMMUNITY_NOT_FOUND));
	}

	List<CommunityEntity> findAllByuserId(Long userId);

	List<CommunityEntity> findAllBygameId(Long gameId);
}

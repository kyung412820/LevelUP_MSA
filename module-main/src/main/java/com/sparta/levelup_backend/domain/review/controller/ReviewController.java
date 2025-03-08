package com.sparta.levelup_backend.domain.review.controller;

import static com.sparta.levelup_backend.common.ApiResMessage.*;
import static com.sparta.levelup_backend.common.ApiResponse.*;
import static org.springframework.http.HttpStatus.*;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.levelup_backend.common.ApiResponse;
import com.sparta.levelup_backend.domain.review.dto.request.ReviewRequestDto;
import com.sparta.levelup_backend.domain.review.dto.request.UserAuthenticationRequestDto;
import com.sparta.levelup_backend.domain.review.dto.response.ReviewResponseDto;
import com.sparta.levelup_backend.domain.review.service.ReviewService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Review 생성 API
     *
     * @param dto contents(리뷰 내용), startScore(별점)
     */
    @PostMapping("/products/{productId}/reviews")
    public ApiResponse<ReviewResponseDto> saveReview(
        HttpServletRequest request,
        @Valid @RequestBody ReviewRequestDto dto,
        @PathVariable Long productId
    ) {
        String encodedAuth = request.getHeader("UserAuthentication");
        UserAuthenticationRequestDto authRequest = UserAuthenticationRequestDto.from(encodedAuth);

        ReviewResponseDto result = reviewService.saveReview(dto, authRequest.getId(), productId);
        return success(OK ,REVIEW_SUCCESS, result);
    }

    /**
     * Review 삭제 API
     */
    @DeleteMapping("/admin/products/{productId}/reviews/{reviewId}")
    public ApiResponse<Void> deleteReview(
        HttpServletRequest request,
        @PathVariable Long productId,
        @PathVariable Long reviewId
    ) {
        String encodedAuth = request.getHeader("UserAuthentication");
        UserAuthenticationRequestDto authRequest = UserAuthenticationRequestDto.from(encodedAuth);

        reviewService.deleteReview(authRequest.getId(), productId, reviewId);
        return success(OK, REVIEW_DELETE);
    }

    /**
     * Review 목록 조회 API
     *
     * @param pageable 무한스크롤 구조로 size만 받음
     * @return
     */
    @GetMapping("/products/{productId}/reviews")
    public ApiResponse<Slice<ReviewResponseDto>> findReviews(
        @PathVariable Long productId,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        return success(OK, REVIEW_LIST_SUCCESS, reviewService.findReviews(productId, pageable));
    }
}

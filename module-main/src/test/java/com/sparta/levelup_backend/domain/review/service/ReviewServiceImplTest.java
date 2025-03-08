package com.sparta.levelup_backend.domain.review.service;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.sparta.levelup_backend.domain.order.entity.OrderEntity;
import com.sparta.levelup_backend.domain.order.repository.OrderRepository;
import com.sparta.levelup_backend.domain.product.entity.ProductEntity;
import com.sparta.levelup_backend.domain.product.repository.ProductRepository;
import com.sparta.levelup_backend.domain.review.client.UserServiceClient;
import com.sparta.levelup_backend.domain.review.dto.request.ReviewRequestDto;
import com.sparta.levelup_backend.domain.review.dto.response.UserResponseDto;
import com.sparta.levelup_backend.domain.review.entity.ReviewEntity;
import com.sparta.levelup_backend.domain.review.repository.ReviewRepository;
import com.sparta.levelup_backend.exception.common.BusinessException;
import com.sparta.levelup_backend.exception.common.DuplicateException;
import com.sparta.levelup_backend.exception.common.ErrorCode;
import com.sparta.levelup_backend.exception.common.ForbiddenException;
import com.sparta.levelup_backend.utill.OrderStatus;
import com.sparta.levelup_backend.utill.UserRole;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    // 테스트별 공통으로 사용할 데이터
    private Long userId = 1L;
    private Long productId = 1L;
    private Long reviewId = 1L;
    private UserResponseDto normalUser;
    private UserResponseDto adminUser;
    private ProductEntity product;
    private ReviewEntity review;
    private OrderEntity order;

    @BeforeEach
    void setUp() {
        normalUser = UserResponseDto.builder()
            .id(userId)
            .role(UserRole.USER)
            .build();

        adminUser = UserResponseDto.builder()
            .id(userId)
            .role(UserRole.ADMIN)
            .build();

        product = ProductEntity.builder()
            .id(productId)
            .build();

    }

    @Test
    void 리뷰_관리자_권한이_없을때_예외발생() {
        //given
        review = ReviewEntity.builder()
            .id(reviewId)
            .starScore(5)
            .userId(normalUser.getId())
            .product(product)
            .order(order)
            .build();

        //when
        when(userServiceClient.findUserById(userId)).thenReturn(normalUser);

        //then
        assertThatThrownBy(() -> {
                reviewService.deleteReview(userId, productId, reviewId);
            }).isInstanceOf(BusinessException.class)
            .hasMessageContaining(ErrorCode.FORBIDDEN_ACCESS.getMessage());

    }

    @Test
    void 리뷰와_상품의_매칭_확인() {
        //given
        Long otherProductId = 2L;

        ProductEntity product2 = ProductEntity.builder()
            .id(otherProductId)
            .build();

        review = ReviewEntity.builder()
            .id(reviewId)
            .starScore(5)
            .userId(normalUser.getId())
            .product(product2)
            .order(order)
            .build();

        //when
        when(userServiceClient.findUserById(userId)).thenReturn(adminUser);
        when(reviewRepository.findByIdOrElseThrow(reviewId)).thenReturn(review);

        //then
        assertThatThrownBy(() -> {
            reviewService.deleteReview(userId, productId, reviewId);
        }).isInstanceOf(BusinessException.class)
            .hasMessageContaining(ErrorCode.MISMATCH_REVIEW_PRODUCT.getMessage());
    }

    @Test
    @Transactional
    void 리뷰_삭제_테스트() {
        //given
        review = ReviewEntity.builder()
            .id(reviewId)
            .starScore(5)
            .userId(normalUser.getId())
            .product(product)
            .order(order)
            .build();

        //when
        when(userServiceClient.findUserById(userId)).thenReturn(adminUser);
        when(reviewRepository.findByIdOrElseThrow(reviewId)).thenReturn(review);
        reviewService.deleteReview(userId, productId, reviewId);

        //then
        assertThat(review.getIsDeleted()).isTrue();
    }

    @Test
    void 거래_완료된_주문이_없을때_예외발생() {
        //when
        when(orderRepository.existsByUserIdAndProductIdAndStatus(userId, productId, OrderStatus.COMPLETED)).thenReturn(false);

        //then
        assertThatThrownBy(() -> {
            reviewService.saveReview(new ReviewRequestDto("리뷰 테스트", 5), userId, productId);
        }).isInstanceOf(ForbiddenException.class)
            .hasMessageContaining(ErrorCode.COMPLETED_ORDER_REQUIRED.getMessage());
    }

    @Test
    void 이미_작성된_리뷰가_있을때_예외발생() {
        //when
        when(orderRepository.existsByUserIdAndProductIdAndStatus(userId, productId, OrderStatus.COMPLETED)).thenReturn(true);
        when(reviewRepository.existsByUserIdAndProductId(userId, productId)).thenReturn(true);

        //then
        assertThatThrownBy(() -> {
            reviewService.saveReview(new ReviewRequestDto("리뷰 테스트", 5), userId, productId);
        }).isInstanceOf(DuplicateException.class)
            .hasMessageContaining(ErrorCode.DUPLICATE_REVIEW.getMessage());

    }

    @Test
    void 이미_삭제된_리뷰_삭제시도시_예외발생() {
        //given
        review = ReviewEntity.builder()
        .id(reviewId)
        .starScore(5)
        .userId(adminUser.getId())
        .product(product)
        .order(order)
        .build();

        review.deleteReview();

        //when
        when(userServiceClient.findUserById(userId)).thenReturn(adminUser);
        when(reviewRepository.findByIdOrElseThrow(reviewId)).thenReturn(review);

        //then
        assertThatThrownBy(() -> {
            reviewService.deleteReview(userId, productId, reviewId);
        }).isInstanceOf(DuplicateException.class)
            .hasMessageContaining(ErrorCode.REVIEW_ISDELETED.getMessage());
    }

}
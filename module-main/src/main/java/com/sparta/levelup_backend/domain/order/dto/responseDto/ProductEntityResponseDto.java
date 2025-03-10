package com.sparta.levelup_backend.domain.order.dto.responseDto;

import java.time.LocalDateTime;

import com.sparta.levelup_backend.domain.product.entity.ProductEntity;
import com.sparta.levelup_backend.utill.ProductStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProductEntityResponseDto {
	private Long id;
	private Long userId;
	private Long gameId;
	private String productName;
	private String contents;
	private Long price;
	private Integer amount;
	private ProductStatus status;
	private String imgUrl;
	private Boolean isDeleted = false;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public ProductEntityResponseDto(Long id, Long userId, Long gameId, String productName, String contents, Long price, Integer amount, ProductStatus status, String imgUrl, Boolean isDeleted, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.id = id;
		this.userId = userId;
		this.gameId = gameId;
		this.productName = productName;
		this.contents = contents;
		this.price = price;
		this.amount = amount;
		this.status = status;
		this.imgUrl = imgUrl;
		this.isDeleted = isDeleted;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public ProductEntityResponseDto(ProductEntity product) {
		this.id = product.getId();
		this.userId = product.getUserId();
		this.productName = product.getProductName();
		this.contents = product.getContents();
		this.price = product.getPrice();
		this.amount = product.getAmount();
		this.status = product.getStatus();
		this.imgUrl = product.getImgUrl();
		this.isDeleted = product.getIsDeleted();
		this.createdAt = product.getCreatedAt();
		this.updatedAt = product.getUpdatedAt();
	}
}

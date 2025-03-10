// package com.sparta.domain.community.dto.response;
//
// import com.sparta.utill.ProductStatus;
// import java.time.LocalDateTime;
// import lombok.Builder;
// import lombok.Getter;
// import lombok.Setter;
//
// @Getter
// @Setter
// @Builder
// public class ProductResponseDto {
// 	private Long id;
// 	private UserEntityResponseDto user;
// 	private GameEntityResponseDto game;
// 	private String productName;
// 	private String contents;
// 	private Long price;
// 	private Integer amount;
// 	private ProductStatus status;
// 	private String imgUrl;
// 	private Boolean isDeleted = false;
// 	private LocalDateTime createdAt;
// 	private LocalDateTime updatedAt;
//
// 	public ProductResponseDto(Long id, UserEntityResponseDto user, GameEntityResponseDto game, String productName, String contents, Long price, Integer amount, ProductStatus status, String imgUrl, Boolean isDeleted, LocalDateTime createdAt, LocalDateTime updatedAt) {
// 		this.id = id;
// 		this.user = user;
// 		this.game = game;
// 		this.productName = productName;
// 		this.contents = contents;
// 		this.price = price;
// 		this.amount = amount;
// 		this.status = status;
// 		this.imgUrl = imgUrl;
// 		this.isDeleted = isDeleted;
// 		this.createdAt = createdAt;
// 		this.updatedAt = updatedAt;
// 	}
// }

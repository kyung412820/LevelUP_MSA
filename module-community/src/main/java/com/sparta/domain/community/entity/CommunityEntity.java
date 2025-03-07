package com.sparta.domain.community.entity;

import com.sparta.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "community")
public class CommunityEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String content;

	@Column(nullable = false)
	private Integer recommendation;

	private Long userId;

	private Long gameId;

	public void updateTitle(String title) {
		this.title = title;
	}

	public void updateContent(String content) {
		this.content = content;
	}

	public void deleteCommunity() {
		this.delete();
	}

	public CommunityEntity(String title, String content, Long userId, Long gameId) {
		this.title = title;
		this.content = content;
		this.userId = userId;
		this.gameId = gameId;
		recommendation = 0;
	}

}

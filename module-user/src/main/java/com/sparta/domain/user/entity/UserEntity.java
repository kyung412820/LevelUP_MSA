package com.sparta.domain.user.entity;

import com.sparta.common.entity.BaseEntity;
import com.sparta.domain.user.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class UserEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String email;

	private String nickName;

	private String imgUrl;

	private String password;

	@Enumerated(EnumType.STRING)
	private UserRole role;

	private String phoneNumber;

	private String provider;

    private String customerKey;

    public void updateEmail(String email){
        this.email = email;
    }

	public void updateImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public void updateNickName(String nickName) {
		this.nickName = nickName;
	}

	public void updatePhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void changePassword(String newPassword) {
		this.password = newPassword;
	}

	public void updateProvider(String provider) {
		this.provider = provider;
	}

    public String generateCustomerKey() {
        return "UUID-" + this.id + "-" + UUID.randomUUID().toString();
    }


}

package com.sparta.levelup_backend.domain.user.entity;

import com.sparta.levelup_backend.common.entity.BaseEntity;
import com.sparta.levelup_backend.utill.UserRole;
import jakarta.persistence.*;
import jdk.jshell.Snippet;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor( access = AccessLevel.PROTECTED)
@Table( name = "user")
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String nickName;

    @Column(nullable = true)
    private String imgUrl;

    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private String phoneNumber;

    public void updateEmail(String email){
        this.email = email;
    }

    public void updateImgUrl(String imgUrl){
        this.imgUrl = imgUrl;
    }

    public void updateNickName(String nickName){
        this.nickName = nickName;
    }

    public void updatePhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }
}

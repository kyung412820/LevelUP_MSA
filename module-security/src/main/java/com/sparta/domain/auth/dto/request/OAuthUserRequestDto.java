package com.sparta.domain.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder  // ✅ Lombok 빌더 패턴 추가
public class OAuthUserRequestDto {

	@JsonProperty(value = "email")
	@Pattern(regexp = "^[\\w!#$%&'*+/=?`{|}~^.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "이메일 형식이 올바르지 않습니다.")
	@NotBlank
	private String email;

	@JsonProperty(value = "nickName")
	@NotBlank
	private String nickName;

	@JsonProperty(value = "phoneNumber")
	@Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
	@NotBlank
	private String phoneNumber;

	// ✅ 추가 필드: role & provider
	@JsonProperty(value = "role")
	private String role;

	@JsonProperty(value = "provider")
	private String provider;
}

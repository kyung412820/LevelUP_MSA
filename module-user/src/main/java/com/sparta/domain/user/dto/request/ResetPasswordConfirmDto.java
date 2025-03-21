package com.sparta.domain.user.dto.request;



import static com.sparta.domain.user.dto.UserValidMessage.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.sparta.config.annotaion.FormToJson;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@FormToJson
public class ResetPasswordConfirmDto {

	@NotBlank
	@JsonProperty(value = "resetCode")
	String resetCode;

	@JsonProperty(value = "newPassword")
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$",
		message = PASSWORD_NOT_VALID)
	@NotBlank
	String newPassword;

	@JsonProperty(value = "passwordConfirm")
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$",
		message = PASSWORD_NOT_VALID)
	@NotBlank
	String passwordConfirm;

	@JsonProperty(value = "email")
	@Pattern(regexp = "^[\\w!#$%&'*+/=?`{|}~^.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = EMAIL_NOT_VALID)
	private String email;

}

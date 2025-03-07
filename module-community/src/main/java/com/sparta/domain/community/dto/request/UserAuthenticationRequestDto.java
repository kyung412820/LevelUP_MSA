package com.sparta.dto.requestDto;

import static com.sparta.exception.common.ErrorCode.INVALID_JSON_FORMAT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.exception.common.BadRequestException;
import java.util.Base64;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserAuthenticationRequestDto {

    private Long id;

    private String email;

    private  String nickName;

    private String role;

    public static UserAuthenticationRequestDto from(String encodedAuth) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            byte[] bytes = Base64.getUrlDecoder().decode(encodedAuth);
            String authUser = new String(bytes);

            return mapper.readValue(authUser, UserAuthenticationRequestDto.class);
        }catch(JsonProcessingException e){
            throw new BadRequestException(INVALID_JSON_FORMAT);
        }
    }
}

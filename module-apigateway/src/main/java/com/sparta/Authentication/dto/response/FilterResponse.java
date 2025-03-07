package com.sparta.Authentication.dto.response;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class FilterResponse {

	public Mono<Void> responseErrorMsg(ServerHttpResponse response, HttpStatus statusCode, String code,
		String msg) {
		try {

			ObjectMapper mapper = new ObjectMapper();
			response.setStatusCode(statusCode);
			response.getHeaders().setContentType(APPLICATION_JSON);
//			response.getWriter().write("{\n"
//				+ "    \"errorCode\": \"" + code + "\",\n"
//				+ "    \"detail\": \"" + msg + "\",\n"
//				+ "    \"errorMessage\": \"" + msg + "\"\n"
//				+ "}");
//			response.getWriter().flush();
			Map<String, String> body = Map.of("errorCode", code, "detail", msg, "errorMessage",
				msg);
			DataBuffer buffer = response.bufferFactory().wrap(mapper.writeValueAsBytes(body));

			return response.writeWith(Mono.just((buffer)));

		} catch (IOException e) {
			throw new RuntimeException(e);
		}


	}
}

package com.sparta.Filter;

import static org.springframework.util.StringUtils.hasText;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.Authentication.dto.response.FilterResponse;
import com.sparta.Authentication.dto.response.UserAuthenticationResponseDto;
import com.sparta.Authentication.exception.common.ErrorCode;
import com.sparta.Authentication.exception.common.NotFoundException;
import com.sparta.Authentication.util.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Base64;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Component
public class JwtFilter extends AbstractGatewayFilterFactory<JwtFilter.Config> {

	private final JwtUtils jwtUtils;
	private final FilterResponse filterResponse;

	public JwtFilter(JwtUtils jwtUtils, FilterResponse filterResponse) {
		super(Config.class);
		this.jwtUtils = jwtUtils;
		this.filterResponse = filterResponse;
	}

	public static class Config {

	}


	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();
			ServerHttpResponse response = exchange.getResponse();

			ServerWebExchange build;

			try {
				// 헤더에서 토큰 추출
				String accessToken = extractAccessToken(request);
				String refreshToken = extractCookieToken(request, "refreshToken");

				// 토큰이 전부 없을 시
				if (!hasText(accessToken) && !hasText(refreshToken)) {
					throw new NotFoundException(ErrorCode.TOKEN_NOT_FOUND);
				}

				reissueExpiredTokens(accessToken, refreshToken, response);

				Claims accessTokenClaims = jwtUtils.extractClaims(accessToken);
				UserAuthenticationResponseDto authentication = createAuthentication(
					accessTokenClaims);

				ObjectMapper object = new ObjectMapper();
				String encodedAuth = Base64.getEncoder().encodeToString(object.writeValueAsString(authentication).getBytes());

				request = request.mutate()
					.header("UserAuthentication", encodedAuth)
					.header("Role", authentication.getRole()).build();


				build = exchange.mutate().request(request).build();

			} catch (SecurityException | MalformedJwtException e) {
				return handleException(response, ErrorCode.INVALID_JWT_TOKEN);
			} catch (ExpiredJwtException e) {
				return handleException(response, ErrorCode.EXPIRED_JWT_TOKEN);
			} catch (UnsupportedJwtException e) {
				return handleException(response, ErrorCode.INVALID_FORMAT_TOKEN);
			} catch (NotFoundException e) {
				return handleException(response, ErrorCode.TOKEN_NOT_FOUND);
			} catch (Exception e) {
				return handleException(response, ErrorCode.INTERNAL_SERVER_ERROR);
			}

			return chain.filter(build);
		};

	}



	/**
	 * 쿠키에서 토큰 추출
	 */
	public String extractCookieToken(ServerHttpRequest request, String tokenName) {

		HttpCookie cookieHeader = request.getCookies().getFirst(tokenName);
		if (cookieHeader == null) {
			return null;
		} else {
			return cookieHeader.getValue();
		}
	}

	/**
	 * 헤더에서 토큰 추출
	 */
	private String extractAccessToken(ServerHttpRequest request) {
		String headerToken = request.getHeaders().getFirst("Authorization");
		if (headerToken != null && headerToken.startsWith("Bearer ")) {
			return headerToken.substring(7);
		}
		return extractCookieToken(request, "accessToken");
	}

	public boolean isTokenExpired(String token) {

		if (token == null || token.isBlank()) {
			throw new NotFoundException(ErrorCode.TOKEN_NOT_FOUND);
		}
		try {
			jwtUtils.extractClaims(token);
			return false;
		} catch (ExpiredJwtException e) {
			return true;
		}
	}

	/**
	 * Refresh 토큰 존재 여부 확인 후 재발급 처리 메서드
	 */
	private void reissueExpiredTokens(String accessToken, String refreshToken,
		ServerHttpResponse response) throws
		Exception {

		if (hasText(refreshToken)) {
			if (isTokenExpired(accessToken)) {
				String newAccessToken = jwtUtils.refresingToken(refreshToken);
				response.getHeaders().set("Authorization", "Bearer " + newAccessToken);
				response.getHeaders()
					.set("Set-Cookie", "accessToken=" + newAccessToken + "; Path=/;");
			} else if (isTokenExpired(refreshToken)) {
				String newRefreshToken = jwtUtils.refresingToken(accessToken);
				response.getHeaders()
					.set("Set-Cookie", "refreshToken=" + newRefreshToken + "; Path=/;");
			}
		}
	}

	// Authentication 객체 생성 메서드
	private UserAuthenticationResponseDto createAuthentication(Claims claims) {
		String email = claims.getSubject();
		String role = claims.get("role", String.class);
		Long id = Long.parseLong(claims.get("id", String.class));
		String nickName = claims.get("nickName", String.class);

		return new UserAuthenticationResponseDto(id, email, nickName, role);
	}

	// 공통 예외 처리
	private Mono<Void> handleException(ServerHttpResponse response, ErrorCode errorCode) {
		return filterResponse.responseErrorMsg(
			response,
			errorCode.getStatus(),
			errorCode.getCode(),
			errorCode.getMessage()
		);
	}
}



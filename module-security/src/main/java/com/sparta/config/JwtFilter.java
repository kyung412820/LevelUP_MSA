package com.sparta.config;

import static org.springframework.util.StringUtils.hasText;

import com.sparta.domain.auth.dto.response.UserEntityResponseDto;
import com.sparta.domain.auth.enums.UserRole;
import com.sparta.exception.common.ErrorCode;
import com.sparta.exception.common.NotFoundException;
import com.sparta.util.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtUtils jwtUtils;
	private final List<RequestMatcher> WHITE_LIST = Arrays.asList(
		new AntPathRequestMatcher("/v2/home"),
		new AntPathRequestMatcher("/v2/sign**"),
		new AntPathRequestMatcher("/v**/users/resetPassword**"),
		new AntPathRequestMatcher("/resetPassword**"),
		new AntPathRequestMatcher("/oauth2/authorization/naver"),
		new AntPathRequestMatcher("/v2/oauth2sign*"));
	private final OrRequestMatcher orRequestMatcher = new OrRequestMatcher(WHITE_LIST);
	private final FilterResponse filterResponse;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException, NotFoundException {

		if (orRequestMatcher.matches(request)) {
			filterChain.doFilter(request, response);
			return;
		}

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
			Authentication authentication = createAuthentication(accessTokenClaims);
			SecurityContextHolder.getContext().setAuthentication(authentication);

			filterChain.doFilter(request, response);

		} catch (SecurityException | MalformedJwtException e) {
			handleException(response, ErrorCode.INVALID_JWT_TOKEN);
		} catch (ExpiredJwtException e) {
			handleException(response, ErrorCode.EXPIRED_JWT_TOKEN);
		} catch (UnsupportedJwtException e) {
			handleException(response, ErrorCode.INVALID_FORMAT_TOKEN);
		} catch (NotFoundException e) {
			handleException(response, ErrorCode.TOKEN_NOT_FOUND);
		} catch (Exception e) {
			handleException(response, ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 쿠키에서 토큰 추출
	 */
	public String extractCookieToken(HttpServletRequest request, String tokenName) {
		String cookieHeader = request.getHeader("Cookie");
		if (cookieHeader == null) {
			return null;
		}
		String[] cookies = cookieHeader.split("; ");
		for (String c : cookies) {
			if (c.startsWith(tokenName + "=")) {
				return c.substring((tokenName + "=").length());
			}
		}
		return null;
	}

	/**
	 * 헤더에서 토큰 추출
	 */
	private String extractAccessToken(HttpServletRequest request) {
		String headerToken = request.getHeader("Authorization");
		if (headerToken != null && headerToken.startsWith("Bearer ")) {
			return headerToken.substring(7);
		}
		return extractCookieToken(request, "accessToken");
	}

	public boolean isTokenExpired(String token) throws Exception {

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
	private void reissueExpiredTokens(String accessToken, String refreshToken, HttpServletResponse response) throws
		Exception {

		if (hasText(refreshToken)) {
			if (isTokenExpired(accessToken)) {
				String newAccessToken = jwtUtils.refresingToken(refreshToken);
				response.addHeader("Authorization", "Bearer " + newAccessToken);
				response.addHeader("Set-Cookie", "accessToken=" + newAccessToken + "; Path=/;");
			} else if (isTokenExpired(refreshToken)) {
				String newRefreshToken = jwtUtils.refresingToken(accessToken);
				response.addHeader("Set-Cookie", "refreshToken=" + newRefreshToken + "; Path=/;");
			}
		}
	}

	// Authentication 객체 생성 메서드
	private Authentication createAuthentication(Claims claims) {
		String email = claims.getSubject();
		String role = claims.get("role", String.class);
		Long id = Long.parseLong(claims.get("id", String.class));
		String nickName = claims.get("nickName", String.class);

		UserEntityResponseDto tokenUser = UserEntityResponseDto.builder() //ssss
			.id(id)
			.email(email)
			.nickName(nickName)
			.role(UserRole.valueOf(role.substring(5)))
			.build();

		CustomUserDetails userDetails = new CustomUserDetails(tokenUser);
		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}

	// 공통 예외 처리
	private void handleException(HttpServletResponse response, ErrorCode errorCode) {
		filterResponse.responseErrorMsg(
			response,
			errorCode.getStatus().value(),
			errorCode.getCode(),
			errorCode.getMessage()
		);
	}


}

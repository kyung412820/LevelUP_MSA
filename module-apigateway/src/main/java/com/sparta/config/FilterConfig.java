package com.sparta.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sparta.Filter.JwtFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {


	@Bean
	public RouteLocator customRoutes(RouteLocatorBuilder builder, JwtFilter jwtFilter) {

		return builder.routes().route( security -> security.path("/v2/sign**").uri("lb://module-security") )
			.route(adminuser -> adminuser.path("/v2/admin/users/**").and()
				.not( intra -> intra.path("/v2/admin/users/intra/**"))
				.filters(filters -> filters.filter(jwtFilter.apply(new JwtFilter.Config())) ).uri("lb://module-user"))
			.route(user -> user.path("/v2/users/**").and()
				.not( intra -> intra.path("/v2/users/intra/**"))
				.filters(filters -> filters.filter(jwtFilter.apply(new JwtFilter.Config())) ).uri("lb://module-user"))
			.route(cummunity -> cummunity.path("/v2/community/**")
				.filters(filters -> filters.filter(jwtFilter.apply(new JwtFilter.Config())) ).uri("lb://module-community"))
			.route(game -> game.path("/v1/admin/games/**").and()
				.not( intra -> intra.path("/v1/admin/games/intra/**"))
				.filters(filters -> filters.filter(jwtFilter.apply(new JwtFilter.Config())) ).uri("lb://module-main")).build();


	}
}

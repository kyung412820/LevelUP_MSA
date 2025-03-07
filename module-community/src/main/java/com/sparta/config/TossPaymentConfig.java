package com.sparta.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;

@Configuration
@Getter
public class TossPaymentConfig {

    @Value("${toss.client.key}")
    private String clientKey;

    @Value("${toss.secret.key}")
    private String secretKey;

    @Value("${toss.success.Url}")
    private String successUrl;

    @Value("${toss.fail.Url}")
    private String failUrl;

    public static final String URL = "https://api.tosspayments.com/v1/payments";

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

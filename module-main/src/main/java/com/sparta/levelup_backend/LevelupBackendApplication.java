package com.sparta.levelup_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class LevelupBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LevelupBackendApplication.class, args);
    }

}

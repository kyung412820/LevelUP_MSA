package com.sparta;

import com.sparta.config.TestBean;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class SecurityApplication {

    private final TestBean testBean;

    @Autowired
    public SecurityApplication(TestBean testBean) {
        this.testBean = testBean;
    }

    @PostConstruct
    public void dependencyTest() {
        testBean.dependencyTest();
    }

    public static void main(String[] args) {
        SpringApplication.run(SecurityApplication.class, args);
    }
}

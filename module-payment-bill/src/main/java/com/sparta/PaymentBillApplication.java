package com.sparta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import com.sparta.config.TestBean;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableFeignClients
public class PaymentBillApplication {

	private final TestBean testBean;

	@Autowired
	public PaymentBillApplication(TestBean testBean) {
		this.testBean = testBean;
	}

	@PostConstruct
	public void dependencyTest() {
		testBean.dependencyTest();

	}
	public static void main(String[] args) {
		SpringApplication.run(PaymentBillApplication.class, args);
	}

}

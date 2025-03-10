package com.sparta.levelup_backend.domain.payment.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sparta.levelup_backend.exception.common.ErrorCode;
import com.sparta.levelup_backend.exception.common.PaymentException;


@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {


    private static final String ORDER_ID = "test-order-id";
    private static final String PAYMENT_KEY = "test-payment-Key";
    private static final long AMOUNT = 5000;

    private int attempt = 0;

    private void fakeHttpRequest() throws Exception {
        if (attempt < 2) {
            attempt++;
            throw new RuntimeException("Temporary Failure");
        }
    }

    @Test
    void 재시도_성공_테스트() throws Exception {
        // Given
        JSONObject requestJson = new JSONObject();
        requestJson.put("paymentKey", PAYMENT_KEY);
        requestJson.put("amount", String.valueOf(AMOUNT));
        requestJson.put("orderId", ORDER_ID);

        // when
        int maxRetries = 3;
        int retryCount = 0;
        boolean success = false;

        while (retryCount < maxRetries) {
            try {
                fakeHttpRequest();
                success = true;
                break;
            } catch (Exception e) {
                retryCount++;
                if (retryCount >= maxRetries) {
                    throw new PaymentException(ErrorCode.PAYMENT_FAILED_RETRY);
                }
            }
        }

        // Then
        assertTrue(success);
        assertEquals(2, attempt);
    }
}
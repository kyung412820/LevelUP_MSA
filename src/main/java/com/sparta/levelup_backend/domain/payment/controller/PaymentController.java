package com.sparta.levelup_backend.domain.payment.controller;

import com.sparta.levelup_backend.domain.bill.entity.BillEntity;
import com.sparta.levelup_backend.domain.bill.repository.BillRepository;
import com.sparta.levelup_backend.domain.bill.service.BillServiceImplV2;
import com.sparta.levelup_backend.domain.payment.dto.request.CancelPaymentRequestDto;
import com.sparta.levelup_backend.domain.payment.entity.PaymentEntity;
import com.sparta.levelup_backend.domain.payment.repository.PaymentRepository;
import com.sparta.levelup_backend.exception.common.ErrorCode;
import com.sparta.levelup_backend.exception.common.NotFoundException;
import com.sparta.levelup_backend.exception.common.PaymentException;
import com.sparta.levelup_backend.utill.BillStatus;
import com.sparta.levelup_backend.utill.OrderStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Controller
@RequestMapping
@RequiredArgsConstructor
public class PaymentController {

    private final RedisTemplate<String, String> redisTemplate;
    private final PaymentRepository paymentRepository;
    private final BillServiceImplV2 billService;
    private final BillRepository billRepository;
    private final int MAX_RETRIES = 3;

    @Value("${toss.secret.key}")
    private String tossSecretKey;

    @RequestMapping(value = {"/confirm/payment"})
    public ResponseEntity<JSONObject> confirmPayment(HttpServletRequest request, @RequestBody String jasonBody) throws Exception {

        JSONObject jasonData = parseRequestData(jasonBody);
        String paymentKey = (String) jasonData.get("paymentKey");
        String price = (String) jasonData.get("amount");
        String orderId = (String) jasonData.get("orderId");

        PaymentEntity payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PAYMENT_NOT_FOUND));

        if (Long.parseLong(price) != payment.getAmount()) {
            throw new PaymentException(ErrorCode.CONFLICT_PRICE_EQUALS);
        }

        String secretKey = tossSecretKey;
        String url = "https://api.tosspayments.com/v1/payments/confirm";

        int attempt = 0;
        JSONObject response = null;

        while (attempt < MAX_RETRIES) {
            try {
                response = sendRequest(parseRequestData(jasonBody), secretKey, url);
                int statusCode = response.containsKey("error") ? 400 : 200;

                if (statusCode == 200) {
                    // 결제 승인 정보 추출
                    String approvedAt = (String) response.get("approvedAt");
                    String method = (String) response.get("method");
                    String status = (String) response.get("status");

                    log.info("paymentKey: {}, 승인시간: {}, 결제방법: {}, 상태: {}, orderId: {}", paymentKey, approvedAt, method, status, orderId);
                    // 결제 정보 업데이트
                    payment.setPaymentKey(paymentKey);
                    payment.setIspaid(true);
                    payment.setCompletedAt(approvedAt);
                    payment.setPayType(method);
                    payment.getOrder().setStatus(OrderStatus.TRADING);
                    billService.createBill(payment.getOrder().getUser().getId(), payment.getOrder().getId());
                    redisTemplate.delete("order:expire:" + orderId);
                    log.info("영수증 생성");
                    paymentRepository.save(payment);
                }
                return ResponseEntity.status(statusCode).body(response);
            } catch (Exception e) {
                attempt++;
                log.error("결제 승인 요청 실패 - 시도 횟수: {}/{}, 내용: {}", attempt, MAX_RETRIES, e.getMessage());

                if (attempt >= MAX_RETRIES) {
                    log.error("결제 승인 요청 실패 - 데이터: {}", jasonData.toString());
                    throw new PaymentException(ErrorCode.PAYMENT_FAILED_RETRY);
                }
                Thread.sleep(2000);
            }
        }
        throw new PaymentException(ErrorCode.PAYMENT_FAILED);
    }

    @RequestMapping("/cancel/payment")
    public ResponseEntity<JSONObject> cancelPayment(@RequestBody CancelPaymentRequestDto dto) throws Exception {

        PaymentEntity payment = paymentRepository.findByPaymentKey(dto.getKey())
                .orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));

        BillEntity bill = billRepository.findByOrder(payment.getOrder())
                .orElseThrow(() -> new NotFoundException(ErrorCode.BILL_NOT_FOUND));

        log.info("결제취소 할 상품: {}, 취소 할 금액: {}", bill.getBillHistory(), bill.getPrice());

        String secretKey = tossSecretKey;
        String url = "https://api.tosspayments.com/v1/payments/" + dto.getKey() + "/cancel";

        int attempt = 0;

        JSONObject response = null;

        JSONObject cancelRequest = new JSONObject();
        cancelRequest.put("cancelReason", dto.getReason());

        while (attempt < MAX_RETRIES) {
            try {
                response = sendRequest(cancelRequest, secretKey, url);
                int statusCode = response.containsKey("error") ? 400 : 200;

                if (statusCode == 200) {

                    JSONArray cancelsArray = (JSONArray) response.get("cancels");

                    for (Object object : cancelsArray) {
                        JSONObject cancelData = (JSONObject) object;

                        String canceledAt = (String) cancelData.get("canceledAt");

                        log.info("취소 한 상품: {}, 취소 한 가격: {}, 취소 한 이유: {}, 취소 한 시간: {}",
                                bill.getBillHistory(), bill.getPrice(), dto.getReason(), canceledAt);

                    }

                    payment.setIscanceled(true);
                    bill.setStatus(BillStatus.PAYCANCELED);
                    payment.getOrder().setStatus(OrderStatus.CANCELED);
                    paymentRepository.save(payment);
                    billRepository.save(bill);
                }

                return ResponseEntity.status(statusCode).body(response);
            } catch (Exception e) {
                attempt++;
                log.error("취소 승인 요청 실패 - 시도 횟수: {}/{}, 내용: {}", attempt, MAX_RETRIES, e.getMessage());

                if (attempt >= MAX_RETRIES) {
                    log.error("취소 승인 요청 실패 - 데이터: {}", dto.getKey());
                    throw new PaymentException(ErrorCode.PAYMENT_FAILED_RETRY);
                }
                Thread.sleep(2000);
            }
        }
        throw new PaymentException(ErrorCode.PAYMENT_FAILED);
    }


    private JSONObject parseRequestData(String jsonBody) {
        try {
            return (JSONObject) new JSONParser().parse(jsonBody);
        } catch (ParseException e) {
            log.error("JSON Parsing Error", e);
            return new JSONObject();
        }
    }

    // 헤더에 시크릿키를 심어서 같이 요청
    JSONObject sendRequest(JSONObject requestData, String secretKey, String urlString) throws IOException {
        HttpURLConnection connection = createConnection(secretKey, urlString);
        try (OutputStream os = connection.getOutputStream()) {
            os.write(requestData.toString().getBytes(StandardCharsets.UTF_8));
        }

        try (InputStream responseStream = connection.getResponseCode() == 200 ? connection.getInputStream() : connection.getErrorStream();
             Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8)) {
            return (JSONObject) new JSONParser().parse(reader);
        } catch (Exception e) {
            log.error("Error reading response", e);
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "Error reading response");
            return errorResponse;
        }
    }

    // 헤더에 시크릿키 담기
    private HttpURLConnection createConnection(String secretKey, String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8)));
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        return connection;
    }
}

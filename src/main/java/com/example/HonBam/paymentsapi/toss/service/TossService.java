package com.example.HonBam.paymentsapi.toss.service;

import com.example.HonBam.auth.TokenUserInfo;
import com.example.HonBam.config.TossPaymentsConfig;
import com.example.HonBam.paymentsapi.toss.dto.requestDTO.PaymentConfirmReqDTO;
import com.example.HonBam.paymentsapi.toss.dto.requestDTO.PaymentInfoRequestDTO;
import com.example.HonBam.paymentsapi.toss.entity.PaymentInfo;
import com.example.HonBam.paymentsapi.toss.repository.PaymentInfoRepository;
import com.example.HonBam.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class TossService {

    private final UserRepository userRepository;
    private final PaymentInfoRepository paymentInfoRepository;
    private final TossPaymentsConfig tossPaymentsConfig;

    // 승인 요청 전 결제 정보 저장
    public void savePaymentInfo(PaymentInfoRequestDTO requestDTO, TokenUserInfo userInfo) {
        // orderId로 DB에서 조회하기
        Optional<PaymentInfo> foundOrderId = paymentInfoRepository.findByOrderId(requestDTO.getOrderId());

        Long payId = null; // 기존 주문번호가 없다면 payId는 null로 지정

        // paymentInfo 객체가 존재 한다면 payId를 기존 객체의 id로 변경
        if (foundOrderId.isPresent()) {
            payId = foundOrderId.get().getPayId();
        }

        // 기존 orderId가 존재 한다면 update, 아니면 insert 진행
        paymentInfoRepository.save(requestDTO.toEntity(payId));

    }

    public ResponseEntity<Map> confirm(PaymentConfirmReqDTO requestDTO) {
        // 요청 데이터
        int amount = requestDTO.getAmount();
        String orderId = requestDTO.getOrderId();
        String paymentKey = requestDTO.getPaymentKey();
        
        // DB에 저장 되어있는 결제 정보와 요청이 들어온 결제 정보가 같은 지 확인
        PaymentInfo foundPayment = paymentInfoRepository.findPaymentByOrderId(requestDTO.getOrderId());
        if (amount != foundPayment.getAmount()
                || !orderId.equals(foundPayment.getOrderId())) {
            throw new RuntimeException("주문 정보가 일치하지 않습니다.");
        }
        
        log.info("결제정보 확인 amount: {}, orderId: {}, paymentKey: {}", amount, orderId, paymentKey);

        // 결제 승인 정보 가져오기
        return getMapResponseEntity(orderId, paymentKey, amount);

    }

    // 토스 페이먼츠에 승인 요청 보내기
    private ResponseEntity<Map> getMapResponseEntity(String orderId, String paymentKey, int amount) {

        // 요청 URI
        String requestURI = "https://api.tosspayments.com/v1/payments/confirm";

        // SecretKey
        String tossSecretKey = tossPaymentsConfig.getTossSecretKey();

        // Base64로 인코딩하기
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodeBytes = encoder.encode((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodeBytes);

        // 헤더 설정하기
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authorizations);
        headers.add("Content-Type", "application/json");

        // 요청 파라미터(바디) 설정 토스 페이먼츠는 JSON형태로 통신
        JSONObject params = new JSONObject();
        params.put("orderId", orderId);
        params.put("paymentKey", paymentKey);
        params.put("amount", amount);

        // 헤더와 바디 정보를 합치기 위해 HttpEntity 생성
        HttpEntity<Object> requestEntity = new HttpEntity<>(params, headers);
        // 토스 페이먼츠와 통신

        // 통신을 보내면서 응답 데이터를 리턴
        // param1: 요청 url
        // param2: 요청 메서드 (전송 방식)
        // param3: 헤더와 요청 파라미터정보 엔터티
        // param4: 응답 데이터를 받을 객체의 타입 (ex: dto, map)
        // 만약 구조가 복잡한 경우에는 응답 데이터 타입을 String으로 받아서 JSON-simple 라이브러리로 직접 해체.

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> responseEntity = restTemplate.exchange(requestURI, HttpMethod.POST, requestEntity, Map.class);
        Map<Object, String> JSONObject = (Map<Object, String>) responseEntity.getBody();
        log.info("데이터: {}", responseEntity);
        log.info("토스페이 승인: {}", JSONObject);

        return responseEntity;
    }

    // 결제 취소
    // /v1/payments/{paymentKey}/cancel


}

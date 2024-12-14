package com.example.HonBam.paymentsapi.toss.service;

import com.example.HonBam.auth.TokenUserInfo;
import com.example.HonBam.config.TossPaymentsConfig;
import com.example.HonBam.freeboardapi.dto.TosspaymentResponseDTO;
import com.example.HonBam.paymentsapi.toss.dto.requestDTO.PaymentConfirmReqDTO;
import com.example.HonBam.paymentsapi.toss.dto.requestDTO.PaymentInfoRequestDTO;
import com.example.HonBam.paymentsapi.toss.entity.PaymentInfo;
import com.example.HonBam.paymentsapi.toss.repository.PaymentInfoRepository;
import com.example.HonBam.userapi.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.function.Consumer;

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

    // 토스 결제 승인 요청
    public TosspaymentResponseDTO confirm(PaymentConfirmReqDTO requestDTO) throws JsonProcessingException {
        String authorizations = getEncodedKey(tossPaymentsConfig.getTossSecretKey());
        // 요청 데이터
        int amount = requestDTO.getAmount();
        String orderId = requestDTO.getOrderId();
        String paymentKey = requestDTO.getPaymentKey();

        // DB에 저장 되어있는 결제 정보와 요청이 들어온 결제 정보가 같은 지 확인
        PaymentInfo foundPayment = paymentInfoRepository.findPaymentByOrderId(requestDTO.getOrderId());

        // 주문 조회
        if (foundPayment.getMethod().equals("VIRTUAL_ACCOUNT")) {
            getOrderInfo(requestDTO,authorizations);
        }

        if (amount != foundPayment.getAmount()
                || !orderId.equals(foundPayment.getOrderId())) {
            throw new RuntimeException("주문 정보가 일치하지 않습니다.");
        }
//        getOrderInfo(requestDTO,authorizations);
//        getOrderInfoByOrderId(requestDTO, authorizations);

        log.info("결제정보 확인 amount: {}, orderId: {}, paymentKey: {}", amount, orderId, paymentKey);

        // 결제 승인 정보 가져오기
        TosspaymentResponseDTO tossPaymentResponseDTO = getMapResponseEntity(orderId, paymentKey, amount, authorizations);

        // 만약 결제 방식이 가상계좌면 가상계좌 생성 요청
        // 결제 방식이 가상계좌인지 확인
//        if (tossPaymentResponseDTO.getMethod().equals("가상계좌")) {
//            createVirtualAccount(tossPaymentResponseDTO, authorizations);
//        }


        return tossPaymentResponseDTO;

    }

    
    // orderId로 주문 내역 조회
    private void getOrderInfoByOrderId(PaymentConfirmReqDTO requestDTO, String authorizations) {

        String orderId = requestDTO.getOrderId();

        // 요청 uri
        String requestURI = TossPaymentsConfig.getTOSS_URl() + "/orders/{orderId}";

        WebClient webClient = WebClient.create();
        String responseData = webClient.get()
                .uri(requestURI, orderId)
                .header("Authorization", authorizations)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info("주문번호로 조회: {}", responseData);


    }

//    // 가상계좌(VirtualAccount) 생성
//    private void createVirtualAccount(TosspaymentResponseDTO tossPaymentResponseDTO, String authorizations) throws JsonProcessingException {
//        // 요청 url
//        String requestURI = TossPaymentsConfig.getTOSS_VIRTUAL_ACCOUNT();
//
//        // 요청 헤더
////        String authorizations = getEncodedKey(tossPaymentsConfig.getTossSecretKey());
//        Consumer<HttpHeaders> headers = httpHeaders -> {
//            httpHeaders.add("Content-Type", "application/json");
//            httpHeaders.add("Authorization", authorizations);
//        };
//
//        // 요청 바디
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("amount", tossPaymentResponseDTO.getAmount());
//        jsonObject.put("orderId", tossPaymentResponseDTO.getOrderId());
//        jsonObject.put("orderName", tossPaymentResponseDTO.getOrderName());
//        jsonObject.put("customerName", tossPaymentResponseDTO.getVirtualAccount().getCustomerName());
//        jsonObject.put("bank", tossPaymentResponseDTO.getVirtualAccount().getBank());
//
//        // 토스 페이먼츠에 요청 보내기
//        WebClient webClient = WebClient.create();
//        String responseData = webClient.post()
//                .uri(requestURI)
//                .headers(headers)
//                .bodyValue(jsonObject.toString())
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
//
//        log.info(responseData);
//
//    }

    // 주문 조회하기
    private void getOrderInfo(PaymentConfirmReqDTO requestDTO, String authorizations) {
        // 요청 URI
        String requestURI = TossPaymentsConfig.getTOSS_URl() + "/{paymentKey}";

        // SecretKey
        String tossSecretKey = tossPaymentsConfig.getTossSecretKey();

        // Base64로 인코딩하기
//        String authorizations = getEncodedKey(tossSecretKey);

        // 요청보내기
        WebClient webClient = WebClient.create();
        String block = webClient.get()
                .uri(requestURI, requestDTO.getPaymentKey())
                .header("Authorization", authorizations)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        log.info("주문 조회: {}", block);

    }

    // 토스 페이먼츠에 승인 요청 보내기
    private TosspaymentResponseDTO getMapResponseEntity(String orderId, String paymentKey, int amount, String authorizations) {

        // 요청 URI
        String requestURI = TossPaymentsConfig.getTOSS_URl() + "/confirm";

        // SecretKey
        String tossSecretKey = tossPaymentsConfig.getTossSecretKey();

        // Base64로 인코딩하기
//        String authorizations = getEncodedKey(tossSecretKey);

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
        ResponseEntity<TosspaymentResponseDTO> responseEntity = restTemplate.exchange(requestURI, HttpMethod.POST, requestEntity, TosspaymentResponseDTO.class);
        TosspaymentResponseDTO responseData = responseEntity.getBody();
        log.info("데이터: {}", responseEntity);
        log.info("토스페이 승인: {}", responseData);

        return responseData;
    }


    // 결제 취소
    // /v1/payments/{paymentKey}/cancel
    public void cancel(PaymentConfirmReqDTO reqDTO) {

        String authorizations = getEncodedKey(tossPaymentsConfig.getTossSecretKey());

        String paymentKey = reqDTO.getPaymentKey();
        String requestURI = TossPaymentsConfig.getTOSS_CANCEL_URL();

        log.info("환불요청 보냄");

        // 요청 바디
        JSONObject params = new JSONObject();
        params.put("cancelReason", "단순 변심");

        // 요청 헤더
        Consumer<HttpHeaders> headers = httpHeaders -> {
            httpHeaders.add("Content-Type", "application/json");
            httpHeaders.add("Authorization", authorizations);
        };

        WebClient webClient = WebClient.create();
        String block = webClient.post()
                .uri(requestURI, paymentKey)
                .headers(headers)
                .bodyValue(params.toString())
                .retrieve()
                .bodyToMono(String.class)
                .block();
        log.info("블락: {}", block);


    }


    // 시크릿키 인코딩
    private static String getEncodedKey(String tossSecretKey) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodeBytes = encoder.encode((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodeBytes);
    }


}

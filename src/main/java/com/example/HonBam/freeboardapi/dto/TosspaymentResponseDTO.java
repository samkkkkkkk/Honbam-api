package com.example.HonBam.freeboardapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.ToString;

@Getter @ToString
public class TosspaymentResponseDTO {

    private String paymentKey;
    private String orderId;

    @JsonProperty("totalAmount")
    private Integer amount;

    private String orderName;

    private String method;

    private VirtualAccount virtualAccount;

    @Getter @ToString
    public static class VirtualAccount {
        private String customerName;
        @JsonProperty("bankCode")
        private String bank;
        private String accountNumber;
    }


}

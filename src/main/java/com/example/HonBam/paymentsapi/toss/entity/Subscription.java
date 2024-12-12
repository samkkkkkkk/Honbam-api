package com.example.HonBam.paymentsapi.toss.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_subscription")
public class Subscription {

    @Id
    private int subId;

    private LocalDateTime period;

    private int price;



}

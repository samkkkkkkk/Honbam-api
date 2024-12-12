package com.example.HonBam.paymentsapi.toss.entity;

import com.example.HonBam.userapi.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sub_management")
public class SubManagement {

    @Id
    @Column(name = "sub_management_id")
    private int subManagementId;

    @Column(nullable = false)
    private int finalPrice;

    @Column(nullable = false)
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paidDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_id")
    private Subscription subscription;


}

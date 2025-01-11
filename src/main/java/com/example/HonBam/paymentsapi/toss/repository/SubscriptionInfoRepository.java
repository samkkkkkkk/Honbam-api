package com.example.HonBam.paymentsapi.toss.repository;

import com.example.HonBam.paymentsapi.toss.entity.SubscriptionInfo;
import com.example.HonBam.userapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionInfoRepository extends JpaRepository<SubscriptionInfo, Long> {
    Optional<SubscriptionInfo> findByUserId(String UserId);

//    @Query("SELECT s FROM SubscriptionInfo s JOIN FETCH s.user u")
//    List<SubscriptionInfo> findExpireDate();

}

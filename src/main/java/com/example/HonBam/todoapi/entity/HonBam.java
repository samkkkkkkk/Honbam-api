package com.example.HonBam.HonBamapi.entity;

import com.example.HonBam.userapi.entity.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter @Getter
@ToString @EqualsAndHashCode(of = "HonBamId")
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_HonBam")
public class HonBam {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String HonBamId;

    @Column(nullable = false, length = 30)
    private String title; // 할 일
    
    private boolean done; // 할 일 완료 여부

    @CreationTimestamp
    private LocalDateTime createDate; // 등록 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
}









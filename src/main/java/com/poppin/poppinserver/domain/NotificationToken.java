package com.poppin.poppinserver.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "notification_token")
public class NotificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId; // 작성자 id

    @Column(name = "token" , nullable = false , columnDefinition = "TINYTEXT")
    private String token; // 토큰, 최대 255자

    @Column(name = "modify_date" , nullable = false)
    private LocalDateTime mod_dtm; // 토큰 갱신일

    @Column(name = "expired_date" ,  nullable = false)
    private LocalDateTime exp_dtm; // 토큰 만료일

    @Column(name = "device" , nullable = false)
    private String device; // android or ios


    @Builder
    public NotificationToken(User user, String token, LocalDateTime mod_dtm , String device){

        this.userId     = user;
        this.token      = token;
        this.mod_dtm    = mod_dtm;
        this.exp_dtm    = mod_dtm.plusMonths(2); // 2달 뒤
        this.device     = device;
    }
}

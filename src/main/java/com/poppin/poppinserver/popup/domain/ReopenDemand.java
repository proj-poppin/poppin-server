package com.poppin.poppinserver.popup.domain;

import com.poppin.poppinserver.user.domain.User;
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
@Table(name = "reopen_demand_user")
public class ReopenDemand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id",nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "popup_id", referencedColumnName = "id",nullable = false)
    private Popup popup;

    @Column(name = "fcm_token" , nullable = false)
    private String token;

    @Column(name = "modify_date" , nullable = false)
    private LocalDateTime mod_dtm; // 토큰 갱신일

    @Column(name = "expired_date" ,  nullable = false)
    private LocalDateTime exp_dtm; // 토큰 만료일


    @Column(name = "created_at" , nullable = false)
    private LocalDateTime createdAt;


    @Builder
    public ReopenDemand(User user, Popup popup, String token, LocalDateTime mod_dtm, LocalDateTime exp_dtm){
        this.user       = user;
        this.popup      = popup;
        this.token      = token;
        this.mod_dtm    = mod_dtm;
        this.exp_dtm    = exp_dtm;
        this.createdAt  = LocalDateTime.now();
    }
}

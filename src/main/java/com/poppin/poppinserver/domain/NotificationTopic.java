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
@Table(name = "notification_topic")
public class NotificationTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_id", nullable = false)
    private Popup popupId; // 팝업 id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "token_id", nullable = false)
    private NotificationToken tokenId; // 토큰 id

    @Column(name = "topic" , nullable = false)
    private String topic; // 주제(팝업의 name)

    @Column(name = "created_at" , nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "modify_date" , nullable = false)
    private LocalDateTime mod_dtm;


    @Builder
    public NotificationTopic(Popup popup, NotificationToken token, LocalDateTime mod_dtm){

        this.popupId        = popup;
        this.tokenId        = token;
        this.topic          = popup.getName();
        this.createdAt      = LocalDateTime.now();
        this.mod_dtm        = mod_dtm;

    }
}

package com.poppin.poppinserver.domain;

import com.poppin.poppinserver.type.EPopupTopic;
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
@Table(name = "popup_topic")
public class PopupTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "token_id", nullable = false)
    private NotificationToken tokenId; // 토큰 id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_id", nullable = false)
    private Popup popup; // 팝업 id

    @Enumerated(EnumType.STRING)
    @Column(name = "topic" , nullable = false)
    private EPopupTopic topic; // 팝업 관련 주제

    @Column(name = "topic_type" , nullable = false)
    private String type; // 타입

    @Column(name = "created_at" , nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "modify_date" , nullable = false)
    private LocalDateTime mod_dtm;


    @Builder
    public PopupTopic(NotificationToken token, Popup popup, String type, LocalDateTime mod_dtm, EPopupTopic topic){

        this.tokenId        = token;
        this.popup          = popup;
        this.topic          = topic;
        this.type           = type;
        this.createdAt      = LocalDateTime.now();
        this.mod_dtm        = mod_dtm;

    }
}

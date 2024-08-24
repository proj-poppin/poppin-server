package com.poppin.poppinserver.alarm.domain;

import com.poppin.poppinserver.popup.domain.Popup;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;


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
    private FCMToken tokenId; // 토큰 id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_id", nullable = false)
    private Popup popup; // 팝업 id

    @Column(name = "topic" , nullable = false)
    private String topicCode; // 타입

    @Builder
    public PopupTopic(FCMToken token, Popup popup, String topicCode){
        this.tokenId        = token;
        this.popup          = popup;
        this.topicCode      = topicCode;
    }
}

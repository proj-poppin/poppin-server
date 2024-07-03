package com.poppin.poppinserver.domain;

import com.poppin.poppinserver.type.EInformationTopic;
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
@Table(name = "information_topic")
public class InformationTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "token_id", nullable = false)
    private FCMToken tokenId; // 토큰 id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_id", nullable = false)
    private Popup popup; // 토큰 id

    @Enumerated(EnumType.STRING)
    @Column(name = "topic" , nullable = false)
    private EInformationTopic topic; // 주제

    @Column(name = "topic_type" , nullable = false)
    private String type; // 타입

    @Column(name = "created_at" , nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "modify_date" , nullable = false)
    private LocalDateTime mod_dtm;


    @Builder
    public InformationTopic(FCMToken token, String type, LocalDateTime mod_dtm, EInformationTopic topic) {
        this.tokenId = token;
        this.topic = topic;
        this.type = type;
        this.createdAt = LocalDateTime.now();
        this.mod_dtm = mod_dtm;
    }

}

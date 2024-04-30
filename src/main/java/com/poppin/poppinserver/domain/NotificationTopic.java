package com.poppin.poppinserver.domain;

import com.poppin.poppinserver.type.ETopic;
import com.poppin.poppinserver.type.ETopicType;
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
    @JoinColumn(name = "token_id", nullable = false)
    private NotificationToken tokenId; // 토큰 id

    @Enumerated(EnumType.STRING)
    @Column(name = "topic" , nullable = false)
    private ETopic topic; // 주제

    @Enumerated(EnumType.STRING)
    @Column(name = "topic_type" , nullable = false)
    private ETopicType type; // 타입

    @Column(name = "created_at" , nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "modify_date" , nullable = false)
    private LocalDateTime mod_dtm;


    @Builder
    public NotificationTopic( NotificationToken token, ETopicType type, LocalDateTime mod_dtm, ETopic topic){

        this.tokenId        = token;
        this.topic          = topic;
        this.type           = type;
        this.createdAt      = LocalDateTime.now();
        this.mod_dtm        = mod_dtm;

    }
}

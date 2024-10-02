package com.poppin.poppinserver.alarm.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;

// 공지사항 알림 테이블
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "inform_alarm")
public class InformAlarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "body", nullable = false)
    private String body;

    @Column(name = "icon", nullable = false)
    private String icon; // icon

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @OneToOne(mappedBy = "informAlarm", fetch = FetchType.EAGER)
    private InformAlarmImage informAlarmImage;

    @Builder
    public InformAlarm(String title, String body, String icon, LocalDate createdAt) {
        this.title = title;
        this.body = body;
        this.icon = icon;
        this.createdAt = createdAt;
    }

}

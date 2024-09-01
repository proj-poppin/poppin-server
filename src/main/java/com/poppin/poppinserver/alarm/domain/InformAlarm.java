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

    @Column(name = "title" , nullable = false)
    private String title;

    @Column(name = "body" , nullable = false)
    private String body;

    @Column(name = "keyword", nullable = false)
    private String keyword; // popup, inform

    @Column(name = "icon" , nullable = false)
    private String icon; // icon

    @Column(name = "created_at" , nullable = false)
    private LocalDate createdAt;



    @Builder
    public InformAlarm(String title, String body, String keyword, String icon, LocalDate createdAt) {
        this.title = title;
        this.body = body;
        this.keyword = keyword;
        this.icon = icon;
        this.createdAt = createdAt;
    }

}

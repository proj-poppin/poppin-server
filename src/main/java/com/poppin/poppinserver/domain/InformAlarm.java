package com.poppin.poppinserver.domain;

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
    private String keyword; // popup, notify

    @Column(name = "icon" , nullable = false)
    private String icon; // icon

    @Column(name = "created_at" , nullable = false)
    private LocalDate createdAt;

    @Column(name = "is_read" , nullable = false)
    private Boolean isRead;

    @Builder
    public InformAlarm(String title, String body, String keyword, String icon, LocalDate createdAt, Boolean isRead) {
        this.title = title;
        this.body = body;
        this.keyword = keyword;
        this.icon = icon;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    // isRead를 변경하는 메서드 추가
    public void markAsRead() {
        this.isRead = true;
    }
}

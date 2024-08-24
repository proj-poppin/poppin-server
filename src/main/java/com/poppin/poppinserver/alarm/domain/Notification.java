package com.poppin.poppinserver.alarm.domain;

import com.poppin.poppinserver.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;


/*
* Method : 유저 별 알림 종류 (팝업, 공지사항)
* Author : sakang
* Date   : 2024-04-24
*
* */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title" , nullable = false)
    private String title; // 제목

    @Column(name = "content" , nullable = false)
    private String content; // 내용

    @Column(name = "created_at",nullable = false)
    private LocalDate createdAt; // 생성일자

    @Column(name = "is_read", nullable = false)
    private boolean isRead;     // 읽음 여부
    @Column(name = "type" , nullable = false)
    private String type; // 팝업, 공지사항

    @Builder
    public Notification(User user, String title, String content, String type) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.createdAt = LocalDate.now();  // mm-dd
        this.isRead = false;
        this.type = type;
    }
}

/**
 * 알림
 * 1. id
 * 2. title
 * 3. content
 * 4. createdAt
 * 5. isRead
 * 6. icon -> string,
 */


/**
 * 공지사항 상세
 * 1. id
 * 2. title
 * 3. content
 * 4. img -> nullable
 */
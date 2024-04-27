package com.poppin.poppinserver.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;


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
    private LocalDateTime createdAt; // 생성일자

    @Column(name = "type" , nullable = false)
    private String type; // 팝업, 공지사항

    @Builder
    public Notification(User user, String title, String content, String type) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.type = type;
    }
}

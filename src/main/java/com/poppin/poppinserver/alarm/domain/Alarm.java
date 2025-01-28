package com.poppin.poppinserver.alarm.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "alarm")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Alarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "body", nullable = false)
    private String body;

    @Column(name = "icon", nullable = false)
    private String icon;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


    public Alarm(String title, String body, String icon) {
        this.title = title;
        this.body = body;
        this.icon = icon;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }
}

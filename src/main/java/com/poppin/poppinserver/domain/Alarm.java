package com.poppin.poppinserver.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "alarm")
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_id", nullable = false)
    private Popup popupId;

    @Column(name = "title" , nullable = false)
    private String title;

    @Column(name = "body" , nullable = false)
    private String body;

    @Column(name = "keyword", nullable = false)
    private String keyword; // popup, notify

    @Column(name = "icon" , nullable = false)
    private String url;

    @Column(name = "created_at" , nullable = false)
    private LocalDate createdAt;

    @Builder
    public Alarm(Popup popupId, String title, String body, String keyword, String url, LocalDate createdAt) {
        this.popupId = popupId;
        this.title = title;
        this.body = body;
        this.keyword = keyword;
        this.url = url;
        this.createdAt = createdAt;
    }
}

package com.poppin.poppinserver.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id",nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "popup_id", referencedColumnName = "id",nullable = false)
    private Popup popup;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "text",nullable = false)
    private String text;

    @Column(name = "visit_date",nullable = false)
    private String visitDate;

    @Column(name = "satisfaction",nullable = false)
    private String satisfaction;

    @Column(name = "congestion",nullable = false)
    private String congestion;

    @Column(name = "created_at",nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_certificated",nullable = false)
    private Boolean isCertificated;

    @Column(name = "recommend_cnt",nullable = false)
    private int recommendCnt;

    @Builder
    public Review(User user, Popup popup, String imageUrl, String text, String visitDate, String satisfaction, String congestion, boolean isCertificated, int recommendCnt) {
        this.user = user;
        this.popup = popup;
        this.imageUrl = imageUrl;
        this.text = text;
        this.visitDate = visitDate;
        this.satisfaction = satisfaction;
        this.congestion = congestion;
        this.createdAt = LocalDateTime.now();
        this.isCertificated = isCertificated;
        this.recommendCnt = 0;
    }

    public void addRecommendCnt(){this.recommendCnt += 1;} // 추천 버튼 클릭시

    public void updateReviewUrl(String imageUrl){this.imageUrl = imageUrl;}
}

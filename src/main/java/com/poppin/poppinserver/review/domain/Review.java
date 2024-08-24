package com.poppin.poppinserver.review.domain;

import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
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

    @Column(name = "token" , nullable = false)
    private String token; // 후기 추천용 토큰

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @ManyToOne
    @JoinColumn(name = "popup_id", referencedColumnName = "id",nullable = false)
    private Popup popup;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "text",nullable = false)
    private String text;

    @Column(name = "created_at",nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_certificated",nullable = false)
    private Boolean isCertificated;

    @Column(name = "is_visible" ,nullable = false)
    private Boolean isVisible;

    @Column(name = "recommend_cnt",nullable = false)
    private int recommendCnt;

    @Builder
    public Review(User user, String token, Popup popup, String imageUrl, String text, boolean isCertificated) {
        this.user = user;
        this.token = token;
        this.nickname = user.getNickname();
        this.popup = popup;
        this.imageUrl = imageUrl;
        this.text = text;
        this.createdAt = LocalDateTime.now();
        this.isCertificated = isCertificated;
        this.isVisible = true; // 후기 첫 생성 시엔 true, 이 후 신고 받아 숨김처리 시 false
        this.recommendCnt = 0;
    }

    public void addRecommendCnt(){this.recommendCnt += 1;} // 추천 버튼 클릭시
    public void updateReviewUrl(String imageUrl){this.imageUrl = imageUrl;}
    public void updateReviewInvisible() {
        if (this.getIsVisible()) {
            this.isVisible = false;
        }
    }
}

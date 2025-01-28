package com.poppin.poppinserver.review.domain;

import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


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
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;


    @Column(name = "nickname", nullable = false)
    private String nickname;

    @ManyToOne
    @JoinColumn(name = "popup_id", referencedColumnName = "id", nullable = false)
    private Popup popup;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_certified", nullable = false)
    private Boolean isCertified;

    @Column(name = "is_visible", nullable = false)
    private Boolean isVisible;

    @Column(name = "recommend_cnt", nullable = false)
    private int recommendCnt;

    @OneToMany(mappedBy = "review", fetch = FetchType.EAGER)
    private List<ReviewImage> reviewImages = new ArrayList<>();

    @Builder
    public Review(User user, Popup popup, String imageUrl, String text, boolean isCertified) {
        this.user = user;
        this.nickname = user.getNickname();
        this.popup = popup;
        this.imageUrl = imageUrl;
        this.text = text;
        this.createdAt = LocalDateTime.now();
        this.isCertified = isCertified;
        this.isVisible = true; // 후기 첫 생성 시엔 true, 이 후 신고 받아 숨김처리 시 false
        this.recommendCnt = 0;
    }

    public void addRecommendCnt() {
        this.recommendCnt += 1;
    } // 추천 버튼 클릭시

    public void updateReviewUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updateReviewInvisible() {
        if (this.getIsVisible()) {
            this.isVisible = false;
        }
    }
}

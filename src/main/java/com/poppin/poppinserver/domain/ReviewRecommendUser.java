package com.poppin.poppinserver.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;


/*후기 추천 시 사용자 정보를 집계하는 테이블*/
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "review_recommend_user")
public class ReviewRecommendUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id",nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "review_id", referencedColumnName = "id",nullable = false)
    private Review review;

    @Column(name = "created_at",nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public ReviewRecommendUser(User user, Review review) {
        this.user = user;
        this.review = review;
        this.createdAt = LocalDateTime.now();
    }
}

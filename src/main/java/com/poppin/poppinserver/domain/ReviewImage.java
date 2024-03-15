package com.poppin.poppinserver.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "review-images")
public class ReviewImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch  = FetchType.LAZY)
    @JoinColumn(name="review_id" , referencedColumnName = "id",nullable = false)
    private Review review;

    @Column(name = "image-url")
    private  String imageUrl;

    @Column(name = "created_at",nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "edited_at",nullable = false)
    private LocalDateTime editedAt;

    @Builder
    public ReviewImage(String imageUrl, Review review){
        this.imageUrl = imageUrl;
        this.review = review;
        this.createdAt = LocalDateTime.now();
        this.editedAt = LocalDateTime.now();
    }
}

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
@Table(name = "poster-images")
public class PosterImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup-id")
    private Popup popupId;

    @Column(name = "poster_url")
    private String posterUrl;

    @Column(name = "created-at")
    private LocalDateTime createdAt;

    @Column(name = "edited-at")
    private LocalDateTime editedAt;

    @Builder
    public PosterImage(String posterUrl, Popup popup) {
        this.posterUrl = posterUrl;
        this.popupId = popup;
        this.createdAt = LocalDateTime.now();
        this.editedAt = LocalDateTime.now();
    }
}

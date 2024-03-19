package com.poppin.poppinserver.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "real-time-visit")
public class RealTimeVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "popup_id", referencedColumnName = "id")
    private Popup popup;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public RealTimeVisit(User user, Popup popup) {
        this.user = user;
        this.popup = popup;
        this.createdAt = LocalDateTime.now();
    }

}

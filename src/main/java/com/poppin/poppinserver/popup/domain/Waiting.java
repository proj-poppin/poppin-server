package com.poppin.poppinserver.popup.domain;

import com.poppin.poppinserver.user.domain.User;
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
@Table(name = "waiting")
public class Waiting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "popup_id", referencedColumnName = "id", nullable = false)
    private Popup popup;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


    @Builder
    public Waiting(User user, Popup popup) {
        this.user = user;
        this.popup = popup;
        this.createdAt = LocalDateTime.now();
    }
}

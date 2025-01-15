package com.poppin.poppinserver.visit.domain;

import com.poppin.poppinserver.popup.domain.Popup;
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
@Table(name = "visit", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "popup_id"})
})
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    @JoinColumn(name = "popup_id", referencedColumnName = "id", nullable = false)
    private Popup popup;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Visit(User user, Popup popup, String status) {
        this.user = user;
        this.popup = popup;
        this.createdAt = LocalDateTime.now();
    }

}

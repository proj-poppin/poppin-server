package com.poppin.poppinserver.popup.domain;

import com.poppin.poppinserver.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "blocked_popup")
public class BlockedPopup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User userId;

    @ManyToOne
    @JoinColumn(name = "popup_id", referencedColumnName = "id", nullable = false)
    private Popup popupId;

    @Builder
    public BlockedPopup(User userId, Popup popupId) {
        this.userId = userId;
        this.popupId = popupId;
    }
}

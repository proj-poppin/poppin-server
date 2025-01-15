package com.poppin.poppinserver.alarm.domain;

import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "popup_alarm")
public class PopupAlarm extends Alarm {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_id", nullable = false)
    private Popup popup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    @Builder
    public PopupAlarm(String title, String body, String icon, Popup popup, User user) {
        super(title, body, icon);
        this.popup = popup;
        this.user = user;
        this.isRead = false;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}


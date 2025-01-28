package com.poppin.poppinserver.alarm.domain;

import com.poppin.poppinserver.popup.domain.Popup;
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
@Table(name = "popup_alarm_keyword")
public class PopupAlarmKeyword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_id", nullable = false)
    private Popup popupId;

    @Column(name = "keyword", nullable = false)
    private String keyword; // 재오픈 알람 키워드

    @Builder
    public PopupAlarmKeyword(Popup popupId, String keyword) {
        this.popupId = popupId;
        this.keyword = keyword;
    }
}

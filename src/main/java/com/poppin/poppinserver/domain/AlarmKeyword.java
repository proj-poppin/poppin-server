package com.poppin.poppinserver.domain;

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
@Table(name = "alarm_keyword")
public class AlarmKeyword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_id")
    private Popup popupId;

    @Column(name = "keyword")
    private String keyword;

    @Builder
    public AlarmKeyword(Popup popupId, String keyword) {
        this.popupId = popupId;
        this.keyword = keyword;
    }
}

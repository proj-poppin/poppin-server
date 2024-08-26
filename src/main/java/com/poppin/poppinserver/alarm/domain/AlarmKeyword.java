package com.poppin.poppinserver.alarm.domain;

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
    @JoinColumn(name = "user_alarm_keyword_id", nullable = false)
    private UserAlarmKeyword userAlarmKeyword;

    @Column(name = "keyword", nullable = false)
    private String keyword;

    @Column(name = "is_on", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isOn;

    @Builder
    public AlarmKeyword(UserAlarmKeyword userAlarmKeyword, String keyword) {
        this.userAlarmKeyword = userAlarmKeyword;
        this.keyword = keyword;
        this.isOn = true;
    }

    public void setAlarmStatus(Boolean isOn) {
        if (isOn) {
            this.isOn = true;
        } else {
            this.isOn = false;
        }
    }
}

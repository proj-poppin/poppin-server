package com.poppin.poppinserver.alarm.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "inform_alarm")
public class InformAlarm extends Alarm {

    @OneToOne(mappedBy = "informAlarm", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private InformAlarmImage informAlarmImage;

    @Builder
    public InformAlarm(String title, String body, String icon) {
        super(title, body, icon);
    }
}


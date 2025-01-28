package com.poppin.poppinserver.alarm.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Data
@Setter
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "inform_alarm_image")
public class InformAlarmImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inform_alarm_id", nullable = false)
    private InformAlarm informAlarm;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Builder
    public InformAlarmImage(InformAlarm informAlarm, String imageUrl) {
        this.informAlarm = informAlarm;
        this.imageUrl = imageUrl;
    }
}



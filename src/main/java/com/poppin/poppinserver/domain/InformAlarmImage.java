package com.poppin.poppinserver.domain;

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
@Table(name = "info_alarm_images")
public class InformAlarmImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @ManyToOne(fetch  = FetchType.LAZY)
    @JoinColumn(name="alarm_id" , referencedColumnName = "id",nullable = false)
    private InformAlarm informAlarm;

    @Column(name = "poster_url")
    private String posterUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "edited_at")
    private LocalDateTime editedAt;

    @Builder
    public InformAlarmImage(String posterUrl, InformAlarm informAlarm) {
        this.informAlarm = informAlarm;
        this.posterUrl = posterUrl;
        this.createdAt = LocalDateTime.now();
        this.editedAt = LocalDateTime.now();
    }
}

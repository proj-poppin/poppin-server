package com.poppin.poppinserver.alarm.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "alarm_setting")
public class AlarmSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // seq

    @Column(name = "token", nullable = false)
    private String token; // 추후 user로 바꾸면 좋겠음

    @Column(name = "push_yn", nullable = false)
    private Boolean pushYn; // 푸시 알림 on off

    @Column(name = "push_night_yn", nullable = false)
    private Boolean pushNightYn; // 야간 푸시 알림 on off

    @Column(name = "hoogi_yn", nullable = false)
    private Boolean hoogiYn; // 도움이 된 후기 알림 on off

    @Column(name = "open_yn", nullable = false)
    private Boolean openYn; // 관심 팝업 오픈 알림 on off

    @Column(name = "magam_yn", nullable = false)
    private Boolean magamYn; // 관심 팝업 마감 임박 알림 on off

    @Column(name = "change_info_yn", nullable = false)
    private Boolean changeInfoYn; // 관심 팝업 정보 변경 알림 on off


    @Builder(access = AccessLevel.PRIVATE)
    public AlarmSetting(String token,
                        Boolean pushYn,
                        Boolean pushNightYn,
                        Boolean hoogiYn,
                        Boolean openYn,
                        Boolean magamYn,
                        Boolean changeInfoYn) {
        this.token = token;
        this.pushYn = pushYn;
        this.pushNightYn = pushNightYn;
        this.hoogiYn = hoogiYn;
        this.openYn = openYn;
        this.magamYn = magamYn;
        this.changeInfoYn = changeInfoYn;
    }

    public static AlarmSetting createAlarmSetting(String token) {
        return AlarmSetting.builder()
                .token(token)
                .pushYn(true)
                .pushNightYn(true)
                .hoogiYn(true)
                .openYn(true)
                .magamYn(true)
                .changeInfoYn(true)
                .build();
    }

}

package com.poppin.poppinserver.alarm.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
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
    private String token;

    @Column(name = "push_yn", nullable = false)
    private String pushYn; // 푸시 알림 on off

    @Column(name = "push_night_yn", nullable = false)
    private String pushNightYn; // 야간 푸시 알림 on off

    @Column(name = "hoogi_yn", nullable = false)
    private String hoogiYn; // 도움이 된 후기 알림 on off

    @Column(name = "open_yn", nullable = false)
    private String openYn; // 관심 팝업 오픈 알림 on off

    @Column(name = "magam_yn", nullable = false)
    private String magamYn; // 관심 팝업 마감 임박 알림 on off

    @Column(name = "change_info_yn", nullable = false)
    private String changeInfoYn; // 관심 팝업 정보 변경 알림 on off


    public AlarmSetting(String token, String pushYn, String pushNightYn, String hoogiYn, String openYn, String magamYn,
                        String changeInfoYn) {
        this.token = token;
        this.pushYn = pushYn;
        this.pushNightYn = pushNightYn;
        this.hoogiYn = hoogiYn;
        this.openYn = openYn;
        this.magamYn = magamYn;
        this.changeInfoYn = changeInfoYn;
    }

}

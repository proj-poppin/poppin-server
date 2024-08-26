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

    public AlarmKeyword(UserAlarmKeyword userAlarmKeyword, String keyword) {
        this.userAlarmKeyword = userAlarmKeyword;
        this.keyword = keyword;
    }
}

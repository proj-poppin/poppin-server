package com.poppin.poppinserver.alarm.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.util.HashSet;
import java.util.Set;

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

    @OneToMany(mappedBy = "keywordId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<UserAlarmKeyword> userAlarmKeyword = new HashSet<>();

    @Column(name = "keyword", nullable = false)
    private String keyword;

    @Column(name = "is_on", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isOn;

    @Builder
    public AlarmKeyword(Set<UserAlarmKeyword> userAlarmKeyword, String keyword) {
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

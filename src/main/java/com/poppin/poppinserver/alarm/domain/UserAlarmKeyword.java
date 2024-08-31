package com.poppin.poppinserver.alarm.domain;

import com.poppin.poppinserver.user.domain.User;
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
@Table(name = "user_alarm_keyword")
public class UserAlarmKeyword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "keyword", nullable = false)
    private String keyword;

    @Column(name = "is_on", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Builder
    public UserAlarmKeyword(User user, String keyword) {
        this.user = user;
        this.keyword = keyword;
        this.isOn = true;
    }

    public void setAlarmStatus(Boolean isOn) {
        this.isOn = isOn ? true : false;
    }
}

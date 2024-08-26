package com.poppin.poppinserver.alarm.domain;

import com.poppin.poppinserver.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User userId;

    @OneToMany(mappedBy = "userAlarmKeyword", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(name = "keyword", nullable = false)
    private List<AlarmKeyword> keywords = new ArrayList<>(); // 재오픈 알람 키워드

    @Builder
    public UserAlarmKeyword(User userId, List<String> keywordList) {
        this.userId = userId;
        for (String keyword : keywordList) {
            this.keywords.add(new AlarmKeyword(this, keyword));
        }
    }
}

package com.poppin.poppinserver.visit.domain;

import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.user.domain.User;
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
@Table(name = "visit")
public class Visit {

    /*방문하기 누른 사람들 데이터*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "popup_id", referencedColumnName = "id", nullable = false)
    private Popup popup;

    @Column(name = "created_at")
    private LocalDateTime createdAt; /*방문한 날짜*/

    @Column(name = "status", nullable = false)
    private String status;

    @Builder
    public Visit(User user, Popup popup, String status) {
        this.user = user;
        this.popup = popup;
        this.createdAt = LocalDateTime.now();
        this.status = status;
    }

    public void changeStatus(String status){
        this.status = status;
    }
}

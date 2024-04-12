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
@Table(name = "visitors")
public class Visitor {

    /*방문하기 누른 사람들 데이터*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "popup_id", referencedColumnName = "id")
    private Popup popup;

    @Column(name = "created_at")
    private LocalDateTime createdAt; /*방문한 날짜*/

    @Builder
    public Visitor(User user, Popup popup) {
        this.user = user;
        this.popup = popup;
        this.createdAt = LocalDateTime.now();
    }

}

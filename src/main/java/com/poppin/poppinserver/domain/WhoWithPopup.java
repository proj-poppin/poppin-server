package com.poppin.poppinserver.domain;

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
@Table(name = "who_with_popup")
public class WhoWithPopup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "solo", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean solo;

    @Column(name = "with_friend", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean withFriend;

    @Column(name = "with_family", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean withFamily;

    @Column(name = "with_lover", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean withLover;

    @Builder
    public WhoWithPopup(Boolean solo, Boolean withFriend, Boolean withFamily,
                        Boolean withLover) {
        this.solo = solo;
        this.withFriend = withFriend;
        this.withFamily = withFamily;
        this.withLover = withLover;
    }

    public void update(Boolean solo, Boolean withFriend, Boolean withFamily,
                        Boolean withLover) {
        this.solo = solo;
        this.withFriend = withFriend;
        this.withFamily = withFamily;
        this.withLover = withLover;
    }
}

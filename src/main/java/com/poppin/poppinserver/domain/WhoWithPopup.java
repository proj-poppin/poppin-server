package com.poppin.poppinserver.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
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

    @Column(name = "solo")
    private Boolean solo;

    @Column(name = "with_friend")
    private Boolean withFriend;

    @Column(name = "with_family")
    private Boolean withFamily;

    @Column(name = "with_bool")
    private Boolean withBool;
}

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
@Table(name = "prefered_popup")
public class PreferedPopup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "market")
    private Boolean market;

    @Column(name = "display")
    private Boolean display;

    @Column(name = "experience")
    private Boolean experience;

    @Column(name = "want_free")
    private Boolean wantFree;
}

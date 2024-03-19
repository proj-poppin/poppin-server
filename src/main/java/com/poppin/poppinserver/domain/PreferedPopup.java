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

    @Column(name = "market", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean market;

    @Column(name = "display", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean display;

    @Column(name = "experience", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean experience;

    @Column(name = "want_free", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean wantFree;
}

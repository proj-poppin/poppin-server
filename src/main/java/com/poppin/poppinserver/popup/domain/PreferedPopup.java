package com.poppin.poppinserver.popup.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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

    @Column(name = "want_free", nullable = true, columnDefinition = "TINYINT(1)")
    private Boolean wantFree;

    @Builder
    public PreferedPopup(Boolean market, Boolean display, Boolean experience,
                         Boolean wantFree) {
        this.market = market;
        this.display = display;
        this.experience = experience;
        this.wantFree = wantFree;
    }

    public void update(Boolean market, Boolean display, Boolean experience,
                        Boolean wantFree) {
        this.market = market;
        this.display = display;
        this.experience = experience;
        this.wantFree = wantFree;
    }
}

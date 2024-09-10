package com.poppin.poppinserver.popup.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
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

    @Column(name = "want_free", columnDefinition = "TINYINT(1)")
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

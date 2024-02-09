package com.poppin.poppinserver.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "popups")
public class Popup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "poster_url")
    private String posterUrl;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "introduce", nullable = false)
    private String introduce;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "entrance_fee", nullable = false)
    private Integer entranceFee;

    @Column(name = "available_age", nullable = false)
    private Integer availableAge;

    @Column(name = "parking_available", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean parkingAvailable;

    @Column(name = "visiter_cnt", nullable = false)
    private Integer visiterCnt;

    @Column(name = "reopen_demand_cnt", nullable = false)
    private Integer reopenDemandCnt;

    @Column(name = "interest_cnt", nullable = false)
    private Integer interestCnt;

    @Column(name = "view_cnt", nullable = false)
    private Integer viewCnt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "open_date", nullable = false)
    private LocalDate openDate;

    @Column(name = "close_date", nullable = false)
    private LocalDate closeDate;

    @Column(name = "open_time", nullable = false)
    private LocalTime openTime;

    @Column(name = "close_time", nullable = false)
    private LocalTime closeTime;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "operation_status", nullable = false)
    private String operationStatus;

    @OneToMany(mappedBy = "popup")
    private Set<Intereste> interestes = new HashSet<>();
}

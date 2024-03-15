package com.poppin.poppinserver.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

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
    private Integer interesteCnt;

    @Column(name = "view_cnt", nullable = false)
    private Integer viewCnt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "edited_at", nullable = false)
    private LocalDateTime editedAt;

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

    @OneToMany(mappedBy = "popup" , fetch = FetchType.EAGER)
    private Set<Intereste> interestes = new HashSet<>();

    @Builder
    public Popup(String posterUrl, String name, String introduce,
                 String location, Integer entranceFee, Integer availableAge,
                 Boolean parkingAvailable,
                 LocalDate openDate, LocalDate closeDate, LocalTime openTime,
                 LocalTime closeTime, String category, String operationStatus) {
        this.posterUrl = posterUrl;
        this.name = name;
        this.introduce = introduce;
        this.location = location;
        this.entranceFee = entranceFee;
        this.availableAge = availableAge;
        this.parkingAvailable = parkingAvailable;
        this.visiterCnt = 0; // 방문하기 버튼 api 동기화
        this.reopenDemandCnt = 0; // 재오픈 수요 버튼 api 동기화
        this.interesteCnt = 0; // 관심등록 api 동기화
        this.viewCnt = 0; // 상세 조회시 자동 ++
        this.createdAt = LocalDateTime.now();
        this.editedAt = LocalDateTime.now();
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.category = category;
        this.operationStatus = operationStatus; // 내부 동기화
    }

    public void addInteresteCnt(){
        this.interesteCnt += 1;
    }

    public void addIntereste(Intereste intereste){
        this.interestes.add(intereste);
    }

    public void updatePosterUrl(String url) {this.posterUrl = url;}
}

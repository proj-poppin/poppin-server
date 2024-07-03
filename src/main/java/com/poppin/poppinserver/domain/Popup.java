package com.poppin.poppinserver.domain;

import com.poppin.poppinserver.type.EAvailableAge;
import com.poppin.poppinserver.type.EOperationStatus;
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

    @Column(name = "homepage_link")
    private String homepageLink;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "introduce")
    private String introduce;

    @Column(name = "address")
    private String address;

    @Column(name = "address_detail")
    private String addressDetail;

    @Column(name = "entrance_fee")
    private String entranceFee;

    @Column(name = "entrance_required", nullable = false)
    private Boolean entranceRequired;

    @Column(name = "resv_required")
    private Boolean resvRequired;

    @Column(name = "available_age")
    @Enumerated(EnumType.STRING)
    private EAvailableAge availableAge;

    @Column(name = "parking_available", columnDefinition = "TINYINT(1)")
    private Boolean parkingAvailable;

    @Column(name = "reopen_demand_cnt", nullable = false)
    private Integer reopenDemandCnt;

    @Column(name = "interest_cnt", nullable = false)
    private Integer interestCnt;

    @Column(name = "view_cnt", nullable = false)
    private Integer viewCnt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "edited_at", nullable = false)
    private LocalDateTime editedAt;

    @Column(name = "open_date")
    private LocalDate openDate;

    @Column(name = "close_date")
    private LocalDate closeDate;

    @Column(name = "open_time")
    private LocalTime openTime;

    @Column(name = "close_time")
    private LocalTime closeTime;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "operation_except")
    private String operationExcept;

    @Column(name = "operation_status", nullable = false)
    private String operationStatus;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "prefered_id", nullable = false)
    private PreferedPopup preferedPopup;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "taste_id", nullable = false)
    private TastePopup tastePopup;

    @ManyToOne
    @JoinColumn(name = "agent")
    private User agent;

    @OneToMany(mappedBy = "popup" , fetch = FetchType.EAGER)
    private Set<Interest> interestes = new HashSet<>();

    @OneToMany(mappedBy = "popupId", fetch = FetchType.EAGER)
    private Set<PosterImage> posterImages = new HashSet<>();

    @OneToMany(mappedBy = "popupId", fetch = FetchType.EAGER)
    private Set<AlarmKeyword> alarmKeywords = new HashSet<>();

    @Builder
    public Popup(String posterUrl, String homepageLink, String name, String introduce,
                 String address, String addressDetail, String entranceFee, Boolean entranceRequired,
                 Boolean resvRequired, EAvailableAge availableAge, Boolean parkingAvailable,
                 LocalDate openDate, LocalDate closeDate, LocalTime openTime,
                 LocalTime closeTime, Double latitude, Double longitude, String operationExcept, String operationStatus,
                 PreferedPopup preferedPopup, TastePopup tastePopup) {
        this.posterUrl = posterUrl;
        this.homepageLink = homepageLink;
        this.name = name;
        this.introduce = introduce;
        this.address = address;
        this.addressDetail = addressDetail;
        this.entranceRequired = entranceRequired;
        this.entranceFee = entranceFee;
        this.resvRequired = resvRequired;
        this.availableAge = availableAge;
        this.parkingAvailable = parkingAvailable;
        this.reopenDemandCnt = 0; // 재오픈 수요 버튼 api 동기화
        this.interestCnt = 0; // 관심등록 api 동기화
        this.viewCnt = 0; // 상세 조회시 자동 ++
        this.createdAt = LocalDateTime.now();
        this.editedAt = LocalDateTime.now();
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.operationExcept = operationExcept;
        this.operationStatus = operationStatus; // 내부 동기화
        this.tastePopup = tastePopup;
        this.preferedPopup = preferedPopup;
    }

    public void update(String homepageLink, String name, String introduce,
                 String address, String addressDetail, Boolean entranceRequired, String entranceFee,
                 Boolean resvRequired, EAvailableAge availableAge, Boolean parkingAvailable,
                 LocalDate openDate, LocalDate closeDate, LocalTime openTime,
                 LocalTime closeTime, Double latitude, Double longitude, String operationExcept, String operationStatus,
                       User agent) {
        this.homepageLink = homepageLink;
        this.name = name;
        this.introduce = introduce;
        this.address = address;
        this.addressDetail = addressDetail;
        this.entranceRequired = entranceRequired;
        this.entranceFee = entranceFee;
        this.resvRequired = resvRequired;
        this.availableAge = availableAge;
        this.parkingAvailable = parkingAvailable;
        this.reopenDemandCnt = 0; // 재오픈 수요 버튼 api 동기화
        this.interestCnt = 0; // 관심등록 api 동기화
        this.viewCnt = 0; // 상세 조회시 자동 ++
        this.createdAt = LocalDateTime.now();
        this.editedAt = LocalDateTime.now();
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.operationExcept = operationExcept;
        this.operationStatus = operationStatus; // 내부 동기화
        this.agent = agent;
    }

    public void addInterestCnt() {this.interestCnt += 1;}

    public void addreopenDemandCnt() {this.reopenDemandCnt += 1;}

    public void addViewCnt() {this.viewCnt += 1;}


    public void updatePosterUrl(String url) {this.posterUrl = url;}

    public void updateOpStatus(String op) {this.operationStatus = op;}

    public void updateAgent(User agent){
        this.agent = agent;}
}

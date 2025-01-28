package com.poppin.poppinserver.user.domain;

import com.poppin.poppinserver.alarm.domain.UserAlarmKeyword;
import com.poppin.poppinserver.core.constant.Constants;
import com.poppin.poppinserver.interest.domain.Interest;
import com.poppin.poppinserver.popup.domain.PreferedPopup;
import com.poppin.poppinserver.popup.domain.TastePopup;
import com.poppin.poppinserver.popup.domain.WhoWithPopup;
import com.poppin.poppinserver.user.domain.type.ELoginProvider;
import com.poppin.poppinserver.user.domain.type.EUserRole;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "agreed_to_privacy_policy", columnDefinition = "TINYINT(1)", nullable = false)
    private Boolean agreedToPrivacyPolicy;

    @Column(name = "agreed_to_service_terms", columnDefinition = "TINYINT(1)", nullable = false)
    private Boolean agreedToServiceTerms;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "is_deleted", columnDefinition = "TINYINT(1)", nullable = false)
    private Boolean isDeleted;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private EUserRole role;

    @Column(name = "login_provider", nullable = false)
    @Enumerated(EnumType.STRING)
    private ELoginProvider provider;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "review_cnt", nullable = false)
    private Integer reviewCnt;

    @Column(name = "visited_popup_cnt", nullable = false)
    private Integer visitedPopupCnt;

    @Column(name = "require_special_care", columnDefinition = "TINYINT(1)", nullable = false)
    private Boolean requiresSpecialCare;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Interest> interest = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<UserAlarmKeyword> userAlarmKeywords = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "prefered_popup_id")
    private PreferedPopup preferedPopup;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "taste_popup_id")
    private TastePopup tastePopup;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "who_with_popup_id")
    private WhoWithPopup whoWithPopup;

    @Column(name = "reported_cnt", nullable = false)
    private Integer reportedCnt;


    @Builder
    public User(String email, String password, String nickname,
                ELoginProvider eLoginProvider, EUserRole role,
                Boolean agreedToPrivacyPolicy, Boolean agreedToServiceTerms
    ) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.provider = eLoginProvider;
        this.role = role;
        this.agreedToPrivacyPolicy = agreedToPrivacyPolicy;
        this.agreedToServiceTerms = agreedToServiceTerms;
        this.createdAt = LocalDateTime.now();
        this.refreshToken = null;
        this.deletedAt = null;
        this.isDeleted = false;
        this.profileImageUrl = null;
        this.requiresSpecialCare = false;
        this.reviewCnt = 0;
        this.visitedPopupCnt = 0;
        this.reportedCnt = 0;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updatePopupTaste(PreferedPopup preferedPopup, TastePopup tastePopup, WhoWithPopup whoWithPopup) {
        this.preferedPopup = preferedPopup;
        this.tastePopup = tastePopup;
        this.whoWithPopup = whoWithPopup;
    }

    public void updatePopupTaste(PreferedPopup preferedPopup) {
        this.preferedPopup = preferedPopup;
    }

    public void updatePopupTaste(TastePopup tastePopup) {
        this.tastePopup = tastePopup;
    }

    public void updatePopupTaste(WhoWithPopup whoWithPopup) {
        this.whoWithPopup = whoWithPopup;
    }

    public void updateUserNickname(String nickname) {
        if (nickname != null && !nickname.isEmpty()) {
            this.nickname = nickname;
        }
    }

    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void deleteProfileImage() {
        this.profileImageUrl = null;
    }

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now().plusDays(Constants.MEMBER_INFO_RETENTION_PERIOD);
    }

    public void requiresSpecialCare() {
        this.requiresSpecialCare = true;
    }

    public void recover() {
        this.isDeleted = false;
        this.deletedAt = null;
    }

    public void addReportCnt() {
        this.reportedCnt++;
    }

    public void addReviewCnt() {
        this.reviewCnt++;
    }

    public void addVisitedPopupCnt() {
        this.visitedPopupCnt++;
    }
}

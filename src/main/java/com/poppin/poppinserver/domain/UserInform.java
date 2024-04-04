package com.poppin.poppinserver.domain;

import com.poppin.poppinserver.type.EInformProgress;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "user_inform")
public class UserInform {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "informer_id")
    private User informerId; // 제보자 id

    @Column(name = "informed_at")
    private LocalDateTime informedAt;  // 제보 일자

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_id")
    private Popup popupId; // 팝업 정보

    @Column(name = "contact_link")
    private String contactLink; // 정보를 접한 사이트 주소

    @Column(name = "progress")
    private EInformProgress progress; // 처리 상태(NOTEXECUTED | EXECUTING | EXECUTED)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private User adminId; // 담당자(관리자) id

    @Column(name = "executed_at")
    private LocalDateTime executedAt; // 처리 일자

    @Builder
    public UserInform(User informerId, LocalDateTime informedAt, Popup popupId,
                      String contactLink, EInformProgress progress) {
        this.informerId = informerId;
        this.informedAt = informedAt;
        this.popupId = popupId;
        this.contactLink = contactLink;
        this.progress = progress;
    }

    public void update(EInformProgress progress, User adminId) {
        this.progress = progress;
        this.adminId = adminId;
        this.executedAt = LocalDateTime.now();
    }
}

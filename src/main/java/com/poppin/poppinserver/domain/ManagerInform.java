package com.poppin.poppinserver.domain;

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
@Table(name = "manager_inform")
public class ManagerInform {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "informer_id")
    private User informerId; // 제보자 id

    @Column(name = "informed_at")
    private LocalDateTime informedAt;  // 제보 일자

    @Column(name = "affiliation")
    private String affiliation; // 소속

    @Column(name = "informer_email")
    private String informer_email; // 담당자 이메일

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_id")
    private Popup popupId; // 팝업 정보

    @Column(name = "progress")
    private String progress; // 처리 상태(NOTEXECUTED | EXECUTING | EXECUTED)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private User adminId; // 담당자(관리자) id

    @Column(name = "executed_at")
    private LocalDateTime executedAt; // 처리 일자

    @Builder
    public ManagerInform(User informerId, LocalDateTime informedAt, String affiliation, String informer_email, Popup popupId, String progress) {
        this.informerId = informerId;
        this.informedAt = informedAt;
        this.affiliation = affiliation;
        this.informer_email = informer_email;
        this.popupId = popupId;
        this.progress = progress;
    }

    public void execute(String progress, User adminId, LocalDateTime executedAt) {
        this.progress = progress;
        this.adminId = adminId;
        this.executedAt = executedAt;
    }
}

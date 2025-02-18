package com.poppin.poppinserver.report.domain;

import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.user.domain.User;
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
@Table(name = "report_popup")
public class ReportPopup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_id", nullable = false)
    private Popup popupId;

    @Column(name = "reported_at", nullable = false)
    private LocalDateTime reportedAt;

    @Column(name = "report_content", nullable = false)
    private String reportContent;

    @Column(name = "is_executed", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isExecuted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private User adminId;

    @Column(name = "executed_at")
    private LocalDateTime executedAt;

    @Column(name = "execute_content")
    private String executeContent;

    @Builder
    public ReportPopup(User reporterId, Popup popupId, LocalDateTime reportedAt, String reportContent,
                       Boolean isExecuted) {
        this.reporterId = reporterId;
        this.popupId = popupId;
        this.reportedAt = reportedAt;
        this.reportContent = reportContent;
        this.isExecuted = isExecuted;
    }

    public void execute(Boolean isExecuted, User adminId, LocalDateTime executedAt, String executeContent) {
        this.isExecuted = isExecuted;
        this.adminId = adminId;
        this.executedAt = executedAt;
        this.executeContent = executeContent;
    }
}

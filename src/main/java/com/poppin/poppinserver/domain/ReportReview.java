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
@Table(name = "report_review")
public class ReportReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review reviewId;

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
    public ReportReview(User reporterId, Review reviewId, LocalDateTime reportedAt, String reportContent, Boolean isExecuted) {
        this.reporterId = reporterId;
        this.reviewId = reviewId;
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

package com.poppin.poppinserver.user.domain;

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
@Table(name = "freq_question")
public class FreqQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private User adminId; // 작성자(관리자) id

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 생성일

    @Column(name = "question", nullable = false)
    private String question; // 질문 텍스트

    @Column(name = "answer", nullable = false)
    private String answer; // 답변 텍스트

    @Builder
    public FreqQuestion(User adminId, LocalDateTime createdAt, String question, String answer) {
        this.adminId = adminId;
        this.createdAt = createdAt;
        this.question = question;
        this.answer = answer;
    }
}

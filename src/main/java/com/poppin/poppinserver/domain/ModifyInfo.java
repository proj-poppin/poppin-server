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
@Table(name = "modify_info")
public class ModifyInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId; // 작성자 id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_id", nullable = false)
    private Popup popupId; // 팝업 id

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 작성 일자

    @Column(name = "content", nullable = false)
    private String content; // 수정 요청 텍스트

    @Column(name = "is_executed", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isExecuted; // 처리 여부

    @Builder
    public ModifyInfo(User userId, Popup popupId, String content) {
        this.userId = userId;
        this.popupId = popupId;
        this.createdAt = LocalDateTime.now();
        this.content = content;
    }
}

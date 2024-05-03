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
    @JoinColumn(name = "origin_popup", nullable = false)
    private Popup originPopup; // 기존 팝업 id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proxy_popup", nullable = false)
    private Popup proxyPopup; // 임시 팝업 id

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 작성 일자

    @Column(name = "content", nullable = false)
    private String content; // 수정 요청 텍스트

    @Column(name = "is_executed", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isExecuted; // 처리 여부

    @Builder
    public ModifyInfo(User userId, Popup proxyPopup, Popup originPopup, String content) {
        this.userId = userId;
        this.proxyPopup = proxyPopup;
        this.originPopup = originPopup;
        this.createdAt = LocalDateTime.now();
        this.content = content;
        this.isExecuted = false;
    }

    public void update(Boolean isExecuted) {
        this.isExecuted = isExecuted;
        this.proxyPopup = this.originPopup;
    }
}

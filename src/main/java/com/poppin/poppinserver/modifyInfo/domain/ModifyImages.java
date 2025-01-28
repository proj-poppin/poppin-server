package com.poppin.poppinserver.modifyInfo.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "modify_images")
public class ModifyImages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modify_id", nullable = false)
    private ModifyInfo modifyId; // 정보수정요청 id

    @Column(name = "image_url", nullable = false)
    private String imageUrl; // 이미지 링크

    @Builder
    public ModifyImages(ModifyInfo modifyId, String imageUrl) {
        this.modifyId = modifyId;
        this.imageUrl = imageUrl;
    }
}

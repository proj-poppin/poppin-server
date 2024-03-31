package com.poppin.poppinserver.domain;

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
    private ModifyInfo modifyId;

    @Column(name = "image_url")
    private String imageUrl;

    @Builder
    public ModifyImages(ModifyInfo modifyId, String imageUrl) {
        this.modifyId = modifyId;
        this.imageUrl = imageUrl;
    }
}

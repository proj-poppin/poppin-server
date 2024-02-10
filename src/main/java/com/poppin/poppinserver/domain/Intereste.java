package com.poppin.poppinserver.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "intereste")
public class Intereste {
    @EmbeddedId
    private InteresteId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @MapsId("popupId")
    @JoinColumn(name = "popup_id", referencedColumnName = "id")
    private Popup popup;

    // InteresteId 정적 중첩 클래스 정의
    @Embeddable
    @Getter
    public static class InteresteId implements Serializable {
        private Long userId;
        private Long popupId;

        public InteresteId() {}

        public InteresteId(Long userId, Long popupId) {
            this.userId = userId;
            this.popupId = popupId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            InteresteId that = (InteresteId) o;
            return Objects.equals(userId, that.userId) &&
                    Objects.equals(popupId, that.popupId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, popupId);
        }
    }
}
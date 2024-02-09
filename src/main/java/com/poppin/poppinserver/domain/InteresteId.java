package com.poppin.poppinserver.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
public class InteresteId implements Serializable {
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
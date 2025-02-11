package com.poppin.poppinserver.popup.repository.specification;

import com.poppin.poppinserver.popup.domain.BlockedPopup;
import com.poppin.poppinserver.popup.domain.Popup;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

public class PopupSpecification {

    public static Specification<Popup> hasTaste(String taste, Boolean value) {
        return (root, query, criteriaBuilder) -> {
            if (taste == null || value == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("tastePopup").get(taste), value);
        };
    }

    public static Specification<Popup> hasPrefered(String taste, Boolean value) {
        return (root, query, criteriaBuilder) -> {
            if (taste == null || value == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("preferedPopup").get(taste), value);
        };
    }

    public static Specification<Popup> isOperating() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("operationStatus"), "OPERATING");
    }

    public static Specification<Popup> isNotBlockedByUser(Long userId) {
        return (root, query, criteriaBuilder) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<BlockedPopup> blockedRoot = subquery.from(BlockedPopup.class);

            subquery.select(blockedRoot.get("popupId").get("id"))
                    .where(
                            criteriaBuilder.equal(blockedRoot.get("popupId").get("id"), root.get("id")),
                            criteriaBuilder.equal(blockedRoot.get("userId").get("id"), userId)
                    );

            // 차단된 팝업에 포함되지 않은 경우만 가져오도록 조건 추가
            return criteriaBuilder.not(criteriaBuilder.exists(subquery));
        };
    }
}

package com.poppin.poppinserver.popup.repository.specification;

import com.poppin.poppinserver.popup.domain.Popup;
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
}

package com.poppin.poppinserver.specification;

import com.poppin.poppinserver.domain.Popup;
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
}

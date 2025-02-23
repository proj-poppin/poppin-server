//package com.poppin.poppinserver.legacy.popup.Repository;
//
//import com.poppin.poppinserver.popup.domain.Popup;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//
//public interface DeprecatedPopupRepository extends JpaRepository<Popup, Long> {
//    // 로그인 베이스 팝업 검색
//    @Query(value = "SELECT p.* FROM popups p " +
//            "LEFT JOIN blocked_popup bp ON p.id = bp.popup_id AND bp.user_id = :userId " +
//            "WHERE bp.popup_id IS NULL " +
//            "AND (:text IS NULL OR :text = '' OR MATCH(p.name, p.introduce) AGAINST (:text IN BOOLEAN MODE)) " +
//            "AND p.operation_status = 'OPERATING' " +
//            "ORDER BY p.open_date DESC, p.id",
//            countQuery = "SELECT COUNT(*) FROM popups p " +
//                    "LEFT JOIN blocked_popup bp ON p.id = bp.popup_id AND bp.user_id = :userId " +
//                    "WHERE bp.popup_id IS NULL " +
//                    "AND (:text IS NULL OR :text = '' OR MATCH(p.name, p.introduce) AGAINST (:text IN BOOLEAN MODE)) " +
//                    "AND p.operation_status = 'OPERATING' " +
//                    "ORDER BY p.open_date DESC, p.id",
//            nativeQuery = true)
//    Page<Popup> findByTextInNameOrIntroduceBaseByBlackList(String text, Pageable pageable, Long userId);
//
//    // 비로그인 베이스 팝업 검색
//    @Query(value = "SELECT p.* FROM popups p " +
//            "WHERE (:text IS NULL OR :text = '' OR MATCH(p.name, p.introduce) AGAINST (:text IN BOOLEAN MODE)) " +
//            "AND p.operation_status = 'OPERATING' " +
//            "ORDER BY p.open_date DESC, p.id",
//            countQuery = "SELECT COUNT(*) FROM popups p " +
//                    "WHERE MATCH(p.name, p.introduce) AGAINST (:text IN BOOLEAN MODE)) " +
//                    "AND p.operation_status = 'OPERATING' " +
//                    "ORDER BY p.open_date DESC, p.id",
//            nativeQuery = true)
//    Page<Popup> findByTextInNameOrIntroduceBase(String text, Pageable pageable);
//}

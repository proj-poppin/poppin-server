package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.Popup;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PopupRepository extends JpaRepository<Popup, Long>, JpaSpecificationExecutor<Popup> {
    //인기 팝업스토어
    @Query("SELECT p FROM Popup p LEFT JOIN p.interestes i " +
            "ON i.createdAt >= :startOfDay AND i.createdAt < :endOfDay " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(i) DESC, p.viewCnt DESC")
    List<Popup> findTopOperatingPopupsByInterestAndViewCount(@Param("startOfDay") LocalDateTime startOfDay,
                                                             @Param("endOfDay") LocalDateTime endOfDay,
                                                             Pageable pageable);

    //새로 오픈 팝업
    @Query("SELECT p FROM Popup p " +
            "WHERE p.operationStatus = 'OPERATING' " +
            "ORDER BY p.openDate DESC, p.id ")
    List<Popup> findNewOpenPopupByAll(Pageable pageable);

    //종료 임박 팝업
    @Query("SELECT p FROM Popup p " +
            "WHERE p.operationStatus = 'OPERATING' " +
            "ORDER BY p.closeDate, p.id ")
    List<Popup> findClosingPopupByAll(Pageable pageable);

    //팝업 검색 -> 추후 full text search 변경 필요
    @Query("SELECT p FROM Popup p WHERE p.name LIKE %:text% OR p.introduce LIKE %:text%")
    List<Popup> findByTextInNameOrIntroduce(String text, Pageable pageable);

    @Query("SELECT p from Popup p WHERE p.operationStatus != 'TERMINATED'")
    List<Popup> findAllByOpStatusNotTerminated();

    @Query("SELECT p FROM Popup p JOIN Review  r ON  p.id = r.popup.id where p.id = :reviewId order by p.createdAt asc ")
    Popup findByReviewId(@Param("reviewId") Long reviewId);

    @Query("SELECT p FROM Popup p WHERE p.id = :vdPopupId")
    Popup findTopByPopupId(Long vdPopupId);


    @Query("SELECT p FROM Popup p JOIN ReopenDemandUser rod ON p.id = rod.popup.id WHERE p.openDate >= :nowDate ")
    List<Popup> findReopenPopup(LocalDate nowDate); // 재오픈 수요 체크 -> 재오픈 알림

    @Query("SELECT p FROM Popup p JOIN Interest i ON p.id = i.popup.id WHERE p.closeDate BETWEEN :now AND :tomorrow")
    List<Popup> findMagamPopup(@Param("now") LocalDate now, @Param("tomorrow") LocalDate tomorrow);

}

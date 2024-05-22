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

    //베이스 팝업 검색
    @Query(value = "SELECT p.* FROM popups p " +
            "WHERE (:text IS NULL OR :text = '' OR MATCH(p.name, p.introduce) AGAINST (:text IN BOOLEAN MODE)) " +
            "AND p.operation_status = 'OPERATING' " +
            "ORDER BY p.open_date DESC, p.id",
            countQuery = "SELECT COUNT(*) FROM popups p " +
                    "WHERE MATCH(p.name, p.introduce) AGAINST (:text IN BOOLEAN MODE)) " +
                    "AND p.operation_status = 'OPERATING' " +
                    "ORDER BY p.open_date DESC, p.id",
            nativeQuery = true)
    List<Popup> findByTextInNameOrIntroduceBase(String text, Pageable pageable);

    //팝업 검색
    @Query(value = "SELECT p.* FROM popups p " +
            "LEFT JOIN prefered_popup pp ON p.prefered_id = pp.id " +
            "LEFT JOIN taste_popup tp ON p.taste_id = tp.id " +
            "WHERE (:text IS NULL OR :text = '' OR MATCH(p.name, p.introduce) AGAINST (:text IN BOOLEAN MODE)) " +
            "AND p.operation_status = :oper  " +
            "AND (:fashionBeauty IS NULL OR tp.fashion_beauty = :fashionBeauty) " +
            "AND (:characters IS NULL OR tp.characters = :characters) " +
            "AND (:foodBeverage IS NULL OR tp.food_beverage = :foodBeverage) " +
            "AND (:webtoonAni IS NULL OR tp.webtoon_ani = :webtoonAni) " +
            "AND (:interiorThings IS NULL OR tp.interior_things = :interiorThings) " +
            "AND (:movie IS NULL OR tp.movie = :movie) " +
            "AND (:musical IS NULL OR tp.musical = :musical) " +
            "AND (:sports IS NULL OR tp.sports = :sports) " +
            "AND (:game IS NULL OR tp.game = :game) " +
            "AND (:itTech IS NULL OR tp.it_tech = :itTech) " +
            "AND (:kpop IS NULL OR tp.kpop = :kpop) " +
            "AND (:alcohol IS NULL OR tp.alcohol = :alcohol) " +
            "AND (:animalPlant IS NULL OR tp.animal_plant = :animalPlant) " +
            "AND (:market IS NULL OR pp.market = :market) " +
            "AND (:display IS NULL OR pp.display = :display) " +
            "AND (:experience IS NULL OR pp.experience = :experience)" +
            "AND (:etc IS NULL OR tp.etc = :etc)",
            countQuery = "SELECT COUNT(*) FROM popups p " +
                    "LEFT JOIN prefered_popup pp ON p.prefered_id = pp.id " +
                    "LEFT JOIN taste_popup tp ON p.taste_id = tp.id " +
                    "WHERE (:text IS NULL OR :text = '' OR MATCH(p.name, p.introduce) AGAINST (:text IN BOOLEAN MODE)) " +
                    "AND p.operation_status = :oper " +
                    "AND (:fashionBeauty IS NULL OR tp.fashion_beauty = :fashionBeauty) " +
                    "AND (:characters IS NULL OR tp.characters = :characters) " +
                    "AND (:foodBeverage IS NULL OR tp.food_beverage = :foodBeverage) " +
                    "AND (:webtoonAni IS NULL OR tp.webtoon_ani = :webtoonAni) " +
                    "AND (:interiorThings IS NULL OR tp.interior_things = :interiorThings) " +
                    "AND (:movie IS NULL OR tp.movie = :movie) " +
                    "AND (:musical IS NULL OR tp.musical = :musical) " +
                    "AND (:sports IS NULL OR tp.sports = :sports) " +
                    "AND (:game IS NULL OR tp.game = :game) " +
                    "AND (:itTech IS NULL OR tp.it_tech = :itTech) " +
                    "AND (:kpop IS NULL OR tp.kpop = :kpop) " +
                    "AND (:alcohol IS NULL OR tp.alcohol = :alcohol) " +
                    "AND (:animalPlant IS NULL OR tp.animal_plant = :animalPlant) " +
                    "AND (:market IS NULL OR pp.market = :market) " +
                    "AND (:display IS NULL OR pp.display = :display) " +
                    "AND (:experience IS NULL OR pp.experience = :experience)" +
                    "AND (:etc IS NULL OR tp.etc = :etc)",
            nativeQuery = true)
    List<Popup> findByTextInNameOrIntroduce(String text, Pageable pageable,
                                            Boolean market, Boolean display, Boolean experience,
                                            Boolean fashionBeauty, Boolean characters, Boolean foodBeverage,
                                            Boolean webtoonAni, Boolean interiorThings, Boolean movie,
                                            Boolean musical, Boolean sports, Boolean game,
                                            Boolean itTech, Boolean kpop, Boolean alcohol,
                                            Boolean animalPlant, Boolean etc, String oper);



    @Query("SELECT p from Popup p WHERE p.operationStatus != 'TERMINATED'")
    List<Popup> findAllByOpStatusNotTerminated();

    @Query("SELECT p FROM Popup p JOIN Review  r ON  p.id = r.popup.id where p.id = :reviewId order by p.createdAt asc ")
    Popup findByReviewId(@Param("reviewId") Long reviewId);

    @Query("SELECT p FROM Popup p WHERE p.id = :vdPopupId")
    Popup findTopByPopupId(Long vdPopupId);


    /**
     *
     * 배치 스케줄러 용 메서드 - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
     *
     */
    // POPUP 테이블 칼럼 추가, 등등 새로 조건이 삽입되고 쿼리 수정도 필요함
    @Query("SELECT p FROM Popup p " +
            "JOIN AlarmKeyword al ON p.id = al.popupId.id " +
            "WHERE p.openDate >= :nowDate " +
            "AND EXISTS (SELECT 1 FROM ReopenDemandUser rod WHERE p.id = rod.popup.id)")
    List<Popup> findReopenPopupWithDemand(LocalDate nowDate);


    @Query("SELECT p FROM Popup p JOIN Interest i ON p.id = i.popup.id WHERE p.operationStatus != 'TERMINATED' AND p.closeDate BETWEEN :now AND :tomorrow ORDER BY p.id asc")
    List<Popup> findMagamPopup(@Param("now") LocalDate now, @Param("tomorrow") LocalDate tomorrow);

    @Query("SELECT p FROM Popup p " +
            "JOIN Interest i ON p.id = i.popup.id " +
            "WHERE p.openDate = :date " +
            "AND (p.openTime <= :timeNow AND CONCAT(p.openDate, ' ', p.openTime) >= :timeBefore) " +
            "ORDER BY p.createdAt DESC")
    List<Popup> findOpenPopup(@Param("date") LocalDate date, @Param("timeNow") String timeNow, @Param("timeBefore") String timeBefore);

    @Query("SELECT p FROM Popup p " +
            "JOIN Interest i ON p.id = i.popup.id " +
            "JOIN User u ON i.user.id = u.id " +
            "WHERE MOD(DATEDIFF(CURRENT_DATE(), u.createdAt), 7) = 0")
    List<Popup> findHotPopup();

    @Query("SELECT p FROM Popup p " +
            "JOIN Visit v ON p.id = v.popup.id " +
            "JOIN User u ON v.user.id = u.id " +
            "WHERE v.createdAt <= :threeHoursAgo")
    List<Popup> findHoogi(@Param("threeHoursAgo") LocalDateTime threeHoursAgo);

    @Query("SELECT p FROM Popup p " +
            "WHERE p.operationStatus != 'EXECUTING' " +
            "ORDER BY p.name ASC")
    List<Popup> findByOperationStatusAndOrderByName(Pageable pageable);


    @Query("SELECT p FROM Popup p LEFT JOIN p.interestes i " +
            "ON i.createdAt >= :startOfWeek AND i.createdAt < :endOfWeek " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(i) DESC, p.viewCnt DESC")
    List<Popup> findTopOperatingPopupsByInterestAndViewCountAndUserCreate(@Param("startOfWeek") LocalDateTime startOfWeek,
                                                                          @Param("endOfWeek") LocalDateTime endOfWeek,
                                                                          Pageable pageable);
}
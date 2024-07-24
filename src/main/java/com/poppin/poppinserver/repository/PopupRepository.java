package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.Popup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface PopupRepository extends JpaRepository<Popup, Long>, JpaSpecificationExecutor<Popup> {
    //인기 팝업스토어
    @Query("SELECT p FROM Popup p LEFT JOIN p.interestes i " +
            "ON i.createdAt >= :startOfDay AND i.createdAt < :endOfDay " +
            "WHERE p.operationStatus = 'OPERATING' " +
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

    // 로그인 베이스 팝업 검색
    @Query(value = "SELECT p.* FROM popups p " +
            "LEFT JOIN blocked_popup bp ON p.id = bp.popup_id AND bp.user_id = :userId " +
            "WHERE bp.popup_id IS NULL " +
            "AND (:text IS NULL OR :text = '' OR MATCH(p.name, p.introduce) AGAINST (:text IN BOOLEAN MODE)) " +
            "AND p.operation_status = 'OPERATING' " +
            "ORDER BY p.open_date DESC, p.id",
            countQuery = "SELECT COUNT(*) FROM popups p " +
                    "LEFT JOIN blocked_popup bp ON p.id = bp.popup_id AND bp.user_id = :userId " +
                    "WHERE bp.popup_id IS NULL " +
                    "AND (:text IS NULL OR :text = '' OR MATCH(p.name, p.introduce) AGAINST (:text IN BOOLEAN MODE)) " +
                    "AND p.operation_status = 'OPERATING' " +
                    "ORDER BY p.open_date DESC, p.id",
            nativeQuery = true)
    Page<Popup> findByTextInNameOrIntroduceBaseByBlackList(String text, Pageable pageable, Long userId);

    // 비로그인 베이스 팝업 검색
    @Query(value = "SELECT p.* FROM popups p " +
            "WHERE (:text IS NULL OR :text = '' OR MATCH(p.name, p.introduce) AGAINST (:text IN BOOLEAN MODE)) " +
            "AND p.operation_status = 'OPERATING' " +
            "ORDER BY p.open_date DESC, p.id",
            countQuery = "SELECT COUNT(*) FROM popups p " +
                    "WHERE MATCH(p.name, p.introduce) AGAINST (:text IN BOOLEAN MODE)) " +
                    "AND p.operation_status = 'OPERATING' " +
                    "ORDER BY p.open_date DESC, p.id",
            nativeQuery = true)
    Page<Popup> findByTextInNameOrIntroduceBase(String text, Pageable pageable);


    // 로그인 팝업 검색
    @Query(value = "SELECT p.* FROM popups p " +
            "LEFT JOIN prefered_popup pp ON p.prefered_id = pp.id " +
            "LEFT JOIN taste_popup tp ON p.taste_id = tp.id " +
            "LEFT JOIN blocked_popup bp ON p.id = bp.popup_id AND bp.user_id = :userId " +
            "WHERE bp.popup_id IS NULL " +
            "AND (:text IS NULL OR :text = '' OR MATCH(p.name, p.introduce) AGAINST (:text IN BOOLEAN MODE)) " +
            "AND p.operation_status = :oper " +
            "AND (" +
            "(tp.fashion_beauty = :fashionBeauty) " +
            "OR (tp.characters = :characters) " +
            "OR (tp.food_beverage = :foodBeverage) " +
            "OR (tp.webtoon_ani = :webtoonAni) " +
            "OR (tp.interior_things = :interiorThings) " +
            "OR (tp.movie = :movie) " +
            "OR (tp.musical = :musical) " +
            "OR (tp.sports = :sports) " +
            "OR (tp.game = :game) " +
            "OR (tp.it_tech = :itTech) " +
            "OR (tp.kpop = :kpop) " +
            "OR (tp.alcohol = :alcohol) " +
            "OR (tp.animal_plant = :animalPlant) " +
            "OR (tp.etc = :etc)) " +
            "AND ( " +
            "(pp.market = :market) " +
            "OR (pp.display = :display) " +
            "OR (pp.experience = :experience))",
            countQuery = "SELECT COUNT(*) FROM popups p " +
                    "LEFT JOIN prefered_popup pp ON p.prefered_id = pp.id " +
                    "LEFT JOIN taste_popup tp ON p.taste_id = tp.id " +
                    "LEFT JOIN blocked_popup bp ON p.id = bp.popup_id AND bp.user_id = :userId " +
                    "WHERE bp.popup_id IS NULL " +
                    "AND (:text IS NULL OR :text = '' OR MATCH(p.name, p.introduce) AGAINST (:text IN BOOLEAN MODE)) " +
                    "AND p.operation_status = :oper " +
                    "AND (" +
                    "(tp.fashion_beauty = :fashionBeauty) " +
                    "OR (tp.characters = :characters) " +
                    "OR (tp.food_beverage = :foodBeverage) " +
                    "OR (tp.webtoon_ani = :webtoonAni) " +
                    "OR (tp.interior_things = :interiorThings) " +
                    "OR (tp.movie = :movie) " +
                    "OR (tp.musical = :musical) " +
                    "OR (tp.sports = :sports) " +
                    "OR (tp.game = :game) " +
                    "OR (tp.it_tech = :itTech) " +
                    "OR (tp.kpop = :kpop) " +
                    "OR (tp.alcohol = :alcohol) " +
                    "OR (tp.animal_plant = :animalPlant) " +
                    "OR (tp.etc = :etc)) " +
                    "AND ( " +
                    "(pp.market = :market) " +
                    "OR (pp.display = :display) " +
                    "OR (pp.experience = :experience))",
            nativeQuery = true)
    Page<Popup> findByTextInNameOrIntroduceByBlackList(String text, Pageable pageable,
                                                       Boolean market, Boolean display, Boolean experience,
                                                       Boolean fashionBeauty, Boolean characters, Boolean foodBeverage,
                                                       Boolean webtoonAni, Boolean interiorThings, Boolean movie,
                                                       Boolean musical, Boolean sports, Boolean game,
                                                       Boolean itTech, Boolean kpop, Boolean alcohol,
                                                       Boolean animalPlant, Boolean etc, String oper, Long userId);


    //비로그인 팝업 검색
    @Query(value = "SELECT p.* FROM popups p " +
            "LEFT JOIN prefered_popup pp ON p.prefered_id = pp.id " +
            "LEFT JOIN taste_popup tp ON p.taste_id = tp.id " +
            "WHERE (:text IS NULL OR :text = '' OR MATCH(p.name, p.introduce) AGAINST (:text IN BOOLEAN MODE)) " +
            "AND p.operation_status = :oper  " +
            "AND (" +
            "(:fashionBeauty IS NULL OR tp.fashion_beauty = :fashionBeauty) " +
            "OR (:characters IS NULL OR tp.characters = :characters) " +
            "OR (:foodBeverage IS NULL OR tp.food_beverage = :foodBeverage) " +
            "OR (:webtoonAni IS NULL OR tp.webtoon_ani = :webtoonAni) " +
            "OR (:interiorThings IS NULL OR tp.interior_things = :interiorThings) " +
            "OR (:movie IS NULL OR tp.movie = :movie) " +
            "OR (:musical IS NULL OR tp.musical = :musical) " +
            "OR (:sports IS NULL OR tp.sports = :sports) " +
            "OR (:game IS NULL OR tp.game = :game) " +
            "OR (:itTech IS NULL OR tp.it_tech = :itTech) " +
            "OR (:kpop IS NULL OR tp.kpop = :kpop) " +
            "OR (:alcohol IS NULL OR tp.alcohol = :alcohol) " +
            "OR (:animalPlant IS NULL OR tp.animal_plant = :animalPlant) " +
            "OR (:etc IS NULL OR tp.etc = :etc)) " +
            "AND ( " +
            "(:market IS NULL OR pp.market = :market) " +
            "OR (:display IS NULL OR pp.display = :display) " +
            "OR (:experience IS NULL OR pp.experience = :experience))",
            countQuery = "SELECT COUNT(*) FROM popups p " +
                    "LEFT JOIN prefered_popup pp ON p.prefered_id = pp.id " +
                    "LEFT JOIN taste_popup tp ON p.taste_id = tp.id " +
                    "WHERE (:text IS NULL OR :text = '' OR MATCH(p.name, p.introduce) AGAINST (:text IN BOOLEAN MODE)) " +
                    "AND p.operation_status = :oper " +
                    "AND (" +
                    "(:fashionBeauty IS NULL OR tp.fashion_beauty = :fashionBeauty) " +
                    "OR (:characters IS NULL OR tp.characters = :characters) " +
                    "OR (:foodBeverage IS NULL OR tp.food_beverage = :foodBeverage) " +
                    "OR (:webtoonAni IS NULL OR tp.webtoon_ani = :webtoonAni) " +
                    "OR (:interiorThings IS NULL OR tp.interior_things = :interiorThings) " +
                    "OR (:movie IS NULL OR tp.movie = :movie) " +
                    "OR (:musical IS NULL OR tp.musical = :musical) " +
                    "OR (:sports IS NULL OR tp.sports = :sports) " +
                    "OR (:game IS NULL OR tp.game = :game) " +
                    "OR (:itTech IS NULL OR tp.it_tech = :itTech) " +
                    "OR (:kpop IS NULL OR tp.kpop = :kpop) " +
                    "OR (:alcohol IS NULL OR tp.alcohol = :alcohol) " +
                    "OR (:animalPlant IS NULL OR tp.animal_plant = :animalPlant) " +
                    "OR (:etc IS NULL OR tp.etc = :etc)) " +
                    "AND ( " +
                    "(:market IS NULL OR pp.market = :market) " +
                    "OR (:display IS NULL OR pp.display = :display) " +
                    "OR (:experience IS NULL OR pp.experience = :experience))",
            nativeQuery = true)
    Page<Popup> findByTextInNameOrIntroduce(String text, Pageable pageable,
                                            Boolean market, Boolean display, Boolean experience,
                                            Boolean fashionBeauty, Boolean characters, Boolean foodBeverage,
                                            Boolean webtoonAni, Boolean interiorThings, Boolean movie,
                                            Boolean musical, Boolean sports, Boolean game,
                                            Boolean itTech, Boolean kpop, Boolean alcohol,
                                            Boolean animalPlant, Boolean etc, String oper);

    @Query(value = "SELECT * FROM popups p " +
            "WHERE (:text IS NULL OR :text = '' OR MATCH(p.name, p.introduce) AGAINST (:text IN BOOLEAN MODE)) " +
            "AND p.operation_status = :oper " +
            "ORDER BY p.name", nativeQuery = true)
    Page<Popup> findByTextInName(String text, Pageable pageable, String oper);

    @Query("SELECT p from Popup p WHERE p.operationStatus = 'NOTYET' OR p.operationStatus = 'OPERATING'")
    List<Popup> findAllByOpStatusIsNotyetOrOperating();

    @Query("SELECT p FROM Popup p JOIN Review  r ON  p.id = r.popup.id where p.id = :reviewId order by p.createdAt asc ")
    Popup findByReviewId(@Param("reviewId") Long reviewId);

    @Query("SELECT p FROM Popup p WHERE p.id = :vdPopupId")
    Popup findTopByPopupId(Long vdPopupId);

    Long countByOperationStatus(String operationStatus);


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
            "AND p.openTime <= :timeNow " +
            "AND p.openTime >= :timeBefore " +
            "ORDER BY p.createdAt DESC")
    List<Popup> findOpenPopup(@Param("date") LocalDate date,
                              @Param("timeNow") LocalTime timeNow,
                              @Param("timeBefore") LocalTime timeBefore);


    @Query("SELECT p FROM Popup p " +
            "JOIN Interest i ON p.id = i.popup.id " +
            "JOIN User u ON i.user.id = u.id " +
            "WHERE MOD(DATEDIFF(CURRENT_DATE(), u.createdAt), 7) = 0")
    List<Popup> findHotPopup();

    @Query("SELECT p FROM Popup p " +
            "JOIN Visit v ON p.id = v.popup.id " +
            "JOIN User u ON v.user.id = u.id " +
            "WHERE v.createdAt between :threeHoursAndMin AND :threeHoursAgo")
    List<Popup> findHoogi(@Param("threeHoursAndMin") LocalDateTime threeHoursAndMin, @Param("threeHoursAgo") LocalDateTime threeHoursAgo);

    @Query("SELECT p FROM Popup p " +
            "WHERE p.operationStatus = :oper " +
            "ORDER BY p.name ASC")
    Page<Popup> findByOperationStatusAndOrderByName(Pageable pageable, String oper);


    @Query("SELECT p FROM Popup p LEFT JOIN p.interestes i " +
            "ON i.createdAt >= :startOfWeek AND i.createdAt < :endOfWeek " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(i) DESC, p.viewCnt DESC")
    List<Popup> findHotPopup(@Param("startOfWeek") LocalDateTime startOfWeek,
                                                                          @Param("endOfWeek") LocalDateTime endOfWeek,
                                                                          Pageable pageable);
}
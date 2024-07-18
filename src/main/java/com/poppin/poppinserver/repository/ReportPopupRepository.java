package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.ReportPopup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportPopupRepository extends JpaRepository<ReportPopup, Long> {
    void deleteAllByPopupId(Popup popupId);

    @Query("SELECT r FROM ReportPopup r WHERE r.isExecuted = :isExec ORDER BY r.reportedAt DESC")
    Page<ReportPopup> findAllByOrderByReportedAtDesc(Pageable pageable, @Param("isExec") Boolean isExec);

    @Query("DELETE FROM ReportPopup r WHERE r.reporterId.id = :userId")
    void deleteAllByUserId(Long userId);
}

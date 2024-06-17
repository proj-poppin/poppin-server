package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.ReportPopup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportPopupRepository extends JpaRepository<ReportPopup, Long> {
    void deleteAllByPopupId(Popup popupId);

    @Query("SELECT r FROM ReportPopup r ORDER BY r.reportedAt DESC")
    Page<ReportPopup> findAllByOrderByReportedAtDesc(Pageable pageable);
}

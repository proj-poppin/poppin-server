package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.ReportPopup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportPopupRepository extends JpaRepository<ReportPopup, Long> {

}

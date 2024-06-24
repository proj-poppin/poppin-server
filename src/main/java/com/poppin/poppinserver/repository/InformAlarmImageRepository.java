package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.InformAlarmImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InformAlarmImageRepository extends JpaRepository<InformAlarmImage, Long> {

    @Query(value = "SELECT * FROM info_alarm_images ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<InformAlarmImage> findAlarmImageOrderByIdDesc();


    List<InformAlarmImage> findAllByInformAlarmId(Long id);
}

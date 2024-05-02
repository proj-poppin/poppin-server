package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.PopupTopic;
import com.poppin.poppinserver.type.EPopupTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



@Repository
public interface PopupTopicRepository extends JpaRepository<PopupTopic, Long> {

    @Query("SELECT PT FROM PopupTopic PT WHERE PT.tokenId = :token AND PT.topic = :topic")
    PopupTopic findByTokenAndTopic(@Param("token") String token , @Param("topic") EPopupTopic topic);


    //@Query("SELECT  p FROM PopupTopic p JOIN Interest i ON  p.id = i.popup.id WHERE p.openDate >= :nowDate ")
//    Optional<List<PopupTopic>> findByInterestPopupIdReopen(LocalDate nowDate); // 쿼리 수정 필요
}

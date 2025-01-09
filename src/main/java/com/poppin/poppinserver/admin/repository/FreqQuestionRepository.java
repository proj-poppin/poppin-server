package com.poppin.poppinserver.admin.repository;

import com.poppin.poppinserver.admin.domain.FreqQuestion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FreqQuestionRepository extends JpaRepository<FreqQuestion, Long> {
    @Query("SELECT fq FROM FreqQuestion fq ORDER BY fq.createdAt DESC")
    List<FreqQuestion> findAllByOrderByCreatedAtDesc();

    Optional<FreqQuestion> findById(Long id);
}

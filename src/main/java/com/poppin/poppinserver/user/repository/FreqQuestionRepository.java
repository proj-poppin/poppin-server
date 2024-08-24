package com.poppin.poppinserver.user.repository;

import com.poppin.poppinserver.user.domain.FreqQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FreqQuestionRepository extends JpaRepository<FreqQuestion, Long> {
    @Query("SELECT fq FROM FreqQuestion fq ORDER BY fq.createdAt DESC")
    List<FreqQuestion> findAllByOrderByCreatedAtDesc();

    Optional<FreqQuestion> findById(Long id);
}

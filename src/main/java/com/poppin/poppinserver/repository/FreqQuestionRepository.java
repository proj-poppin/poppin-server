package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.FreqQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FreqQuestionRepository extends JpaRepository<FreqQuestion, Long> {
    @Query("SELECT fq FROM FreqQuestion fq ORDER BY fq.createdAt DESC")
    List<FreqQuestion> findAllByOrderByCreatedAtDesc();
}

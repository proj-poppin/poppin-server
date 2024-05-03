package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.InformationTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InformationTopicRepository extends JpaRepository<InformationTopic, Long> {
}

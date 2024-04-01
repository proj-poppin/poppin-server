package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.PosterImage;
import com.poppin.poppinserver.domain.UserInform;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserinformRepository extends JpaRepository<UserInform, Long> {
}

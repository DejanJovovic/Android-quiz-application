package com.deksi.backend.repository;

import com.deksi.backend.model.UserScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserScoreRepository extends JpaRepository<UserScore, Long> {
    UserScore findByUsername(String username);
}

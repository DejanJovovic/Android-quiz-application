package com.deksi.backend.repository;

import com.deksi.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUsername(String username);

    User findByEmail(String email);
}

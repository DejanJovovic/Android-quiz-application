package com.deksi.backend.repository;

import com.deksi.backend.model.SudokuUserTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SudokuUserTimeRepository extends JpaRepository<SudokuUserTime, Long> {
    SudokuUserTime findByUsername(String username);
}

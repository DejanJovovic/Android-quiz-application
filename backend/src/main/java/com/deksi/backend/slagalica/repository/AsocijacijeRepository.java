package com.deksi.backend.slagalica.repository;

import com.deksi.backend.slagalica.model.AsocijacijeEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AsocijacijeRepository extends JpaRepository<AsocijacijeEntity, Long> {

    Optional<AsocijacijeEntity> findOneById(Long id);
    @Query(value = "SELECT * FROM asocijacije ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<AsocijacijeEntity> getRandomEntity();
}

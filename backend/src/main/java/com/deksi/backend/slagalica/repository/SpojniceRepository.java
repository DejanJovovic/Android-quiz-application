package com.deksi.backend.slagalica.repository;

import com.deksi.backend.slagalica.model.SpojniceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SpojniceRepository extends JpaRepository<SpojniceEntity, Long> {

    Optional<SpojniceEntity> findOneById(Long id);

    @Query(value = "SELECT * FROM spojnice ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<SpojniceEntity> getRandomEntity();
}

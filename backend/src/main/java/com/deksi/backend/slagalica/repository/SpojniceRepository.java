package com.deksi.backend.slagalica.repository;

import com.deksi.backend.slagalica.model.SpojniceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpojniceRepository extends JpaRepository<SpojniceEntity, Long> {

    SpojniceEntity findOneById(Long id);
}

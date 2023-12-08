package com.deksi.backend.slagalica.repository;

import com.deksi.backend.slagalica.model.AsocijacijeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AsocijacijeRepository extends JpaRepository<AsocijacijeEntity, Long> {

    Optional<AsocijacijeEntity> findOneById(Long id);
}

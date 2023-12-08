package com.deksi.backend.slagalica.repository;

import com.deksi.backend.slagalica.model.KoZnaZnaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KoZnaZnaRepository extends JpaRepository<KoZnaZnaEntity, Long> {

    KoZnaZnaEntity findOneById(Long id);
}

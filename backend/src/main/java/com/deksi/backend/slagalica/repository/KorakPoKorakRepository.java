package com.deksi.backend.slagalica.repository;

import com.deksi.backend.slagalica.model.KorakPoKorakEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KorakPoKorakRepository extends JpaRepository<KorakPoKorakEntity, Long> {

    KorakPoKorakEntity findOneById(Long id);
}

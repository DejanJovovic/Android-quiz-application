package com.deksi.backend.slagalica.repository;

import com.deksi.backend.slagalica.model.KorakPoKorakEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface KorakPoKorakRepository extends JpaRepository<KorakPoKorakEntity, Long> {

    Optional<KorakPoKorakEntity> findOneById(Long id);

    @Query(value = "SELECT * FROM korakpokorak WHERE language = :language ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<KorakPoKorakEntity> getRandomEntity(@Param("language") String language);
}

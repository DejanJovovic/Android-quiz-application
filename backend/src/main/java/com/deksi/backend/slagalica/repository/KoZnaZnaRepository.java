package com.deksi.backend.slagalica.repository;

import com.deksi.backend.slagalica.model.KoZnaZnaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KoZnaZnaRepository extends JpaRepository<KoZnaZnaEntity, Long> {

    Optional<KoZnaZnaEntity> findOneById(Long id);

    List<KoZnaZnaEntity> findAll();

    @Query(value = "SELECT * FROM koznazna WHERE language = :language ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<KoZnaZnaEntity> getRandomEntitiesByLanguage(@Param("language") String language, @Param("count") int count);
}

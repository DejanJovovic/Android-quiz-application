package com.deksi.backend.slagalica.service;

import com.deksi.backend.slagalica.model.KorakPoKorakEntity;

import java.util.Optional;

public interface KorakPoKorakService {

    Optional<KorakPoKorakEntity> findOneById(Long id);

    Long getRandomKorakPoKorakRound(Long previousRoundId, String language);
}

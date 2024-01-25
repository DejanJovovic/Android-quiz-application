package com.deksi.backend.slagalica.service;

import com.deksi.backend.slagalica.model.SpojniceEntity;

import java.util.Optional;

public interface SpojniceService {

    Optional<SpojniceEntity> findOneById(Long id);

    Long getRandomSpojniceRound(Long previousRoundId);
}

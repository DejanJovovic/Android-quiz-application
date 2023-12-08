package com.deksi.backend.slagalica.service;

import com.deksi.backend.slagalica.model.AsocijacijeEntity;

import java.util.Optional;

public interface AsocijacijeService {

    Optional<AsocijacijeEntity> findOneById(Long id);

    Long getRandomAsosijacijeRound();

}

package com.deksi.backend.slagalica.service;

import com.deksi.backend.slagalica.model.KoZnaZnaEntity;

import java.util.List;
import java.util.Optional;

public interface KoZnaZnaService {

    Optional<KoZnaZnaEntity> findOneById(Long id);

    List<Long> getRandomKoZnaZnaRounds(String language, int count);
}

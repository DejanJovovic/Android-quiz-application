package com.deksi.backend.slagalica.service.impl;

import com.deksi.backend.slagalica.model.KoZnaZnaEntity;
import com.deksi.backend.slagalica.repository.KoZnaZnaRepository;
import com.deksi.backend.slagalica.service.KoZnaZnaService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class KoZnaZnaServiceImpl implements KoZnaZnaService {

    private final KoZnaZnaRepository koZnaZnaRepository;

    public KoZnaZnaServiceImpl(KoZnaZnaRepository koZnaZnaRepository) {
        this.koZnaZnaRepository = koZnaZnaRepository;
    }

    @Override
    public Optional<KoZnaZnaEntity> findOneById(Long id) {
        return koZnaZnaRepository.findOneById(id);
    }

    @Override
    public List<Long> getRandomKoZnaZnaRounds(String language, int count) {
        List<KoZnaZnaEntity> randomEntities = koZnaZnaRepository.getRandomEntitiesByLanguage(language, count);
        return randomEntities.stream()
                .map(KoZnaZnaEntity::getId)
                .collect(Collectors.toList());
    }
}

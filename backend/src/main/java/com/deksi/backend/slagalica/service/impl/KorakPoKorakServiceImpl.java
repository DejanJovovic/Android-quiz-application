package com.deksi.backend.slagalica.service.impl;

import com.deksi.backend.slagalica.model.AsocijacijeEntity;
import com.deksi.backend.slagalica.model.KorakPoKorakEntity;
import com.deksi.backend.slagalica.repository.KorakPoKorakRepository;
import com.deksi.backend.slagalica.service.KorakPoKorakService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class KorakPoKorakServiceImpl implements KorakPoKorakService {

    private final KorakPoKorakRepository korakPoKorakRepository;

    public KorakPoKorakServiceImpl(KorakPoKorakRepository korakPoKorakRepository) {
        this.korakPoKorakRepository = korakPoKorakRepository;
    }

    @Override
    public Optional<KorakPoKorakEntity> findOneById(Long id) {
        return korakPoKorakRepository.findOneById(id);
    }

    @Override
    public Long getRandomKorakPoKorakRound(Long previousRoundId, String language) {
        Long randomRoundId;

        do {
            Optional<KorakPoKorakEntity> randomEntity = korakPoKorakRepository.getRandomEntity(language);
            randomRoundId = randomEntity.map(KorakPoKorakEntity::getId).orElse(null);

        } while (randomRoundId != null && randomRoundId.equals(previousRoundId));

        return randomRoundId;
    }
}

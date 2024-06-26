package com.deksi.backend.slagalica.service.impl;

import com.deksi.backend.slagalica.model.SpojniceEntity;
import com.deksi.backend.slagalica.repository.SpojniceRepository;
import com.deksi.backend.slagalica.service.SpojniceService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SpojniceServiceImpl implements SpojniceService {

    private final SpojniceRepository spojniceRepository;

    public SpojniceServiceImpl(SpojniceRepository spojniceRepository) {
        this.spojniceRepository = spojniceRepository;
    }

    @Override
    public Optional<SpojniceEntity> findOneById(Long id) {
        return spojniceRepository.findOneById(id);
    }


    @Override
    public Long getRandomSpojniceRound(Long previousRoundId, String language) {
        Long randomRoundId;

        do {
            Optional<SpojniceEntity> randomEntity = spojniceRepository.getRandomEntity(language);
            randomRoundId = randomEntity.map(SpojniceEntity::getId).orElse(null);

        } while (randomRoundId != null && randomRoundId.equals(previousRoundId));

        return randomRoundId;
    }
}

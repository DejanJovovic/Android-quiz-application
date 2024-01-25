package com.deksi.backend.slagalica.service.impl;

import com.deksi.backend.slagalica.model.AsocijacijeEntity;
import com.deksi.backend.slagalica.repository.AsocijacijeRepository;
import com.deksi.backend.slagalica.service.AsocijacijeService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AsocijacijeServiceImpl implements AsocijacijeService {

    private final AsocijacijeRepository asocijacijeRepository;

    public AsocijacijeServiceImpl(AsocijacijeRepository asocijacijeRepository) {
        this.asocijacijeRepository = asocijacijeRepository;
    }

    @Override
    public Optional<AsocijacijeEntity> findOneById(Long id) {
        return asocijacijeRepository.findOneById(id);
    }

    @Override
    public Long getRandomAsocijacijeRound(Long previousRoundId) {
        Long randomRoundId;

        do {
            Optional<AsocijacijeEntity> randomEntity = asocijacijeRepository.getRandomEntity();
            randomRoundId = randomEntity.map(AsocijacijeEntity::getId).orElse(null);

        } while (randomRoundId != null && randomRoundId.equals(previousRoundId));

        return randomRoundId;
    }

}

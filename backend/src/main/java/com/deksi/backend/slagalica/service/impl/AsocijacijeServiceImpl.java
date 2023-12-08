package com.deksi.backend.slagalica.service.impl;

import com.deksi.backend.slagalica.model.AsocijacijeEntity;
import com.deksi.backend.slagalica.repository.AsocijacijeRepository;
import com.deksi.backend.slagalica.service.AsocijacijeService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    //probably needs fixing
    @Override
    public Long getRandomAsosijacijeRound() {
        long count = asocijacijeRepository.count();

        if (count > 0) {
            int randomRound = (int) (Math.random() * count);
            Pageable pageable = PageRequest.of(randomRound, 1);

            return asocijacijeRepository.findAll(pageable).getContent().get(0).getId();
        }

        return null; // Or throw an exception if you prefer
    }

}

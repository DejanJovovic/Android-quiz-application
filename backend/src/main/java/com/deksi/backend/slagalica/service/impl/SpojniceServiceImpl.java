package com.deksi.backend.slagalica.service.impl;

import com.deksi.backend.slagalica.model.SpojniceEntity;
import com.deksi.backend.slagalica.repository.SpojniceRepository;
import com.deksi.backend.slagalica.service.SpojniceService;
import org.springframework.stereotype.Service;

@Service
public class SpojniceServiceImpl implements SpojniceService {

    private final SpojniceRepository spojniceRepository;

    public SpojniceServiceImpl(SpojniceRepository spojniceRepository) {
        this.spojniceRepository = spojniceRepository;
    }

    @Override
    public SpojniceEntity findOneById(Long id) {
        return spojniceRepository.findOneById(id);
    }
}

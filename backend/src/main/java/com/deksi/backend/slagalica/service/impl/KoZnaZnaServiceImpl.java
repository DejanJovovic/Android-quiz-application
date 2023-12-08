package com.deksi.backend.slagalica.service.impl;

import com.deksi.backend.slagalica.model.KoZnaZnaEntity;
import com.deksi.backend.slagalica.repository.KoZnaZnaRepository;
import com.deksi.backend.slagalica.service.KoZnaZnaService;
import org.springframework.stereotype.Service;

@Service
public class KoZnaZnaServiceImpl implements KoZnaZnaService {

    private final KoZnaZnaRepository koZnaZnaRepository;

    public KoZnaZnaServiceImpl(KoZnaZnaRepository koZnaZnaRepository) {
        this.koZnaZnaRepository = koZnaZnaRepository;
    }

    @Override
    public KoZnaZnaEntity findOneById(Long id) {
        return koZnaZnaRepository.findOneById(id);
    }
}

package com.deksi.backend.slagalica.controllers;

import com.deksi.backend.slagalica.model.AsocijacijeEntity;
import com.deksi.backend.slagalica.model.KorakPoKorakEntity;
import com.deksi.backend.slagalica.repository.KorakPoKorakRepository;
import com.deksi.backend.slagalica.service.KorakPoKorakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/korakPoKorak")
public class KorakPoKorakController {


    @Autowired
    private KorakPoKorakRepository korakPoKorakRepository;

    @Autowired
    private KorakPoKorakService korakPoKorakService;


    @GetMapping("/random-round")
    public ResponseEntity<KorakPoKorakEntity> getRandomRound() {
        Long randomRoundId = korakPoKorakService.getRandomKorakPoKorakRound();

        if (randomRoundId != null) {
            Optional<KorakPoKorakEntity> round = korakPoKorakService.findOneById(randomRoundId);

            return round.map(korakPoKorakEntity
                    -> new ResponseEntity<>(korakPoKorakEntity, HttpStatus.OK)).orElseGet(()
                    -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Optional<KorakPoKorakEntity>> getOneRoundById(@PathVariable Long id) {
        Optional<KorakPoKorakEntity> round = korakPoKorakRepository.findOneById(id);

        if (round.isPresent()) {
            return new ResponseEntity<>(round, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}

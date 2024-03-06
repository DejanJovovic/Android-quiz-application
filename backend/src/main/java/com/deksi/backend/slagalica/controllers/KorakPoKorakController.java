package com.deksi.backend.slagalica.controllers;

import com.deksi.backend.slagalica.model.KorakPoKorakEntity;
import com.deksi.backend.slagalica.repository.KorakPoKorakRepository;
import com.deksi.backend.slagalica.service.KorakPoKorakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/korakPoKorak")
public class KorakPoKorakController {


    @Autowired
    private KorakPoKorakRepository korakPoKorakRepository;

    @Autowired
    private KorakPoKorakService korakPoKorakService;

    private Long previousRoundId;


    @GetMapping("/random-round")
    public ResponseEntity<KorakPoKorakEntity> getRandomRound(@RequestParam(required = false, defaultValue = "en") String language) {
        Long randomRoundId = korakPoKorakService.getRandomKorakPoKorakRound(previousRoundId, language);

        if (randomRoundId != null) {
            Optional<KorakPoKorakEntity> round = korakPoKorakService.findOneById(randomRoundId);

            if (round.isPresent()) {
                previousRoundId = randomRoundId;
                return new ResponseEntity<>(round.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
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

package com.deksi.backend.slagalica.controllers;

import com.deksi.backend.slagalica.model.KoZnaZnaEntity;
import com.deksi.backend.slagalica.model.KorakPoKorakEntity;
import com.deksi.backend.slagalica.repository.KoZnaZnaRepository;
import com.deksi.backend.slagalica.repository.KorakPoKorakRepository;
import com.deksi.backend.slagalica.service.KoZnaZnaService;
import com.deksi.backend.slagalica.service.KorakPoKorakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/koznazna")
public class KoZnaZnaController {


    @Autowired
    private KoZnaZnaRepository koZnaZnaRepository;

    @Autowired
    private KoZnaZnaService koZnaZnaService;

//    @GetMapping("/random-round")
//    public ResponseEntity<List<KoZnaZnaEntity>> getRandomRounds() {
//        List<KoZnaZnaEntity> randomRounds = new ArrayList<>();
//
//        for (int i = 0; i < 4; i++) {
//            Long randomRoundId = koZnaZnaService.getRandomKoZnaZnaRound();
//
//            if (randomRoundId != null) {
//                Optional<KoZnaZnaEntity> round = koZnaZnaService.findOneById(randomRoundId);
//
//                round.ifPresent(randomRounds::add);
//            }
//        }
//
//        if (!randomRounds.isEmpty()) {
//            return new ResponseEntity<>(randomRounds, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }

    @GetMapping("/random-rounds")
    public ResponseEntity<List<KoZnaZnaEntity>> getRandomRounds() {
        int count = 5;
        List<Long> randomRoundIds = koZnaZnaService.getRandomKoZnaZnaRounds(count);

        if (!randomRoundIds.isEmpty()) {
            List<KoZnaZnaEntity> rounds = new ArrayList<>();
            for (Long randomRoundId : randomRoundIds) {
                koZnaZnaService.findOneById(randomRoundId).ifPresent(rounds::add);
            }

            return new ResponseEntity<>(rounds, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/get/{id}")
    public ResponseEntity<Optional<KoZnaZnaEntity>> getOneRoundById(@PathVariable Long id) {
        Optional<KoZnaZnaEntity> round = koZnaZnaRepository.findOneById(id);

        if (round.isPresent()) {
            return new ResponseEntity<>(round, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

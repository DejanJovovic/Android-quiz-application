package com.deksi.backend.slagalica.controllers;

import com.deksi.backend.slagalica.model.AsocijacijeEntity;
import com.deksi.backend.slagalica.model.SpojniceEntity;
import com.deksi.backend.slagalica.repository.SpojniceRepository;
import com.deksi.backend.slagalica.service.SpojniceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/spojnice")
public class SpojniceController {

    @Autowired
    private SpojniceRepository spojniceRepository;

    @Autowired
    private SpojniceService spojniceService;

    private Long previousRoundId;



    @GetMapping("/random-round")
    public ResponseEntity<SpojniceEntity> getRandomRound(@RequestParam(required = false, defaultValue = "en") String language) {
        Long randomRoundId = spojniceService.getRandomSpojniceRound(previousRoundId, language);

        if (randomRoundId != null) {
            Optional<SpojniceEntity> round = spojniceService.findOneById(randomRoundId);

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
    public ResponseEntity<Optional<SpojniceEntity>> getOneRoundById(@PathVariable Long id) {
        Optional<SpojniceEntity> round = spojniceRepository.findOneById(id);

        if (round.isPresent()) {
            return new ResponseEntity<>(round, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}

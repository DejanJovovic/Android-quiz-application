package com.deksi.backend.slagalica.controllers;

import com.deksi.backend.slagalica.model.SpojniceEntity;
import com.deksi.backend.slagalica.repository.SpojniceRepository;
import com.deksi.backend.slagalica.service.SpojniceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<SpojniceEntity> getRandomRound() {
        Long randomRoundId = spojniceService.getRandomSpojniceRound(previousRoundId);

        if (randomRoundId != null) {
            Optional<SpojniceEntity> round = spojniceService.findOneById(randomRoundId);

            previousRoundId = randomRoundId;

            return round.map(spojniceEntity
                    -> new ResponseEntity<>(spojniceEntity, HttpStatus.OK)).orElseGet(()
                    -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
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

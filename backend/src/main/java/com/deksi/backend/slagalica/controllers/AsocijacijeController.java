package com.deksi.backend.slagalica.controllers;

import com.deksi.backend.slagalica.model.AsocijacijeEntity;
import com.deksi.backend.slagalica.repository.AsocijacijeRepository;
import com.deksi.backend.slagalica.service.AsocijacijeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;



public class AsocijacijeController {

    @Autowired
    private AsocijacijeRepository asocijacijeRepository;

    @Autowired
    private AsocijacijeService asocijacijeService;


    // needs fixing

    @GetMapping("/random-round")
    public ResponseEntity<AsocijacijeEntity> getRandomRound() {
        Long randomRoundId = asocijacijeService.getRandomAsosijacijeRound();

        if (randomRoundId != null) {
            Optional<AsocijacijeEntity> round = asocijacijeService.findOneById(randomRoundId);

            return round.map(asocijacijeEntity
                    -> new ResponseEntity<>(asocijacijeEntity, HttpStatus.OK)).orElseGet(()
                    -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Optional<AsocijacijeEntity>> getOneRoundById(@PathVariable Long id) {
        Optional<AsocijacijeEntity> round = asocijacijeRepository.findOneById(id);

        if (round.isPresent()) {
            return new ResponseEntity<>(round, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}

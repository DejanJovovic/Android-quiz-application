package com.deksi.backend.slagalica.controllers;

import com.deksi.backend.slagalica.model.AsocijacijeEntity;
import com.deksi.backend.slagalica.repository.AsocijacijeRepository;
import com.deksi.backend.slagalica.service.AsocijacijeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/api/slagalica")
public class AsocijacijeController {

    @Autowired
    private AsocijacijeRepository asocijacijeRepository;

    @Autowired
    private AsocijacijeService asocijacijeService;

    private Long previousRoundId;


    @GetMapping("/random-round")
    public ResponseEntity<AsocijacijeEntity> getRandomRound(@RequestParam(required = false, defaultValue = "en") String language) {
        Long randomRoundId = asocijacijeService.getRandomAsocijacijeRound(previousRoundId, language);

        if (randomRoundId != null) {
            Optional<AsocijacijeEntity> round = asocijacijeService.findOneById(randomRoundId);

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
    public ResponseEntity<Optional<AsocijacijeEntity>> getOneRoundById(@PathVariable Long id) {
        Optional<AsocijacijeEntity> round = asocijacijeRepository.findOneById(id);

        if (round.isPresent()) {
            return new ResponseEntity<>(round, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}

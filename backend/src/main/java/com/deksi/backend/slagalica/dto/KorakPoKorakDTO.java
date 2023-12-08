package com.deksi.backend.slagalica.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class KorakPoKorakDTO implements Serializable {

    private final String hint1;
    private final String hint2;
    private final String hint3;
    private final String hint4;
    private final String hint5;
    private final String hint6;
    private final String hint7;
    private final String konacno;
}

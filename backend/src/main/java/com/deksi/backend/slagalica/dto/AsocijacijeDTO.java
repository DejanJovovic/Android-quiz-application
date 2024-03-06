package com.deksi.backend.slagalica.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class AsocijacijeDTO implements Serializable {

    private final String a1;
    private final String a2;
    private final String a3;
    private final String a4;
    private final String b1;
    private final String b2;
    private final String b3;
    private final String b4;
    private final String c1;
    private final String c2;
    private final String c3;
    private final String c4;
    private final String d1;
    private final String d2;
    private final String d3;
    private final String d4;
    private final String konacnoA;
    private final String konacnoB;
    private final String konacnoC;
    private final String konacnoD;
    private final String konacno;
    private final String language;

}

package com.deksi.backend.slagalica.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class SpojniceDTO implements Serializable {

    private final String subject;
    private final String left1;
    private final String left2;
    private final String left3;
    private final String left4;
    private final String left5;
    private final String right1;
    private final String right2;
    private final String right3;
    private final String right4;
    private final String right5;

}

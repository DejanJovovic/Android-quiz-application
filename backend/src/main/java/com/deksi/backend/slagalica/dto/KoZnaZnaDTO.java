package com.deksi.backend.slagalica.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class KoZnaZnaDTO implements Serializable {

    private final String question;
    private final String option1;
    private final String option2;
    private final String option3;
    private final String option4;
    private final String answer;
    private final String language;

}

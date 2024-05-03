package com.deksi.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class SudokuUserTimeDTO implements Serializable {
    private final String username;
    private final int totalTime;
    private final String difficulty;

}

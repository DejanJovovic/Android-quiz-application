package com.deksi.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UserScoresDTO implements Serializable {
    private final String username;
    private final int totalPoints;

}

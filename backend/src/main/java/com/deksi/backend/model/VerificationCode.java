package com.deksi.backend.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class VerificationCode {

    @Id
    private String email;
    @Column(nullable = false)
    private String code;


}
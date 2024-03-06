package com.deksi.backend.slagalica.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "korakpokorak")
public class KorakPoKorakEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hint1")
    private String hint1;

    @Column(name = "hint2")
    private String hint2;

    @Column(name = "hint3")
    private String hint3;

    @Column(name = "hint4")
    private String hint4;

    @Column(name = "hint5")
    private String hint5;

    @Column(name = "hint6")
    private String hint6;

    @Column(name = "hint7")
    private String hint7;

    @Column(name = "konacno")
    private String konacno;

    @Column(name = "language")
    private String language;
}

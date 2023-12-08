package com.deksi.backend.slagalica.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class AsocijacijeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "a1")
    private String a1;

    @Column(name = "a2")
    private String a2;

    @Column(name = "a3")
    private String a3;

    @Column(name = "a4")
    private String a4;

    @Column(name = "b1")
    private String b1;

    @Column(name = "b2")
    private String b2;

    @Column(name = "b3")
    private String b3;

    @Column(name = "b4")
    private String b4;

    @Column(name = "c1")
    private String c1;

    @Column(name = "c2")
    private String c2;

    @Column(name = "c3")
    private String c3;

    @Column(name = "c4")
    private String c4;

    @Column(name = "d1")
    private String d1;

    @Column(name = "d2")
    private String d2;

    @Column(name = "d3")
    private String d3;

    @Column(name = "d4")
    private String d4;

    @Column(name = "konacnoA")
    private String konacnoA;

    @Column(name = "konacnoB")
    private String konacnoB;

    @Column(name = "konacnoC")
    private String konacnoC;

    @Column(name = "konacnoD")
    private String konacnoD;

    @Column(name = "konacno")
    private String konacno;



}

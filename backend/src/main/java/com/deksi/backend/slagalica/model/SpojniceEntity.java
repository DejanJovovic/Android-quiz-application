package com.deksi.backend.slagalica.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class SpojniceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject")
    private String subject;

    @Column(name = "left1")
    private String left1;

    @Column(name = "left2")
    private String left2;

    @Column(name = "left3")
    private String left3;

    @Column(name = "left4")
    private String left4;

    @Column(name = "left5")
    private String left5;

    @Column(name = "right1")
    private String right1;

    @Column(name = "right2")
    private String right2;

    @Column(name = "right3")
    private String right3;

    @Column(name = "right4")
    private String right4;

    @Column(name = "right5")
    private String right5;

}

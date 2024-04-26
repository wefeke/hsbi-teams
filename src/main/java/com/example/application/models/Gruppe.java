//Autor: Kennet
package com.example.application.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Gruppe {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;
    private Long nummer;

    //Beziehungen
    @ManyToOne
    private Gruppenarbeit gruppenarbeit = new Gruppenarbeit();
    @ManyToMany
    private List<Teilnehmer> teilnehmer = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNummer() {
        return nummer;
    }

    public void setNummer(Long nummer) {
        this.nummer = nummer;
    }
}

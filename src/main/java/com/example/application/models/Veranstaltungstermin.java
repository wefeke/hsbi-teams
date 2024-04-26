//Autor: Kennet
package com.example.application.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Veranstaltungstermin {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    private Date datum;

    //Beziehungen
    @ManyToOne
    private Veranstaltung veranstaltung = new Veranstaltung();
    @OneToMany
    private List<Gruppenarbeit> gruppenarbeiten = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }
}

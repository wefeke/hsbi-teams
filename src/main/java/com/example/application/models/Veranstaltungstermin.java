//Autor: Kennet
package com.example.application.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Veranstaltungstermin {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    private LocalDate datum;
    private LocalTime uhrzeit;
    private String ort;
    private String notizen;

    //Beziehungen
    @ManyToOne
    private Veranstaltung veranstaltung;
    @OneToMany
    private List<Gruppenarbeit> gruppenarbeiten = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public void setDatum(LocalDate datum) {
        this.datum = datum;
    }

    public Veranstaltung getVeranstaltung() {
        return veranstaltung;
    }

    public void setVeranstaltung(Veranstaltung veranstaltung) {
        this.veranstaltung = veranstaltung;
    }

    public String getNotizen() {
        return notizen;
    }

    public void setNotizen(String notizen) {
        this.notizen = notizen;
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public LocalTime getUhrzeit() {
        return uhrzeit;
    }

    public void setUhrzeit(LocalTime uhrzeit) {
        this.uhrzeit = uhrzeit;
    }
}

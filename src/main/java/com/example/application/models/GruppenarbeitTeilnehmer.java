package com.example.application.models;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "gruppenarbeit_teilnehmer")
public class GruppenarbeitTeilnehmer {
    @EmbeddedId
    private GruppenarbeitTeilnehmerId id;

    @Column(name = "punkte", nullable = true)
    private Float punkte;

    public Float getPunkte() {
        return punkte;
    }

    public void setPunkte(Float punkte) {
        this.punkte = punkte;
    }

    public GruppenarbeitTeilnehmerId getId() {
        return id;
    }

    public void setId(GruppenarbeitTeilnehmerId id) {
        this.id = id;
    }

    public GruppenarbeitTeilnehmer() {}

    //TODO [Reverse Engineering] generate columns from DB
}
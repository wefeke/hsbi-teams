package com.example.application.models;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Die Klasse GruppenarbeitTeilnehmer repräsentiert die Beziehung zwischen Gruppenarbeit und Teilnehmer.
 * Sie enthält Informationen über die Punkte, die ein Teilnehmer für eine Gruppenarbeit erhalten hat.
 *
 * @autor Leon
 */
@Entity
@Table(name = "gruppenarbeit_teilnehmer")
public class GruppenarbeitTeilnehmer {

    @EmbeddedId
    private GruppenarbeitTeilnehmerId id;

    @Column(name = "punkte", nullable = true)
    private Float punkte;

    public GruppenarbeitTeilnehmer() {}

    public Float getPunkte() {
        return punkte;
    }

    public void setPunkte(Float punkte) {
        this.punkte = punkte;
    }

    public Double getPunkteD() {
        return punkte.doubleValue();
    }

    public void setPunkteD(Double punkte) {
        this.punkte = punkte.floatValue();
    }

    public GruppenarbeitTeilnehmerId getId() {
        return id;
    }

    public void setId(GruppenarbeitTeilnehmerId id) {
        this.id = id;
    }

}

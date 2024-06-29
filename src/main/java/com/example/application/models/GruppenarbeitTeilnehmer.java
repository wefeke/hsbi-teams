package com.example.application.models;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Die Klasse GruppenarbeitTeilnehmer repräsentiert die Beziehung zwischen Gruppenarbeit und Teilnehmer.
 * Sie enthält Informationen über die Punkte, die ein Teilnehmer für eine Gruppenarbeit erhalten hat.
 *
 * @author Leon
 */
@Entity
@Table(name = "gruppenarbeit_teilnehmer")
public class GruppenarbeitTeilnehmer {

    @EmbeddedId
    private GruppenarbeitTeilnehmerId id;

    @Column(name = "punkte", nullable = true)
    private Float punkte = 0.0f;

    /**
     * Standardkonstruktor für die Klasse GruppenarbeitTeilnehmer.
     *
     * @author Leon
     */
    public GruppenarbeitTeilnehmer() {}

    /**
     * Gibt die Punkte zurück, die ein Teilnehmer für eine Gruppenarbeit erhalten hat.
     *
     * @return die Punkte als Float
     *
     * @author Leon
     */
    public Float getPunkte() {
        return punkte;
    }

    /**
     * Setzt die Punkte, die ein Teilnehmer für eine Gruppenarbeit erhalten hat.
     *
     * @param punkte die neuen Punkte als Float
     *
     * @author Leon
     */
    public void setPunkte(Float punkte) {
        this.punkte = punkte;
    }

    /**
     * Gibt die Punkte als Double zurück.
     *
     * @return die Punkte als Double
     *
     * @author Leon
     */
    public Double getPunkteD() {
        return punkte.doubleValue();
    }

    /**
     * Setzt die Punkte, die ein Teilnehmer für eine Gruppenarbeit erhalten hat, als Double.
     *
     * @param punkte die neuen Punkte als Double
     *
     * @author Leon
     */
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

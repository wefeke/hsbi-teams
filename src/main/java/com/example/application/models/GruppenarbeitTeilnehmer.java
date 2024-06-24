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

    /**
     * Standardkonstruktor für die Klasse GruppenarbeitTeilnehmer.
     *
     * @autor Leon
     */
    public GruppenarbeitTeilnehmer() {}

    /**
     * Gibt die Punkte zurück, die ein Teilnehmer für eine Gruppenarbeit erhalten hat.
     *
     * @return die Punkte als Float
     *
     * @autor Leon
     */
    public Float getPunkte() {
        return punkte;
    }

    /**
     * Setzt die Punkte, die ein Teilnehmer für eine Gruppenarbeit erhalten hat.
     *
     * @param punkte die neuen Punkte als Float
     *
     * @autor Leon
     */
    public void setPunkte(Float punkte) {
        this.punkte = punkte;
    }

    /**
     * Gibt die Punkte als Double zurück.
     *
     * @return die Punkte als Double
     *
     * @autor Leon
     */
    public Double getPunkteD() {
        return punkte.doubleValue();
    }

    /**
     * Setzt die Punkte, die ein Teilnehmer für eine Gruppenarbeit erhalten hat, als Double.
     *
     * @param punkte die neuen Punkte als Double
     *
     * @autor Leon
     */
    public void setPunkteD(Double punkte) {
        this.punkte = punkte.floatValue();
    }

    /**
     * Gibt die eingebettete ID zurück, die die Beziehung zwischen Gruppenarbeit und Teilnehmer repräsentiert.
     *
     * @return die eingebettete ID
     *
     * @autor Leon
     */
    public GruppenarbeitTeilnehmerId getId() {
        return id;
    }

    /**
     * Setzt die eingebettete ID, die die Beziehung zwischen Gruppenarbeit und Teilnehmer repräsentiert.
     *
     * @param id die neue eingebettete ID
     *
     * @autor Leon
     */
    public void setId(GruppenarbeitTeilnehmerId id) {
        this.id = id;
    }

    // TODO [Reverse Engineering] generate columns from DB
}

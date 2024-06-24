package com.example.application.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

/**
 * Die Klasse GruppenarbeitTeilnehmerId stellt die eingebettete ID für die Beziehung zwischen Gruppenarbeit und Teilnehmer dar.
 * Sie enthält die IDs der Gruppenarbeit und des Teilnehmers.
 *
 * @autor Leon
 */
@Embeddable
public class GruppenarbeitTeilnehmerId implements Serializable {

    private static final long serialVersionUID = 6629286066534374775L;

    @NotNull
    @Column(name = "gruppenarbeiten_id", nullable = false)
    private Long gruppenarbeitId;

    @NotNull
    @Column(name = "teilnehmer_matrikel_nr", nullable = false)
    private Long teilnehmerMatrikelNr;

    /**
     * Konstruktor mit Parameter zur Initialisierung der Gruppenarbeit und Teilnehmer IDs.
     *
     * @param id1 die ID des Teilnehmers
     * @param id2 die ID der Gruppenarbeit
     *
     * @autor Leon
     */
    public GruppenarbeitTeilnehmerId(long id1, long id2) {
        this.teilnehmerMatrikelNr = id1;
        this.gruppenarbeitId = id2;
    }

    /**
     * Standardkonstruktor für die Klasse GruppenarbeitTeilnehmerId.
     *
     * @autor Leon
     */
    public GruppenarbeitTeilnehmerId() {}

    /**
     * Gibt die Matrikelnummer des Teilnehmers zurück.
     *
     * @return die Matrikelnummer des Teilnehmers
     *
     * @autor Leon
     */
    public Long getTeilnehmerMatrikelNr() {
        return teilnehmerMatrikelNr;
    }

    /**
     * Setzt die Matrikelnummer des Teilnehmers.
     *
     * @param teilnehmerMatrikelNr die neue Matrikelnummer des Teilnehmers
     *
     * @autor Leon
     */
    public void setTeilnehmerMatrikelNr(Long teilnehmerMatrikelNr) {
        this.teilnehmerMatrikelNr = teilnehmerMatrikelNr;
    }

    /**
     * Gibt die ID der Gruppenarbeit zurück.
     *
     * @return die ID der Gruppenarbeit
     *
     * @autor Leon
     */
    public Long getGruppenarbeitId() {
        return gruppenarbeitId;
    }

    /**
     * Setzt die ID der Gruppenarbeit.
     *
     * @param gruppenarbeitId die neue ID der Gruppenarbeit
     *
     * @autor Leon
     */
    public void setGruppenarbeitId(Long gruppenarbeitId) {
        this.gruppenarbeitId = gruppenarbeitId;
    }

    /**
     * Überprüft, ob zwei GruppenarbeitTeilnehmerId-Objekte gleich sind.
     *
     * @param o das zu vergleichende Objekt
     * @return true, wenn die Objekte gleich sind, sonst false
     *
     * @autor Leon
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        GruppenarbeitTeilnehmerId entity = (GruppenarbeitTeilnehmerId) o;
        return Objects.equals(this.gruppenarbeitId, entity.gruppenarbeitId) &&
                Objects.equals(this.teilnehmerMatrikelNr, entity.teilnehmerMatrikelNr);
    }

    /**
     * Gibt den Hashcode für die GruppenarbeitTeilnehmerId zurück.
     *
     * @return der Hashcode
     *
     * @autor Leon
     */
    @Override
    public int hashCode() {
        return Objects.hash(gruppenarbeitId, teilnehmerMatrikelNr);
    }
}

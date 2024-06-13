package com.example.application.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class GruppenarbeitTeilnehmerId implements Serializable {

    private static final long serialVersionUID = 6629286066534374775L;

    @NotNull
    @Column(name = "gruppenarbeiten_id", nullable = false)
    private Long gruppenarbeitId;

    @NotNull
    @Column(name = "teilnehmer_matrikel_nr", nullable = false)
    private Long teilnehmerMatrikelNr;

    public GruppenarbeitTeilnehmerId(long id1, long id2) {
        this.teilnehmerMatrikelNr = id1;
        this.gruppenarbeitId = id2;
    }

    public GruppenarbeitTeilnehmerId() {}

    public Long getTeilnehmerMatrikelNr() {
        return teilnehmerMatrikelNr;
    }

    public void setTeilnehmerMatrikelNr(Long teilnehmerMatrikelNr) {
        this.teilnehmerMatrikelNr = teilnehmerMatrikelNr;
    }

    public Long getGruppenarbeitId() {
        return gruppenarbeitId;
    }

    public void setGruppenarbeitId(Long gruppenarbeitId) {
        this.gruppenarbeitId = gruppenarbeitId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        GruppenarbeitTeilnehmerId entity = (GruppenarbeitTeilnehmerId) o;
        return Objects.equals(this.gruppenarbeitId, entity.gruppenarbeitId) &&
                Objects.equals(this.teilnehmerMatrikelNr, entity.teilnehmerMatrikelNr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gruppenarbeitId, teilnehmerMatrikelNr);
    }

}

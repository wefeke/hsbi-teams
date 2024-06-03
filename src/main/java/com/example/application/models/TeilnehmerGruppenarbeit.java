package com.example.application.models;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "teilnehmer_gruppenarbeit")
public class TeilnehmerGruppenarbeit {
    @EmbeddedId
    private TeilnehmerGruppenarbeitId id;



    public TeilnehmerGruppenarbeitId getId() {
        return id;
    }

    public void setId(TeilnehmerGruppenarbeitId id) {
        this.id = id;
    }

    public TeilnehmerGruppenarbeit() {}

    //TODO [Reverse Engineering] generate columns from DB
}
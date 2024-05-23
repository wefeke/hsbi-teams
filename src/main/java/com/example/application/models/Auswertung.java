package com.example.application.models;

import jakarta.persistence.*;

@Entity
public class Auswertung {
    @Id
    private Long id;
    private String name;
    private String titelVeranstaltung;
    private String titelGruppenarbeit;
    private Double punkte;


    public Auswertung() {

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitelVeranstaltung() {
        return titelVeranstaltung;
    }

    public void setTitelVeranstaltung(String titelVeranstaltung) {
        this.titelVeranstaltung = titelVeranstaltung;
    }

    public String getTitelGruppenarbeit() {
        return titelGruppenarbeit;
    }

    public void setTitelGruppenarbeit(String titelGruppenarbeit) {
        this.titelGruppenarbeit = titelGruppenarbeit;
    }

    public Double getPunkte() {
        return punkte;
    }

    public void setPunkte(Double punkte) {
        this.punkte = punkte;
    }
}

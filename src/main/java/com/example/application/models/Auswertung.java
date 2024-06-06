package com.example.application.models;

import jakarta.persistence.*;

@Entity
public class Auswertung {
    @Id
    private Long matrikelnummer;
    private String vorname;
    private String nachname;
    private String veranstaltung;
    private String gruppenarbeit;
    private Float punkte;


    public Long getMatrikelnummer() {
        return matrikelnummer;
    }

    public void setMatrikelnummer(Long matrikelnummer) {
        this.matrikelnummer = matrikelnummer;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public String getVeranstaltung() {
        return veranstaltung;
    }

    public void setVeranstaltung(String veranstaltung) {
        this.veranstaltung = veranstaltung;
    }

    public String getGruppenarbeit() {
        return gruppenarbeit;
    }

    public void setGruppenarbeit(String gruppenarbeit) {
        this.gruppenarbeit = gruppenarbeit;
    }

    public Float getPunkte() {
        return punkte;
    }

    public void setPunkte(Float punkte) {
        this.punkte = punkte;
    }
}

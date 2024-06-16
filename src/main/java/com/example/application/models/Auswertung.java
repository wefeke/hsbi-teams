package com.example.application.models;

public class Auswertung {


    private Long matrikelnummer;
    private String name;
    private String veranstaltung;
    private String gruppenarbeit;
    private Float punkte;

    public Float getPunkte() {
        return punkte;
    }

    public void setPunkte(Float punkte) {
        this.punkte = punkte;
    }



    public Auswertung() {

    }
    public Long getMatrikelnummer() {
        return matrikelnummer;
    }

    public void setMatrikelnummer(Long matrikelnummer) {
        this.matrikelnummer = matrikelnummer;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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


}

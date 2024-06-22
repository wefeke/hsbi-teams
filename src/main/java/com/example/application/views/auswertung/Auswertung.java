package com.example.application.views.auswertung;

import com.example.application.models.Gruppe;

import java.util.ArrayList;
import java.util.List;

// Auswertung
public class Auswertung {


    private String nameMatrikelnummer;
    private Float gesamtPunkte = 0.0f;
    private List<TGGPHelper> tggpHelper;
    private List<Gruppe> gruppe = new ArrayList<Gruppe>();

    public Auswertung() {

    }

    public List<TGGPHelper> getTggpHelper() {
        return tggpHelper;
    }

    public void setTggpHelper(List<TGGPHelper> tggpHelper) {
        this.tggpHelper = tggpHelper;
    }

    public void addToGesamtPunkte(Float punkte) {
        gesamtPunkte+=punkte;
    }

    public String getTggHelperValues() {
        StringBuilder result = new StringBuilder();
        if (tggpHelper.isEmpty()) {
            result = new StringBuilder("Keine Teilnahme");
        }
        if (!gruppe.isEmpty()) {
                result.append("Gruppe ").append(gruppe.getFirst().getNummer());
                gruppe.removeFirst();

        }

        if (gesamtPunkte != 0.0f) {
            result.append(" \u2705");
        }

        return ""+result.toString();
    }


    public Gruppe getGruppe() {
        if (gruppe.isEmpty()) {
            return null;
        } else {
            return gruppe.getFirst();
        }

    }

    public void addGruppe(Gruppe gruppe) {
        this.gruppe.add(gruppe);
    }



    public boolean gruppeAdded() {
        if (gruppe != null) {
            return true;
        } else {
            return false;
        }
    }
    public Float getGesamtPunkte() {
        return gesamtPunkte;
    }

    public void setGesamtPunkte(Float gesamtPunkte) {
        this.gesamtPunkte = gesamtPunkte;
    }


    public String getNameMatrikelnummer() {
        return nameMatrikelnummer;
    }


    public void setNameMatrikelnummer(String nameMatrikelnummer) {
        this.nameMatrikelnummer = nameMatrikelnummer;
    }
}

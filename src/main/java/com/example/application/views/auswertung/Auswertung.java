package com.example.application.views.auswertung;

import java.util.List;

// Auswertung
public class Auswertung {

    private Long matrikelnummer;
    private String name;
    private Float gesamtPunkte =0f;
    private List<TGGPHelper> tggpHelper;

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
        String res = "";

        for (int i = 0; i < tggpHelper.size(); i++) {
            if (tggpHelper.get(i).getUsed()) {
                continue;
            } else {
                res = tggpHelper.get(i).getGruppeAndCheckmark();
                tggpHelper.get(i).setUsedTrue();
                break;
            }


        }
        return res;
    }

    public Float getGesamtPunkte() {
        return gesamtPunkte;
    }

    public void setGesamtPunkte(Float gesamtPunkte) {
        this.gesamtPunkte = gesamtPunkte;
    }

    public Long getMatrikelnummer() {
        return matrikelnummer;
    }

    public void setMatrikelnummer(Long matrikelnummer) {
        this.matrikelnummer = matrikelnummer;
    }

    public String getNameMatrikelnummer() {
        return name + "\n" + "("+matrikelnummer+")";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}

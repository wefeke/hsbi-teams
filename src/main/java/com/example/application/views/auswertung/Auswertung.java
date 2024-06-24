package com.example.application.views.auswertung;

import com.example.application.models.Gruppe;

import java.util.ArrayList;
import java.util.List;

/**
 * Die Klasse Auswertung dient zur Verwaltung und Berechnung von Auswertungsdaten.
 * Sie enthält Informationen über die Matrikelnummer, die Gesamtpunkte und eine Liste von TGGPHelper-Objekten.
 *
 * @autor Leon
 */
public class Auswertung {

    private String nameMatrikelnummer;
    private Float gesamtPunkte = 0.0f;
    private List<TGGPHelper> tggpHelper;
    private List<Gruppe> gruppe = new ArrayList<>();

    /**
     * Standardkonstruktor für die Klasse Auswertung.
     *
     * @autor Leon
     */
    public Auswertung() {

    }

    /**
     * Gibt die Liste der TGGPHelper-Objekte zurück.
     *
     * @return die Liste der TGGPHelper-Objekte
     *
     * @autor Leon
     */
    public List<TGGPHelper> getTggpHelper() {
        return tggpHelper;
    }

    /**
     * Setzt die Liste der TGGPHelper-Objekte.
     *
     * @param tggpHelper die neue Liste der TGGPHelper-Objekte
     *
     * @autor Leon
     */
    public void setTggpHelper(List<TGGPHelper> tggpHelper) {
        this.tggpHelper = tggpHelper;
    }

    /**
     * Fügt die angegebenen Punkte zu den Gesamtpunkten hinzu.
     *
     * @param punkte die hinzuzufügenden Punkte
     *
     * @autor Leon
     */
    public void addToGesamtPunkte(Float punkte) {
        gesamtPunkte += punkte;
    }

    /**
     * Gibt die Werte der TGGPHelper als String zurück.
     * Falls keine Teilnahme vorhanden ist, wird dies entsprechend angezeigt.
     *
     * @return die Werte der TGGPHelper als String
     *
     * @autor Leon
     */
    public String getTggHelperValues() {
        StringBuilder result = new StringBuilder();
        if (tggpHelper.isEmpty()) {
            result = new StringBuilder("Keine Teilnahme");
        }
        if (!gruppe.isEmpty()) {
            result.append("Gruppe ").append(gruppe.get(0).getNummer());
            gruppe.remove(0);
        }

        if (gesamtPunkte != 0.0f) {
            result.append(" \u2705");
        }

        return result.toString();
    }

    /**
     * Gibt die erste Gruppe in der Liste zurück, falls vorhanden.
     *
     * @return die erste Gruppe oder null, wenn die Liste leer ist
     *
     * @autor Leon
     */
    public Gruppe getGruppe() {
        if (gruppe.isEmpty()) {
            return null;
        } else {
            return gruppe.get(0);
        }
    }

    /**
     * Fügt eine Gruppe zur Liste hinzu.
     *
     * @param gruppe die hinzuzufügende Gruppe
     *
     * @autor Leon
     */
    public void addGruppe(Gruppe gruppe) {
        this.gruppe.add(gruppe);
    }

    /**
     * Überprüft, ob eine Gruppe hinzugefügt wurde.
     *
     * @return true, wenn eine Gruppe hinzugefügt wurde, sonst false
     *
     * @autor Leon
     */
    public boolean gruppeAdded() {
        return gruppe != null;
    }

    /**
     * Gibt die Gesamtpunkte zurück.
     *
     * @return die Gesamtpunkte
     *
     * @autor Leon
     */
    public Float getGesamtPunkte() {
        return gesamtPunkte;
    }

    /**
     * Setzt die Gesamtpunkte.
     *
     * @param gesamtPunkte die neuen Gesamtpunkte
     *
     * @autor Leon
     */
    public void setGesamtPunkte(Float gesamtPunkte) {
        this.gesamtPunkte = gesamtPunkte;
    }

    /**
     * Gibt die Matrikelnummer zurück.
     *
     * @return die Matrikelnummer
     *
     * @autor Leon
     */
    public String getNameMatrikelnummer() {
        return nameMatrikelnummer;
    }

    /**
     * Setzt die Matrikelnummer.
     *
     * @param nameMatrikelnummer die neue Matrikelnummer
     *
     * @autor Leon
     */
    public void setNameMatrikelnummer(String nameMatrikelnummer) {
        this.nameMatrikelnummer = nameMatrikelnummer;
    }
}

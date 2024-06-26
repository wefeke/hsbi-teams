package com.example.application.views.auswertung;

import com.example.application.models.Gruppe;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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
    private List<Long> gruppen = new ArrayList<>();
    private List<Float> punkte = new ArrayList<>();
    private int anzahlGruppenarbeiten;

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

        if (!gruppen.isEmpty()) {
            if (gruppen.getFirst() != 0) {
                result.append(gruppen.getFirst());
            }

            gruppen.removeFirst();
        }

        if (!punkte.isEmpty()) {
            if (punkte.getFirst() != 0.0f) {
                result.append(", ").append(punkte.getFirst()).append(" Punkte");
            }
            punkte.removeFirst();
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
    public List<Long> getGruppen() {
        if (gruppen.isEmpty()) {
            return null;
        } else {
            return gruppen;
        }
    }

    /**
     * Fügt eine Gruppenummer zur Liste hinzu am Index i.
     *
     * @param gruppe die hinzuzufügende Gruppe
     *
     * @autor Leon
     */
    public void addGruppeNummer(Long gruppe, int i) {
        if (gruppen.isEmpty()) {
            gruppen.add(gruppe);
        } else {
            if (gruppen.get(i) != null) {
                this.gruppen.set(i,gruppe);
            } else {
                this.gruppen.add(gruppe);
            }
        }
    }

    public void addGruppeNummer(Long gruppe) {
        if (gruppen.isEmpty()) {
            gruppen.add(gruppe);
        } else {
            gruppen.add(gruppe);
        }
    }

    /**
     * Überprüft, ob eine Gruppe hinzugefügt wurde.
     *
     * @return true, wenn eine Gruppe hinzugefügt wurde, sonst false
     *
     * @autor Leon
     */
    public boolean gruppeAdded() {
        return gruppen != null;
    }

    /**
     * Gibt die Gesamtpunkte zurück.
     *
     * @return die Gesamtpunkte
     *
     * @autor Leon
     */
    public String getGesamtPunkteAndGruppenarbeiten() {
        return gesamtPunkte + ", " + anzahlGruppenarbeiten;
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

    /**
     * Gibt die Liste der Punkte für die Gruppenarbeitn zurück.
     *
     * @return die Punkte
     *
     * @autor Leon
     */
    public List<Float> getPunkte() {
        return punkte;
    }
    /**
     * Setzt die Punkte der Gruppenarbeiten.
     *
     * @param punkte die neuen Punkte für die Gruppenarbeiten
     *
     * @autor Leon
     */
    public void setPunkte(List<Float> punkte) {
        this.punkte = punkte;
    }

    public void addPunkte(Float punkte) {
        this.punkte.add(punkte);
    }

    public int getAnzahlGruppenarbeiten() {
        return anzahlGruppenarbeiten;
    }

    public void setAnzahlGruppenarbeiten(int anzahlGruppenarbeiten) {
        this.anzahlGruppenarbeiten = anzahlGruppenarbeiten;
    }

    public void incrementAnzahlGruppenarbeiten() {
        anzahlGruppenarbeiten++;
    }


}

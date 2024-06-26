package com.example.application.views.auswertung;


import java.util.ArrayList;
import java.util.List;

/**
 * Die Klasse Auswertung dient zur Verwaltung und Berechnung von Auswertungsdaten.
 * Sie enthält Informationen über die Matrikelnummer, die Gesamtpunkte und eine Liste von TGGPHelper-Objekten.
 *
 * @author Leon
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
     * @author Leon
     */
    public Auswertung() {

    }

    /**
     * Gibt die Liste der TGGPHelper-Objekte zurück.
     *
     * @return die Liste der TGGPHelper-Objekte
     *
     * @author Leon
     */
    public List<TGGPHelper> getTggpHelper() {
        return tggpHelper;
    }

    /**
     * Setzt die Liste der TGGPHelper-Objekte.
     *
     * @param tggpHelper die neue Liste der TGGPHelper-Objekte
     *
     * @author Leon
     */
    public void setTggpHelper(List<TGGPHelper> tggpHelper) {
        this.tggpHelper = tggpHelper;
    }

    /**
     * Fügt die angegebenen Punkte zu den Gesamtpunkten hinzu.
     *
     * @param punkte die hinzuzufügenden Punkte
     *
     * @author Leon
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
     * @author Leon
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
     * Gibt die Werte der TGGPHelper als String zurück.
     * Falls keine Teilnahme vorhanden ist, wird dies entsprechend angezeigt.
     *
     * @return die Werte der TGGPHelper als String
     *
     * @author Leon
     */
    public Long getTggHelperValuesGruppe() { // Wie getTggHelperValues nur die Punkte

        Long gruppe = 0L;
        if (!gruppen.isEmpty()) {
            if (gruppen.getFirst() != 0) {
                gruppe = gruppen.getFirst();
            }
            gruppen.removeFirst();
        }

        return gruppe;
    }
    /**
     * Gibt die Werte der TGGPHelper als String zurück.
     * Falls keine Teilnahme vorhanden ist, wird dies entsprechend angezeigt.
     *
     * @return die Werte der TGGPHelper als String
     *
     * @author Leon
     */
    public Float getTggHelperValuesPunkte() { // Wie getTggHelperValues nur die Punkte

        Float punkt = 0.0f;
        if (!punkte.isEmpty()) {
            if (punkte.getFirst() != 0.0f) {
                punkt = punkte.getFirst();
            }
            punkte.removeFirst();
        }

        return punkt;
    }

    /**
     * Gibt die erste Gruppe in der Liste zurück, falls vorhanden.
     *
     * @return die erste Gruppe oder null, wenn die Liste leer ist
     *
     * @author Leon
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
     * @author Leon
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

    /**
     * Fügt eine neue Gruppennummer zur Liste der Gruppen hinzu.
     *
     * @param gruppe die hinzuzufügende Gruppennummer
     * @author Leon
     */
    public void addGruppeNummer(Long gruppe) {
        if (gruppen.isEmpty()) {
            gruppen.add(gruppe);
        } else {
            gruppen.add(gruppe);
        }
    }

    /**
     * Gibt die Gesamtpunkte und Gruppenarbeiten zurück.
     *
     * @return die Gesamtpunkte und Gruppenarbeiten
     *
     * @author Leon
     */
    public String getGesamtPunkteAndGruppenarbeiten() {
        return gesamtPunkte + ", " + anzahlGruppenarbeiten;
    }

    /**
     * Gibt die teilgenommenen Gruppenarbeiten zurück.
     *
     * @return die Gruppenarbeiten
     *
     * @author Leon
     */
    public int getGesamtGruppenarbeiten() {
        return anzahlGruppenarbeiten;
    }

    /**
     * Gibt die Gesamtpunkte zurück.
     *
     * @return die Gesamtpunkte
     *
     * @author Leon
     */
    public Float getGesamtPunkte() {
        return gesamtPunkte;
    }

    /**
     * Setzt die Gesamtpunkte.
     *
     * @param gesamtPunkte die neuen Gesamtpunkte
     *
     * @author Leon
     */
    public void setGesamtPunkte(Float gesamtPunkte) {
        this.gesamtPunkte = gesamtPunkte;
    }

    /**
     * Gibt die Matrikelnummer zurück.
     *
     * @return die Matrikelnummer
     *
     * @author Leon
     */
    public String getNameMatrikelnummer() {
        return nameMatrikelnummer;
    }

    /**
     * Setzt die Matrikelnummer.
     *
     * @param nameMatrikelnummer die neue Matrikelnummer
     *
     * @author Leon
     */
    public void setNameMatrikelnummer(String nameMatrikelnummer) {
        this.nameMatrikelnummer = nameMatrikelnummer;
    }

    /**
     * Gibt die Liste der Punkte für die Gruppenarbeitn zurück.
     *
     * @return die Punkte
     *
     * @author Leon
     */
    public List<Float> getPunkte() {
        return punkte;
    }
    /**
     * Setzt die Punkte der Gruppenarbeiten.
     *
     * @param punkte die neuen Punkte für die Gruppenarbeiten
     *
     * @author Leon
     */
    public void setPunkte(List<Float> punkte) {
        this.punkte = punkte;
    }
    /**
     * Fügt der Liste der Punkte einen neuen Punkt hinzu.
     *
     * @param punkte der hinzuzufügende Punkt
     * @throws IllegalArgumentException wenn punkte null ist
     * @author Leon
     */
    public void addPunkte(Float punkte) {
        if (punkte == null) {
            throw new IllegalArgumentException("Der Punkt darf nicht null sein");
        }
        this.punkte.add(punkte);
    }

    /**
     * Gibt die Anzahl der Gruppenarbeiten zurück.
     *
     * @return die Anzahl der Gruppenarbeiten
     * @author Leon
     */
    public int getAnzahlGruppenarbeiten() {
        return anzahlGruppenarbeiten;
    }

    /**
     * Setzt die Anzahl der Gruppenarbeiten auf den angegebenen Wert.
     *
     * @param anzahlGruppenarbeiten die neue Anzahl der Gruppenarbeiten
     * @author Leon
     */
    public void setAnzahlGruppenarbeiten(int anzahlGruppenarbeiten) {
        this.anzahlGruppenarbeiten = anzahlGruppenarbeiten;
    }

    /**
     * Erhöht die Anzahl der Gruppenarbeiten um eins.
     *
     * @author Leon
     */
    public void incrementAnzahlGruppenarbeiten() {
        anzahlGruppenarbeiten++;
    }


}

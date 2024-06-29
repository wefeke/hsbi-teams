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

    private String vorname;
    private String nachname;
    private Long matrikelnummer;
    private Float gesamtPunkte = 0.0f;
    private List<TGGPHelper> tggpHelper;
    private final List<Long> gruppen = new ArrayList<>();
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
        gruppen.add(gruppe);
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
     * Gibt die Matrikelnummer zurück.
     *
     * @return die Matrikelnummer
     *
     * @author Leon
     */
    public String getNameMatrikelnummer() {
        return vorname + " " + nachname + " " + "(" + matrikelnummer + ")";
    }

    /**
     * Fügt der Liste der Punkte einen neuen Punkt hinzu.
     *
     * @param punkte der hinzuzufügende Punkt
     * @throws IllegalArgumentException wenn punkte null ist
     * @author Leon
     */
    public void addPunkte(Float punkte) {
        if (punkte == null) throw new IllegalArgumentException("Der Punkt darf nicht null sein");
        this.punkte.add(punkte);
    }

    /**
     * Erhöht die Anzahl der Gruppenarbeiten um eins.
     *
     * @author Leon
     */
    public void incrementAnzahlGruppenarbeiten() {
        anzahlGruppenarbeiten++;
    }

    /**
     * Gibt den Vornamen zurück.
     *
     * @return der Vorname
     *
     * @author Leon
     */

    public String getVorname() {
        return vorname;
    }

    /**
     * Setzt den Vornamen.
     *
     * @param vorname der Vorname der gesetz werden soll
     *
     * @author Leon
     */

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    /**
     * Gibt den Nachname zurück.
     *
     * @return der Nachname
     *
     * @author Leon
     */

    public String getNachname() {
        return nachname;
    }

    /**
     * Setzt den Vornamen.
     *
     * @param nachname der Nachname der gesetz werden soll
     *
     * @author Leon
     */

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    /**
     * Gibt den Nachname zurück.
     *
     * @return der Nachname
     *
     * @author Leon
     */
    public Long getMatrikelnummer() {
        return matrikelnummer;
    }

    /**
     * Setzt die Matrikelnummer.
     *
     * @param matrikelnummer die Matrikelnummer die gesetz werden soll
     *
     * @author Leon
     */

    public void setMatrikelnummer(Long matrikelnummer) {
        this.matrikelnummer = matrikelnummer;
    }
}

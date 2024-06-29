package com.example.application.views.kalender;

import java.util.ArrayList;
import java.util.List;

/**
 * Die Klasse KalenderAuswahl dient zur Verwaltung der verschiedenen Auswahlwerte für den Kalender.
 * Sie ermöglicht die Auswahl zwischen Tag, Woche und Monat.
 *
 * @autor Leon
 */
public class KalenderAuswahl {

    private final AuswahlWert auswahlWert;

    /**
     * Konstruktor mit Parameter zur Initialisierung des Auswahlwerts.
     *
     * @param auswahlWert der Auswahlwert für den Kalender
     *
     * @autor Leon
     */
    public KalenderAuswahl(AuswahlWert auswahlWert) {
        this.auswahlWert = auswahlWert;
    }

    /**
     * Überprüft, ob der aktuelle Auswahlwert "Tag" ist.
     *
     * @return true, wenn der Auswahlwert "Tag" ist, sonst false
     *
     * @autor Leon
     */
    public boolean istTag() {
        return auswahlWert == AuswahlWert.Tag;
    }

    /**
     * Überprüft, ob der aktuelle Auswahlwert "Monat" ist.
     *
     * @return true, wenn der Auswahlwert "Monat" ist, sonst false
     *
     * @autor Leon
     */
    public boolean istMonat() {
        return auswahlWert == AuswahlWert.Monat;
    }

    /**
     * Überprüft, ob der aktuelle Auswahlwert "Woche" ist.
     *
     * @return true, wenn der Auswahlwert "Woche" ist, sonst false
     *
     * @autor Leon
     */
    public boolean istWoche() {
        return auswahlWert == AuswahlWert.Woche;
    }

    /**
     * Gibt den aktuellen Auswahlwert als String zurück.
     *
     * @return der aktuelle Auswahlwert als String
     *
     * @autor Leon
     */
    @Override
    public String toString() {
        return auswahlWert.toString();
    }

    /**
     * Gibt eine Liste aller möglichen Auswahlwerte zurück.
     *
     * @return eine Liste aller möglichen Auswahlwerte
     *
     * @autor Leon
     */
    public static List<KalenderAuswahl> getAllWerte() {
        List<KalenderAuswahl> werte = new ArrayList<>();
        KalenderAuswahl wert01 = new KalenderAuswahl(AuswahlWert.Monat);
        KalenderAuswahl wert02 = new KalenderAuswahl(AuswahlWert.Woche);
        KalenderAuswahl wert03 = new KalenderAuswahl(AuswahlWert.Tag);
        werte.add(wert01);
        werte.add(wert02);
        werte.add(wert03);
        return werte;
    }
}

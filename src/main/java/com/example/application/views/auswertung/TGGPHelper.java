package com.example.application.views.auswertung;

import com.example.application.models.Gruppe;
import com.example.application.models.Gruppenarbeit;
import com.example.application.models.Teilnehmer;
import com.example.application.models.Veranstaltungstermin;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * TGGPHelper ist eine Hilfsklasse, die das Akronym für Termin-Gruppenarbeit-Gruppe-Punkte darstellt.
 * Sie dient zur Vereinfachung der Darstellung in einer Tabelle, ohne das Datenbankmodell verändern zu müssen.
 * Die Klasse hilft dabei, die jeweiligen Daten pro Spalte in der Auswertung anzugeben.
 *
 * @author Leon
 */
public class TGGPHelper {
    Veranstaltungstermin veranstaltungtermin;
    Gruppenarbeit gruppenarbeit;
    Gruppe gruppe;
    List<Teilnehmer> teilnehmer;
    Float punkte = 0f;
    private int tablePos = 0;
    boolean used;


    /**
     * Gibt die Tabellenposition zurück.
     *
     * @return die Tabellenposition
     *
     * @author Leon
     */
    public int getTablePos() {
        return tablePos;
    }

    /**
     * Setzt die Tabellenposition.
     *
     * @param tablePos die neue Tabellenposition
     *
     * @author Leon
     */
    public void setTablePos(int tablePos) {
        this.tablePos = tablePos;
    }

    /**
     * Gibt den Veranstaltungstermin zurück.
     *
     * @return der Veranstaltungstermin
     *
     * @author Leon
     */
    public Veranstaltungstermin getVeranstaltungtermin() {
        return veranstaltungtermin;
    }

    /**
     * Setzt den Veranstaltungstermin.
     *
     * @param veranstaltungtermin der neue Veranstaltungstermin
     *
     * @author Leon
     */
    public void setVeranstaltungtermin(Veranstaltungstermin veranstaltungtermin) {
        this.veranstaltungtermin = veranstaltungtermin;
    }

    /**
     * Gibt den Titel der Gruppenarbeit und das Datum des Veranstaltungstermins zurück.
     *
     * @return der Titel der Gruppenarbeit und das Datum des Veranstaltungstermins
     *
     * @author Leon
     */
    public String getTerminAndGruppenarbeit() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return gruppenarbeit.getTitel() + " " +veranstaltungtermin.getDatum().format(dateFormatter);
    }

    /**
     * Gibt die Gruppenarbeit zurück.
     *
     * @return die Gruppenarbeit
     *
     * @author Leon
     */
    public Gruppenarbeit getGruppenarbeit() {
        return gruppenarbeit;
    }

    /**
     * Setzt die Gruppenarbeit.
     *
     * @param gruppenarbeit die neue Gruppenarbeit
     *
     * @author Leon
     */
    public void setGruppenarbeit(Gruppenarbeit gruppenarbeit) {
        this.gruppenarbeit = gruppenarbeit;
    }

    /**
     * Gibt die erste Gruppennummer in der Liste zurück, falls vorhanden.
     *
     * @return die erste Gruppennummer oder null, wenn die Liste leer ist
     *
     * @author Leon
     */
    public Gruppe getGruppe() {
        if (gruppe != null) {
            return gruppe;
        } else {
            return null;
        }
    }
    /**
     * Gibt true zurück, falls eine Gruppe existiert
     *
     * @return true wenn Bedingung nicht leer, andernfalls false
     *
     * @author Leon
     */
    public boolean isGruppeEmpty() {
        if (gruppe == null)
        return true;
        else {
            return false;
        }
    }

    /**
     * Gibt die Punkte zurück.
     *
     * @return die Punkte
     *
     * @author Leon
     */
    public Float getPunkte() {
        return punkte;
    }

    /**
     * Setzt die Punkte.
     *
     * @param punkte die neuen Punkte
     *
     * @author Leon
     */
    public void setPunkte(Float punkte) {
        this.punkte = punkte;
    }

    /**
     * Überprüft, ob Punkte vorhanden sind.
     *
     * @return true, wenn Punkte >= 0 sind, sonst false
     *
     * @author Leon
     */
    public boolean hasPunkte() {
        return punkte >= 0f;
    }
    /**
     * Gibt den Zustand zurück, ob die Gruppe verwendet wird.
     *
     * @return true, wenn verwendet, sonst false
     *
     * @author Leon
     */
    public boolean getUsed() {
        return used;
    }

    /**
     * Setzt den Zustand auf verwendet (true).
     *
     * @author Leon
     */
    public void setUsedTrue() {
        this.used = true;
    }

    /**
     * Setzt den Zustand auf nicht verwendet (false).
     *
     * @author Leon
     */
    public void setUsedFalse() {
        this.used = false;
    }

    /**
     * Setzt die Gruppe
     *
     * @param gruppe die Gruppe
     *
     * @author Leon
     */
    public void setGruppe(Gruppe gruppe) {
        this.gruppe = gruppe;
    }

    /**
     * Gibt die Liste der Teilnehmer zurück.
     *
     * @return die Liste der Teilnehmer
     *
     * @author Leon
     */
    public List<Teilnehmer> getTeilnehmer() {
        return teilnehmer;
    }

    /**
     * Setzt die Liste der Teilnehmer.
     *
     * @param teilnehmer die neue Liste der Teilnehmer
     *
     * @author Leon
     */
    public void setTeilnehmer(List<Teilnehmer> teilnehmer) {
        this.teilnehmer = teilnehmer;
    }
}

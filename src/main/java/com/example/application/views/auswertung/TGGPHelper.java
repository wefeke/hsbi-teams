package com.example.application.views.auswertung;

import com.example.application.models.Gruppe;
import com.example.application.models.Gruppenarbeit;
import com.example.application.models.Veranstaltungstermin;


// TGGP ist das Akronym für Termin-Gruppenarbeit-Gruppe-Punkte und dient als Helper-Klasse, um eine
// Repräsentation in einer Tabelle zu vereinfachen. Dabei ohne das Datenbankmodel verändern zu müssen,
// hilft es pro Spalte in der Auswertung die jeweiligen Daten anzugeben
public class TGGPHelper {
    Veranstaltungstermin veranstaltungtermin;
    Gruppenarbeit gruppenarbeit;
    Gruppe gruppe;
    Float punkte = 0f;
    private int tablePos = 0;

    public int getTablePos() {
        return tablePos;
    }

    public void setTablePos(int tablePos) {
        this.tablePos = tablePos;
    }

    public Veranstaltungstermin getVeranstaltungtermin() {
        return veranstaltungtermin;
    }

    public void setVeranstaltungtermin(Veranstaltungstermin veranstaltungtermin) {
        this.veranstaltungtermin = veranstaltungtermin;
    }

    public String getTerminAndGruppenarbeit() {
        return veranstaltungtermin.getDatum().toString() + ", "+ gruppenarbeit.getTitel().toString();
    }

    public Gruppenarbeit getGruppenarbeit() {
        return gruppenarbeit;
    }

    public void setGruppenarbeit(Gruppenarbeit gruppenarbeit) {
        this.gruppenarbeit = gruppenarbeit;
    }

    public Gruppe getGruppe() {
        return gruppe;
    }

    public void setGruppe(Gruppe gruppe) {
        this.gruppe = gruppe;
    }

    public Float getPunkte() {
        return punkte;
    }

    public void setPunkte(Float punkte) {
        this.punkte = punkte;
    }

    public boolean hasPunkte() {
        if (punkte >= 0f) {
            return true;
        } else {
            return false;
        }
    }

    public String getGruppeAndCheckmark() {
        if (gruppe != null) {
            String unicodeString = "\u2705";
                return "Gruppe " + gruppe.getNummer();
        } else {
         return  "Gehört zu keiner Gruppe!";
        }
    }
}

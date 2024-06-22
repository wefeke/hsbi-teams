package com.example.application.views.auswertung;

import com.example.application.models.Gruppe;
import com.example.application.models.Gruppenarbeit;
import com.example.application.models.Teilnehmer;
import com.example.application.models.Veranstaltungstermin;

import java.util.ArrayList;
import java.util.List;


// TGGP ist das Akronym für Termin-Gruppenarbeit-Gruppe-Punkte und dient als Helper-Klasse, um eine
// Repräsentation in einer Tabelle zu vereinfachen. Dabei ohne das Datenbankmodel verändern zu müssen,
// hilft es pro Spalte in der Auswertung die jeweiligen Daten anzugeben
public class TGGPHelper {
    Veranstaltungstermin veranstaltungtermin;
    Gruppenarbeit gruppenarbeit;
    List<Gruppe> gruppe = new ArrayList<>();
    List<Teilnehmer> teilnehmer;
    Float punkte = 0f;
    private int tablePos = 0;
    boolean used;

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
        if (!gruppe.isEmpty()) {
            return gruppe.getFirst();
        } else {
            return null;
        }
    }

    public void addGruppe(Gruppe gruppe) {
        this.gruppe.add(gruppe);
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
        if (!gruppe.isEmpty()) {
            String unicodeString = "\u2705";
            String res = ""+gruppe.getFirst();
            gruppe.remove(gruppe.getFirst());
                return res;
        } else {
         return  "Gehört zu keiner Gruppe!";
        }
    }

    public boolean getUsed() {
    return used;
    }

    public void setUsedTrue() {
        this.used = true;
    }

    public void setUsedFalse() {
        this.used = false;
    }

    public void setGruppe(List<Gruppe> gruppe) {
        this.gruppe = gruppe;
    }

    public List<Teilnehmer> getTeilnehmer() {
        return teilnehmer;
    }

    public void setTeilnehmer(List<Teilnehmer> teilnehmer) {
        this.teilnehmer = teilnehmer;
    }
}

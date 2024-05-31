//Autor: Kennet
package com.example.application.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Teilnehmer {
    @Id

    @Column(name = "matrikelNr", nullable = false)
    private Long matrikelNr;
    private String vorname;
    private String nachname;

    //Beziehungen
    @ManyToMany (fetch = FetchType.EAGER)
    private List<Veranstaltung> veranstaltungen = new ArrayList<>();
    @ManyToMany
    private List<Gruppenarbeit> gruppenarbeit = new ArrayList<>();
    @ManyToMany
    private List<Gruppe> gruppen = new ArrayList<>();

    public Long getId() {
        return matrikelNr;
    }

    public void setId(Long matrikelNummer) {
        this.matrikelNr = matrikelNummer;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public Teilnehmer() {

    }

    public Teilnehmer(String vorname, String nachname){
        this.vorname = vorname;
        this.nachname = nachname;
    }

    public List<Veranstaltung> getVeranstaltungen() {
        return veranstaltungen;
    }

    public void setVeranstaltungen(List<Veranstaltung> veranstaltungen) {
        this.veranstaltungen = veranstaltungen;
    }

    public void addVerastaltung (Veranstaltung veranstaltung){
        this.veranstaltungen.add(veranstaltung);
    }

    public String toString(){
        return this.vorname + " " + this.nachname;
    }

    public List<Gruppe> getGruppen() {return gruppen;}

    public List<Gruppenarbeit> getGruppenarbeit() {
        return gruppenarbeit;
    }
}

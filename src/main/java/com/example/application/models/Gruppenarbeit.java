//Autor: Kennet
package com.example.application.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Gruppenarbeit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;
    private String beschreibung;
    private String titel;

    //Beziehungen
    @ManyToOne
    private Veranstaltungstermin veranstaltungstermin = new Veranstaltungstermin();
    @OneToMany(fetch = FetchType.EAGER)
    private List<Gruppe> gruppen = new ArrayList<>();
    @ManyToMany
    private List<Teilnehmer> teilnehmer = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public void setVeranstaltungstermin(Veranstaltungstermin veranstaltungstermin) {
        this.veranstaltungstermin = veranstaltungstermin;
    }

    public void setGruppe(List<Gruppe> gruppen) {
        this.gruppen = gruppen;
    }

    public List<Gruppe> getGruppen() {
        return gruppen;
    }
}

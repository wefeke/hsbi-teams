//Autor: Kennet
package com.example.application.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Gruppenarbeit {
    @Id
    @GeneratedValue(generator = "generator")
    @SequenceGenerator(name="generator", sequenceName = "GENERATOR", allocationSize = 50, initialValue = 100)
    @Column(name = "id", nullable = false)
    private Long id;
    private String beschreibung;
    private String titel;

    //Beziehungen
    @ManyToOne()
    private User user;
    @ManyToOne
    private Veranstaltungstermin veranstaltungstermin = new Veranstaltungstermin();
    @OneToMany(fetch = FetchType.EAGER)
    private List<Gruppe> gruppen = new ArrayList<>();
    @ManyToMany
    private List<Teilnehmer> teilnehmer = new ArrayList<>();

    public Gruppenarbeit(){
    }

    public Gruppenarbeit(String beschreibung, String titel, User user, Veranstaltungstermin veranstaltungstermin) {
        this.beschreibung = beschreibung;
        this.titel = titel;
        this.user = user;
        this.veranstaltungstermin = veranstaltungstermin;
    }

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

    public void setGruppe(List<Gruppe> gruppen){
        this.gruppen = gruppen;
    }

    public void setTeilnehmer(List<Teilnehmer> teilnehmer){
        this.teilnehmer = teilnehmer;
    }

    public List<Gruppe> getGruppen() {
        return new ArrayList<>(gruppen);
    }

    public List<Teilnehmer> getTeilnehmer() {
        return teilnehmer;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void removeGruppe(Gruppe gruppe){
        this.gruppen.remove(gruppe);
    }

    public void removeAllGruppen(){
        this.gruppen = new ArrayList<>();
    }

    public void removeVeranstaltungstermin() {
        this.veranstaltungstermin = null;
    }

    public Veranstaltungstermin getVeranstaltungstermin() {
        return this.veranstaltungstermin;
    }
}

//Autor: Kennet
package com.example.application.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Veranstaltungstermin {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    private LocalDate datum;
    private LocalTime startZeit;
    private LocalTime endZeit;
    private String ort;
    private String notizen;

    //Beziehungen
    @ManyToOne()
    private User user;
    @ManyToOne
    private Veranstaltung veranstaltung;
    @OneToMany(fetch = FetchType.EAGER)
    private List<Gruppenarbeit> gruppenarbeiten = new ArrayList<>();

    public Veranstaltungstermin() {

    }

    public Veranstaltungstermin(LocalDate datum, LocalTime startZeit, LocalTime endZeit, String ort, String notizen, User user, Veranstaltung veranstaltung) {
        this.datum = datum;
        this.startZeit = startZeit;
        this.endZeit = endZeit;
        this.ort = ort;
        this.notizen = notizen;
        this.user = user;
        this.veranstaltung = veranstaltung;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public void setDatum(LocalDate datum) {
        this.datum = datum;
    }

    public Veranstaltung getVeranstaltung() {
        return veranstaltung;
    }

    public void setVeranstaltung(Veranstaltung veranstaltung) {
        this.veranstaltung = veranstaltung;
    }

    public String getNotizen() {
        return notizen;
    }

    public void setNotizen(String notizen) {
        this.notizen = notizen;
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public LocalTime getStartZeit() {
        return startZeit;
    }

    public void setStartZeit(LocalTime startZeit) {
        this.startZeit = startZeit;
    }

    public LocalTime getEndZeit() {
        return endZeit;
    }

    public void setEndZeit(LocalTime endZeit) {
        this.endZeit = endZeit;
    }

    public List<Gruppenarbeit> getGruppenarbeiten() {
        return gruppenarbeiten;
    }

    public void addGruppenarbeit (Gruppenarbeit gruppenarbeit) {
        this.gruppenarbeiten.add(gruppenarbeit);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    //Lilli
    public void removeGruppenarbeit(Gruppenarbeit gruppenarbeit) {
        this.gruppenarbeiten.remove(gruppenarbeit);
    }
}

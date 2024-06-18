//Autor: Kennet
package com.example.application.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private String titel;

    //Beziehungen
    @ManyToOne()
    private User user;
    @ManyToOne
    private Veranstaltung veranstaltung;
    @OneToMany(mappedBy = "veranstaltungstermin", fetch = FetchType.EAGER)
    private List<Gruppenarbeit> gruppenarbeiten = new ArrayList<>();

    public Veranstaltungstermin() {

    }

    public Veranstaltungstermin(LocalDate datum, LocalTime startZeit, LocalTime endZeit, String ort, String titel, User user, Veranstaltung veranstaltung) {
        this.datum = datum;
        this.startZeit = startZeit;
        this.endZeit = endZeit;
        this.ort = ort;
        this.titel = titel;
        this.user = user;
        this.veranstaltung = veranstaltung;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Veranstaltungstermin that = (Veranstaltungstermin) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
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
        return new ArrayList<>(gruppenarbeiten);
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

    //Lilli
    public void removeAllGruppenarbeiten(){
        this.gruppenarbeiten = new ArrayList<Gruppenarbeit>();
    }
}

package com.example.application.models;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Die Teilnehmer Klasse repräsentiert einen Teilnehmer in der Anwendung.
 * Sie enthält Informationen wie Matrikelnummer, Vorname, Nachname und den Zeitpunkt, wann der Teilnehmer hinzugefügt wurde.
 * Sie hat Beziehungen zu den Klassen User, Veranstaltung, Gruppenarbeit und Gruppe, die den Benutzer, die Veranstaltungen, die Gruppenarbeiten und die Gruppen repräsentieren, an denen der Teilnehmer teilnimmt.
 * Sie enthält auch Methoden zum Abrufen und Setzen dieser Informationen sowie zum Hinzufügen und Entfernen von Gruppenarbeiten.
 *
 * @author Tobias
 */
@Entity
public class Teilnehmer {
    @Id
    @Column(name = "matrikelNr", nullable = false)
    private Long matrikelNr;
    private String vorname;
    private String nachname;

    // für Timestamp
    @CreationTimestamp
    @Column(name = "hinzugefügt am", updatable = false)
    private LocalDateTime hinzugefuegtAm;

    //Beziehungen
    @ManyToOne()
    private User user;
    @ManyToMany (mappedBy = "teilnehmer", fetch = FetchType.EAGER)
    private List<Veranstaltung> veranstaltungen = new ArrayList<>();
    @ManyToMany (mappedBy = "teilnehmer", fetch = FetchType.EAGER)
    private final List<Gruppenarbeit> gruppenarbeiten = new ArrayList<>();
    @ManyToMany (mappedBy = "teilnehmer", fetch = FetchType.EAGER)
    private final List<Gruppe> gruppen = new ArrayList<>();

    public Teilnehmer() {
    }

    public Teilnehmer(Long matrikelNr, String vorname, String nachname, User user){
        this.matrikelNr = matrikelNr;
        this.vorname = vorname;
        this.nachname = nachname;
        this.user = user;
    }

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

    @Override
    public String toString(){
        return this.matrikelNr + " " + this.vorname + " " + this.nachname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Teilnehmer that = (Teilnehmer) o;
        return Objects.equals(matrikelNr, that.matrikelNr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matrikelNr);
    }

    public List<Gruppe> getGruppen() {
        return gruppen;
    }

    public List<Gruppenarbeit> getGruppenarbeiten() {
        return gruppenarbeiten;
    }

    public String getFullName() {
        return this.vorname + " " + this.nachname;
    }

    public void addGruppenarbeit(Gruppenarbeit gruppenarbeit){
        this.gruppenarbeiten.add(gruppenarbeit);
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser () {
        return user;
    }

    public void removeGruppenarbeit(Gruppenarbeit gruppenarbeit) {
        this.gruppenarbeiten.remove(gruppenarbeit);
    }
}

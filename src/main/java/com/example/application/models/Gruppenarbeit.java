//Autor: Kennet
package com.example.application.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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
    @ManyToOne()
    private Veranstaltungstermin veranstaltungstermin = new Veranstaltungstermin();
    @OneToMany(mappedBy = "gruppenarbeit",fetch = FetchType.EAGER)
    private List<Gruppe> gruppen = new ArrayList<>();
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Teilnehmer> teilnehmer = new ArrayList<>();

    public Gruppenarbeit(){
    }

    public Gruppenarbeit(String beschreibung, String titel, User user, Veranstaltungstermin veranstaltungstermin) {
        this.beschreibung = beschreibung;
        this.titel = titel;
        this.user = user;
        this.veranstaltungstermin = veranstaltungstermin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Gruppenarbeit that = (Gruppenarbeit) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
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
        return new ArrayList<Teilnehmer>(teilnehmer);
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

    public void addAllGruppen(Collection<Gruppe> gruppenCollection) {
        this.gruppen.addAll(gruppenCollection);
    }

    public void removeAllTeilnehmer() {
        this.teilnehmer = new ArrayList<Teilnehmer>();
    }

    public void addTeilnehmer(Teilnehmer teilnehmer) {
        this.teilnehmer.add(teilnehmer);
    }

}



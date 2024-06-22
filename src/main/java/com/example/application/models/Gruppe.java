//Autor: Kennet
package com.example.application.models;

import jakarta.persistence.*;

import java.util.*;

@Entity
public class Gruppe {
    @Id
    @GeneratedValue(generator = "generator")
    @SequenceGenerator(name="generator", sequenceName = "GENERATOR", allocationSize = 50, initialValue = 100)
    @Column(name = "id", nullable = false)
    private Long id;
    private Long nummer;

    //Beziehungen
    @ManyToOne()
    private User user;
    @ManyToOne()
    private Gruppenarbeit gruppenarbeit = new Gruppenarbeit();
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Teilnehmer> teilnehmer = new HashSet<>();

    public Gruppe(){
    }

    public Gruppe(Long nummer){
        this.nummer = nummer;
        this.gruppenarbeit = null;
        this.teilnehmer = new HashSet<>();
    }

    public Gruppe(Long nummer, User user){
        this.nummer = nummer;
        this.user = user;
        this.gruppenarbeit = null;
        this.teilnehmer = new HashSet<>();
    }


    public Gruppenarbeit getGruppenarbeit() {
        return gruppenarbeit;
    }

    public void setGruppenarbeit(Gruppenarbeit gruppenarbeit) {
        this.gruppenarbeit = gruppenarbeit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNummer() {
        return nummer;
    }

    public void setNummer(Long nummer) {
        this.nummer = nummer;
    }

    public String toString(){
        return "Gruppe " + this.nummer;
    }

    public Set<Teilnehmer> getTeilnehmer() {
        return this.teilnehmer;
    }

    public void addTeilnehmer(Teilnehmer teilnehmer) {
        this.teilnehmer.add(teilnehmer);
    }

    public void setUser(User user){
        this.user = user;
    }

    public void removeAllTeilnehmer(){
        this.teilnehmer = new HashSet<>();
    }

    public void addAllTeilnehmer(Collection<Teilnehmer> teilnehmerCollection){
        this.teilnehmer.addAll(teilnehmerCollection);
    }
}

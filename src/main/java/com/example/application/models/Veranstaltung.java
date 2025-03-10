//Autor: Joris
package com.example.application.models;

import jakarta.persistence.*;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

/**
 * Die Veranstaltung Klasse repräsentiert eine Veranstaltung in der Anwendung.
 * Sie enthält Informationen wie Semester, Titel und hat Beziehungen zu den Klassen User, Veranstaltungstermin und Teilnehmer.
 * Sie enthält auch Methoden zum Hinzufügen und Entfernen von Veranstaltungsterminen und Teilnehmern.
 *
 * @author Joris
 */
@Entity
public class Veranstaltung implements Serializable {

    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    @GeneratedValue(generator = "generator")
    @SequenceGenerator(name="generator", sequenceName = "GENERATOR", allocationSize = 50, initialValue = 100)
    private Long id; // Primary Key
    private LocalDate semester;          // Semester-Nummer
    private String titel;

    //Beziehungen
    @ManyToOne()
    private User user;
    @OneToMany(mappedBy = "veranstaltung", fetch = FetchType.EAGER)
    private List<Veranstaltungstermin> veranstaltungstermine = new ArrayList<>();
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Teilnehmer> teilnehmer = new HashSet<>();

    public Veranstaltung() {
    }

    public Veranstaltung(LocalDate semester, String titel, User user, List<Veranstaltungstermin> veranstaltungstermine, Set<Teilnehmer> teilnehmer) {
        this.semester = semester;
        this.titel = titel;
        this.user = user;
        this.veranstaltungstermine = veranstaltungstermine;
        this.teilnehmer = teilnehmer;
    }

    public Veranstaltung(Long id, LocalDate semester, String titel, User user) {
        this.id = id;
        this.semester = semester;
        this.titel = titel;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getSemester() {
        return semester;
    }

    public void setSemester(LocalDate semester) {
        this.semester = semester;
    }

    public String getTitel() {
        return titel;
    }
    public void setTitel(String titel) {
        this.titel = titel;
    }

    @Transactional
    public void setUser(User user){
        this.user = user;
    }

    @SuppressWarnings("PatternVariableHidesField")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Veranstaltung semester)) return false;
        return Objects.equals(id, semester.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Semester{" +
                "id=" + id +
                ", semester=" + semester +
                ", titel='" + titel + '\'' +
                '}';
    }

    public Set<Teilnehmer> getTeilnehmer() {
        return new HashSet<>(teilnehmer);
    }

    public void setTeilnehmer(Set<Teilnehmer> teilnehmer) {
        this.teilnehmer = teilnehmer;
    }


    public void addAllTeilnehmer(Set<Teilnehmer> teilnehmer) {
        this.teilnehmer.addAll(teilnehmer);
    }

    public List<Veranstaltungstermin> getVeranstaltungstermine() {
        return new ArrayList<>(veranstaltungstermine);
    }

    public void addVeranstaltungstermin(Veranstaltungstermin veranstaltungstermin) {
        this.veranstaltungstermine.add(veranstaltungstermin);
    }

    public void removeAllTermine() {
        this.veranstaltungstermine = new ArrayList<>();
    }

    public void removeAllTeilnehmer() {
        this.teilnehmer = new HashSet<>();
    }
}
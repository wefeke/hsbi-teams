//Autor: Joris
package com.example.application.models;

import com.vaadin.flow.component.html.Div;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

@Entity
public class Veranstaltung implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; // Primary Key
    private LocalDate semester;          // Semester-Nummer
    private String titel;

    //Beziehungen
    @ManyToOne()
    private User user;
    @OneToMany(fetch = FetchType.EAGER)
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

    // Getter und Setter
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

    public void setUser(User user){
        this.user = user;
    }

    // equals und hashCode Methoden
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Veranstaltung)) return false;
        Veranstaltung semester = (Veranstaltung) o;
        return Objects.equals(id, semester.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // toString Methode, um das Objekt als String darzustellen, nützlich für Logging
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

    public void addTeilnehmer(Teilnehmer teilnehmer) {
        this.teilnehmer.add(teilnehmer);
    }

    public List<Veranstaltungstermin> getVeranstaltungstermine() {
        return new ArrayList<Veranstaltungstermin>(veranstaltungstermine);
    }

    public void setVeranstaltungstermine(List<Veranstaltungstermin> veranstaltungstermine) {
        this.veranstaltungstermine = veranstaltungstermine;
    }

    public void addVeranstaltungstermin(Veranstaltungstermin veranstaltungstermin) {
        this.veranstaltungstermine.add(veranstaltungstermin);
    }

    //Lilli
    public void removeVeranstaltungstermin(Veranstaltungstermin veranstaltungstermin){
        this.veranstaltungstermine.remove(veranstaltungstermin);
    }

    public void removeAllTermine() {
        this.veranstaltungstermine = new ArrayList<Veranstaltungstermin>();
    }

    public void removeAllTeilnehmer() {
        this.teilnehmer = new HashSet<Teilnehmer>();
    }
}
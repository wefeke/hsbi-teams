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
    private Long veranstaltungsId; // Primary Key
    private LocalDate semester;          // Semester-Nummer
    private String titel;

    //Beziehungen
    @ManyToOne()
    private User user = new User();
    @OneToMany(fetch = FetchType.EAGER)
    private List<Veranstaltungstermin> veranstaltungstermine = new ArrayList<>();
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Teilnehmer> teilnehmer = new HashSet<>();

    public Veranstaltung() {
    }

    public Veranstaltung(Long veranstaltungsId, LocalDate semester, String titel) {
        this.veranstaltungsId = veranstaltungsId;
        this.semester = semester;
        this.titel = titel;
    }

    // Getter und Setter
    public Long getVeranstaltungsId() {
        return veranstaltungsId;
    }

    public void setVeranstaltungsId(Long veranstaltungsId) {
        this.veranstaltungsId = veranstaltungsId;
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
        return Objects.equals(veranstaltungsId, semester.veranstaltungsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(veranstaltungsId);
    }

    // toString Methode, um das Objekt als String darzustellen, nützlich für Logging
    @Override
    public String toString() {
        return "Semester{" +
                "veranstaltungsId=" + veranstaltungsId +
                ", semester=" + semester +
                ", titel='" + titel + '\'' +
                '}';
    }

    public Set<Teilnehmer> getTeilnehmer() {
        return teilnehmer;
    }

    public void setTeilnehmer(Set<Teilnehmer> teilnehmer) {
        this.teilnehmer = teilnehmer;
    }

    public void addTeilnehmer(Teilnehmer teilnehmer) {
        this.teilnehmer.add(teilnehmer);
    }

    public List<Veranstaltungstermin> getVeranstaltungstermine() {
        return veranstaltungstermine;
    }

    public void setVeranstaltungstermine(List<Veranstaltungstermin> veranstaltungstermine) {
        this.veranstaltungstermine = veranstaltungstermine;
    }

    public void addVeranstaltungstermin(Veranstaltungstermin veranstaltungstermin) {
        this.veranstaltungstermine.add(veranstaltungstermin);
    }

}
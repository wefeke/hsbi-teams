package com.example.application.data;

import java.io.Serializable;
import java.util.Objects;

public class Veranstaltung implements Serializable {

    private Long veranstaltungsId; // Primary Key
    private int semester;      // Semester-Nummer
    private String titel;          // Titel der Veranstaltung

    public Veranstaltung(Long veranstaltungsId, Integer semester, String titel) {
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

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
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
}


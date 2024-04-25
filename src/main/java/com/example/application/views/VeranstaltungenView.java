package com.example.application.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.Arrays;
import java.util.List;

@PageTitle("Veranstaltungen")
@Route(value = "")
public class VeranstaltungenView extends VerticalLayout {

    public VeranstaltungenView() {
        // Erstellen des Haupt-Layouts für die Seite
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setSizeFull();

        // Linke Seite mit Semestern und Veranstaltungen
        VerticalLayout leftLayout = new VerticalLayout();
        // Beispieldaten für Semester
        List<Semester> semesterListe = Arrays.asList(
                new Semester("Informatik 1", 1),
                new Semester("Mathematik für Informatiker", 1),
                new Semester("Algorithmen und Datenstrukturen", 2),
                new Semester("Datenbanken", 2)
        );
        Grid<Semester> semesterGrid = new Grid<>(Semester.class);
        semesterGrid.setItems(semesterListe);
        semesterGrid.addColumn(Semester::getName).setHeader("Veranstaltung");
        semesterGrid.addColumn(Semester::getSemester).setHeader("Semester");

        // Rechte Seite mit Terminliste
        VerticalLayout rightLayout = new VerticalLayout();
        // Beispieldaten für Termine
        List<String> termineListe = Arrays.asList(
                "Treffen mit Studienberater - 14:00",
                "Tutorium Algorithmen - 16:00",
                "Klausur Mathematik - 10:00"
        );
        ListBox<String> termineListBox = new ListBox<>();
        termineListBox.setItems(termineListe);

        // Füge die Layouts zum Hauptlayout hinzu
        leftLayout.add(semesterGrid);
        rightLayout.add(termineListBox);

        mainLayout.add(leftLayout, rightLayout);

        // Füge das Hauptlayout zur View hinzu
        add(mainLayout);
    }

    // Datenmodellklasse für Semester
    public static class Semester {
        private String name;
        private int semester;

        public Semester(String name, int semester) {
            this.name = name;
            this.semester = semester;
        }

        public String getName() {
            return name;
        }

        public int getSemester() {
            return semester;
        }
    }

    // ...

}

package com.example.application.views.studierende;

import com.example.application.ExcelReader.ExcelImporter;
import com.example.application.models.Teilnehmer;
import com.example.application.models.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.TeilnehmerService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import org.vaadin.lineawesome.LineAwesomeIcon;
import java.util.*;

/**
 * Diese Klasse repräsentiert einen Dialog zum Importieren von Studierenden aus einer Excel-Datei.
 * Sie erbt von der Dialog-Klasse von Vaadin und bietet eine Benutzeroberfläche zum Hochladen einer Excel-Datei und zum Importieren der darin enthaltenen Studierenden.
 * Der Dialog enthält einen Upload-Bereich für die Excel-Datei und Schaltflächen zum Importieren der Studierenden und zum Schließen des Dialogs.
 * Bei erfolgreichem Hochladen einer Datei werden die Studierendendaten aus der Datei gelesen und in die Datenbank eingefügt.
 * Die Klasse verwendet einen TeilnehmerService zum Speichern der neuen Studierenden und einen AuthenticatedUser zur Authentifizierung.
 * Sie enthält auch eine Referenz auf eine StudierendeView, die aktualisiert wird, wenn neue Studierende hinzugefügt werden.
 *
 * @author Tobias
 */
public class StudierendeImportDialog extends Dialog {

    Button importButton = new Button("Importieren");
    Button closeButton = new Button("Schließen");

    MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
    ExcelImporter excelImporter;
    Set<Teilnehmer> newTeilnehmerListe = new HashSet<>();

    /**
     * Konstruktor für die StudierendeImportDialog Klasse.
     * Initialisiert den ExcelImporter und setzt den Header-Titel.
     * Fügt einen Click-Listener zum Import-Button hinzu, der einen Dialog öffnet und neue Teilnehmer hinzufügt.
     * Fügt einen Click-Listener zum Close-Button hinzu, der den Dialog schließt.
     * Fügt einen SucceededListener zum Upload hinzu, der die Excel-Datei liest und die neuen Teilnehmer hinzufügt.
     * Konfiguriert den Upload-Button und das Drop-Label.
     * Fügt den Upload zur Layout-Komponente hinzu und fügt diese zur Dialog-Komponente hinzu.
     * Fügt den Import-Button und den Close-Button zur Footer-Komponente des Dialogs hinzu.
     *
     * @param teilnehmerService Der Service, der für die Verwaltung der Studierenden benötigt wird.
     * @param authenticatedUser Der aktuell authentifizierte Benutzer.
     * @param studierendeView   Die StudierendeView, die aktualisiert wird, wenn neue Teilnehmer hinzugefügt werden.
     * @author Tobias
     */
    public StudierendeImportDialog(TeilnehmerService teilnehmerService, AuthenticatedUser authenticatedUser, StudierendeView studierendeView) {
        this.excelImporter = new ExcelImporter(teilnehmerService, authenticatedUser);

        H2 headerTitle = new H2("Studierende Importieren");
        add(headerTitle);

        importButton.addClickListener(event -> {

            for (Teilnehmer teilnehmer : newTeilnehmerListe) {
                Optional<User> maybeUser = authenticatedUser.get();
                if (maybeUser.isPresent()) {
                    User user = maybeUser.get();
                    teilnehmerService.saveTeilnehmer(teilnehmer, user);

                }
            }
            studierendeView.updateStudierendeView();
            close();

        });

        closeButton.addClickListener(event -> this.close());
        Upload upload = new Upload(buffer);
        upload.setUploadButton(new Button(LineAwesomeIcon.UPLOAD_SOLID.create()));
        upload.setDropLabelIcon(LineAwesomeIcon.ID_CARD.create());
        upload.setDropLabel(new Span("Teilnehmer Excel-Datei"));
        upload.setAcceptedFileTypes(".xlsx");


        VerticalLayout layout = new VerticalLayout(upload);
        add(layout);

        this.getFooter().add(importButton, closeButton);

        upload.addSucceededListener(event -> {
            try {

                newTeilnehmerListe.addAll(excelImporter.readNewTeilnehmerFromExcel(buffer.getInputStream(event.getFileName())));

                Optional<User> maybeUser = authenticatedUser.get();
                if (maybeUser.isPresent()) {
                    Dialog dialog = new Dialog();
                    dialog.setHeight(getHeight());
                    dialog.setHeaderTitle(newTeilnehmerListe.size() + " neue Teilnehmer gefunden");
                    dialog.getFooter().add(new Button("OK", e -> dialog.close()));

                    if (!newTeilnehmerListe.isEmpty()) {
                        Grid<Teilnehmer> grid = new Grid<>();
                        grid.setItems(newTeilnehmerListe);
                        grid.addColumn(Teilnehmer::getId).setHeader("MatrikelNr").setSortable(true).setAutoWidth(true);
                        grid.addColumn(Teilnehmer::getVorname).setHeader("Vorname").setSortable(true).setAutoWidth(true);
                        grid.addColumn(Teilnehmer::getNachname).setHeader("Nachname").setSortable(true).setAutoWidth(true);
                        grid.setWidth("70vw");

                        dialog.setWidth(grid.getWidth());
                        dialog.add(grid);
                        dialog.open();
                    }

                    System.out.println("List at end: " + newTeilnehmerListe.toString());
                }
            } catch (Exception e) {
                Notification.show("Error reading Excel file: " + e.getMessage());
                System.out.println(e.getMessage());
            }
        });
    }
}

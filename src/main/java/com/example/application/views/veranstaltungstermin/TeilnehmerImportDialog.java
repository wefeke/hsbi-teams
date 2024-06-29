package com.example.application.views.veranstaltungstermin;

import com.example.application.ExcelReader.ExcelImporter;
import com.example.application.models.Teilnehmer;
import com.example.application.models.User;
import com.example.application.models.Veranstaltung;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.TeilnehmerService;
import com.example.application.services.VeranstaltungenService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIcon;
import java.util.*;

/**
 * Diese Klasse repräsentiert einen Dialog zum Importieren von Teilnehmern.
 * Sie erbt von der Dialog-Klasse von Vaadin und bietet eine Benutzeroberfläche zum Hochladen einer Excel-Datei mit Teilnehmerdaten und zum Importieren dieser Daten.
 * Der Dialog enthält einen Upload-Button zum Hochladen der Excel-Datei, einen Import-Button zum Importieren der hochgeladenen Daten und einen Schließen-Button zum Schließen des Dialogs.
 * Bei erfolgreichem Hochladen einer Datei werden die Teilnehmerdaten aus der Datei gelesen und in die entsprechenden Listen eingefügt.
 * Wenn neue Teilnehmer gefunden werden, die noch nicht in der Datenbank vorhanden sind, wird ein Dialog mit einer Liste dieser Teilnehmer angezeigt.
 * Die Klasse verwendet einen ExcelImporter zum Lesen der Daten aus der Excel-Datei, einen TeilnehmerService zum Speichern der importierten Teilnehmer und einen AuthenticatedUser zur Authentifizierung.
 * Sie enthält auch eine Referenz auf eine VeranstaltungsterminView und einen TeilnehmerHinzufuegenDialog, die aktualisiert werden, wenn Teilnehmer importiert werden.
 *
 * @author Tobias
 */
@RolesAllowed({"ADMIN", "USER"})
public class TeilnehmerImportDialog extends Dialog {


    Button importButton = new Button("Importieren");
    Button closeButton = new Button("Schließen");

    MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
    ExcelImporter excelImporter;
    Set<Teilnehmer> newTeilnehmerListe = new HashSet<>();
    Set<Teilnehmer> teilnehmerVeranstaltungsListe = new HashSet<>();
    Set<Teilnehmer> neueStudierende = new HashSet<>();


    /**
     * Konstruktor für den Dialog zum Importieren von Teilnehmern.
     * Initialisiert den Dialog und konfiguriert die Interaktionen der Benutzeroberfläche.
     * Der Dialog enthält einen Upload-Button zum Hochladen einer Excel-Datei mit Teilnehmerdaten und Buttons zum Importieren und Schließen.
     * Der Import-Button speichert die importierten Teilnehmer und fügt sie zur Veranstaltung hinzu.
     * Der Schließen-Button schließt den Dialog.
     * Bei erfolgreichem Hochladen einer Datei werden die Teilnehmerdaten aus der Datei gelesen und in die entsprechenden Listen eingefügt.
     * Wenn neue Teilnehmer gefunden werden, die noch nicht in der Datenbank vorhanden sind, wird ein Dialog mit einer Liste dieser Teilnehmer angezeigt.
     *
     * @param teilnehmerService der Service zur Verwaltung von Teilnehmern
     * @param authenticatedUser der authentifizierte Benutzer
     * @param veranstaltungService der Service zur Verwaltung von Veranstaltungen
     * @param veranstaltungId die ID der Veranstaltung, zu der Teilnehmer hinzugefügt werden sollen
     * @param veranstaltungsterminView die Ansicht des Veranstaltungstermins
     * @param teilnehmerHinzufuegenDialog der Dialog zum Hinzufügen von Teilnehmern
     * @author Tobias
     */
    public TeilnehmerImportDialog(TeilnehmerService teilnehmerService, AuthenticatedUser authenticatedUser, VeranstaltungenService veranstaltungService, Long veranstaltungId, VeranstaltungsterminView veranstaltungsterminView, TeilnehmerHinzufuegenDialog teilnehmerHinzufuegenDialog){
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

            Optional<User> maybeUser = authenticatedUser.get();
            if (maybeUser.isPresent()) {
                User user = maybeUser.get();
                Veranstaltung veranstaltung = veranstaltungService.findVeranstaltungById(veranstaltungId, user);
                veranstaltung.addAllTeilnehmer(teilnehmerVeranstaltungsListe);
                veranstaltungService.saveVeranstaltung(veranstaltung);
            }
            veranstaltungsterminView.update();
            teilnehmerHinzufuegenDialog.updateGrid();
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

                newTeilnehmerListe.addAll(excelImporter.readAllTeilnehmerFromExcel(buffer.getInputStream(event.getFileName())));
                teilnehmerVeranstaltungsListe.addAll(excelImporter.readAllTeilnehmerFromExcel(buffer.getInputStream(event.getFileName())));
                neueStudierende.addAll(excelImporter.readNewTeilnehmerFromExcel(buffer.getInputStream(event.getFileName())));


                Optional<User> maybeUser = authenticatedUser.get();
                if (maybeUser.isPresent()) {
                    Dialog dialog = new Dialog();
                    dialog.setHeight(getHeight());
                    dialog.setHeaderTitle(neueStudierende.size() + " Teilnehmer gefunden, der neu angelegt werden muss");
                    dialog.getFooter().add(new Button("OK", e -> dialog.close()));

                    if (!neueStudierende.isEmpty()) {
                        Grid<Teilnehmer> grid = new Grid<>();
                        grid.setItems(neueStudierende);
                        grid.addColumn(Teilnehmer::getId).setHeader("MatrikelNr").setSortable(true).setAutoWidth(true);
                        grid.addColumn(Teilnehmer::getVorname).setHeader("Vorname").setSortable(true).setAutoWidth(true);
                        grid.addColumn(Teilnehmer::getNachname).setHeader("Nachname").setSortable(true).setAutoWidth(true);

                        dialog.setWidth(grid.getWidth());
                        dialog.add(grid);
                        dialog.open();
                    }
                }
            } catch (Exception e) {
                Notification.show("Error reading Excel file: " + e.getMessage());
                System.out.println(e.getMessage());
            }
        });
    }

}

//Author: Leon
package com.example.application.views.auswertung;

import com.example.application.models.*;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.*;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.RolesAllowed;
import com.vaadin.flow.router.Route;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

//LEON
@Route(value = "auswertung/:veranstaltungId", layout = MainLayout.class)
@PageTitle("Auswertungen")
@RolesAllowed({"ADMIN", "USER"})
public class AuswertungView extends VerticalLayout implements BeforeEnterObserver{
    // Grid-Field für die Anzeige der Auswertungen
    Grid<Auswertung> grid01 = new Grid<>(Auswertung.class);
    // Services für die verschiedenen Entitäten und Logiken
    VeranstaltungenService veranstaltungenService;
    TeilnehmerService teilnehmerService;
    GruppenarbeitService gruppenarbeitService;
    GruppenarbeitTeilnehmerService gruppenarbeitTeilnehmerService;
    SuperService superService;
    UserService userService;

    // Authentifizierter Benutzer und zugehörige Felder
    AuthenticatedUser authenticatedUser;
    Optional<User> maybeUser;
    User user;
    Long veranstaltungsID;
    AuswertungExcelExporter auswertungExcelExporter;
    Veranstaltung veranstaltung;
    List<Auswertung> auswertungen;

    /**
     * Konstruktor für die AuswertungView Klasse.
     * Initialisiert die Services und die benötigten Felder.
     * Ruft die Methode validateUser() auf, um den authentifizierten Benutzer zu validieren.
     *
     * @param veranstaltungenService der Service für die Veranstaltungen
     * @param teilnehmerService der Service für die Teilnehmer
     * @param gruppenarbeitService der Service für die Gruppenarbeiten
     * @param gruppenarbeitTeilnehmerService der Service für die Gruppenarbeitsteilnehmer
     * @param userService der Service für die Benutzer
     * @param authenticatedUser der aktuell authentifizierte Benutzer
     * @param superService der Service für übergeordnete Funktionen
     * @param studierendeExcelExporter der Exporter für die Auswertungen in Excel-Format
     *
     * @author Leon
     */
    public AuswertungView(
            VeranstaltungenService veranstaltungenService,
            TeilnehmerService teilnehmerService,
            GruppenarbeitService gruppenarbeitService,
            GruppenarbeitTeilnehmerService gruppenarbeitTeilnehmerService,
            UserService userService,
            AuthenticatedUser authenticatedUser,
            SuperService superService,
            AuswertungExcelExporter studierendeExcelExporter) {
        this.veranstaltungenService = veranstaltungenService;
        this.teilnehmerService = teilnehmerService;
        this.gruppenarbeitService = gruppenarbeitService;
        this.gruppenarbeitTeilnehmerService = gruppenarbeitTeilnehmerService;
        this.userService = userService;
        this.authenticatedUser = authenticatedUser;
        this.superService = superService;
        this.auswertungExcelExporter = studierendeExcelExporter;

        addClassName("auswertung-view");
        maybeUser = authenticatedUser.get();
        user = validateUser(maybeUser);
        setSizeFull();
    }

    /**
     * Validiert den authentifizierten Benutzer.
     * Wenn der Benutzer vorhanden ist, wird er zurückgegeben.
     * Andernfalls wird ein neuer Benutzer erstellt und zurückgegeben.
     *
     * @param maybeUser der optionale authentifizierte Benutzer
     * @return der validierte Benutzer
     *
     * @author Leon
     */
    private User validateUser(Optional<User> maybeUser) {
        return maybeUser.orElseGet(User::new);
    }

    /**
     * Erstellt den Hauptinhalt der AuswertungView.
     * Der Inhalt besteht aus einem VerticalLayout, das das Grid enthält.
     *
     * @return der erstellte Hauptinhalt als Component
     *
     * @author Leon
     */
    private Component getContent() {
        VerticalLayout content = new VerticalLayout(grid01);
        content.setFlexGrow(2, grid01);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    /**
     * Konfiguriert das Grid für die Anzeige der Auswertungen.
     * Entfernt alle Spalten und fügt neue Spalten basierend auf den Gruppenarbeiten hinzu.
     * Setzt allgemeine Eigenschaften des Grids wie Breite und automatische Anpassung der Spalten.
     *
     * @author Leon
     */
    private void configureGrid() {
        grid01.removeAllColumns();
        // Eine einzelne Auswertung nehmen und alle Gruppenarbeiten als Columns darstellen
        Auswertung auswertung = auswertungen.getFirst();
        grid01.addColumn(Auswertung::getNameMatrikelnummer).setHeader("");
        grid01.addColumn(Auswertung::getGesamtPunkte).setHeader("Gesamtpunkte");
        for (TGGPHelper tggpHelper : auswertung.getTggpHelper()) {
            grid01.addColumn(Auswertung::getTggHelperValues).setHeader(tggpHelper.getTerminAndGruppenarbeit());
        }
        grid01.addColumn(Auswertung::getGesamtGruppenarbeiten).setHeader("Teilgenommene Gruppenarbeiten");

        grid01.addClassNames("contact-grid");
        grid01.setWidthFull();
        grid01.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    /**
     * Erstellt und konfiguriert die Toolbar für die AuswertungView.
     * Die Toolbar enthält eine Überschrift, einen Download-Button und einen unsichtbaren Anchor für den Dateidownload.
     * Der Download-Button löst ein Click-Event aus, um den Anchor zu triggern und die Auswertung als Excel-Datei herunterzuladen.
     *
     * @return die erstellte Toolbar als HorizontalLayout
     *
     * @author Leon
     */
    private HorizontalLayout getToolbar() {
        // Timestamp für das Laden der aktuellen Daten
        String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new java.util.Date());
        // Überschrift Placeholder
        H1 username = new H1("Auswertung für eine Veranstaltung");
        username.getStyle().set("font-size", "28px");
        // Falls der User aktiv ist, soll der Titel der aktuellen Veranstaltung hinzugefügt werden
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            username.setText("Auswertung für die Veranstaltung " +
                    veranstaltungenService.findVeranstaltungById(veranstaltungsID,user).getTitel());
        } else {
            // ansonsten soll zum Login zurücknavigiert werden, um Sicherheitsrisiken zu minimieren
            getUI().ifPresent(ui -> ui.navigate("login"));
        }
        // Ein Anchor, unter welchem der Download der Daten möglich ist
        Anchor anchor = new Anchor();
        anchor.setText("Download");
        anchor.getElement().getStyle().set("display", "none");
        anchor.getElement().setAttribute("download", true);

        // Dieser Button wird gedrückt und führt ein Click-Event aus, um den Anchor zu triggern
        Button button = new Button("Download");
        button.addClickListener(event -> {
            StreamResource resource = new StreamResource("auswertung_"+timeStamp+".xlsx", () -> {
                byte[] data; // Your method to fetch data
                try {
                    data = auswertungExcelExporter.export(superService.findAllAuswertungenByVeranstaltung(veranstaltungsID));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return new ByteArrayInputStream(data);
            });
            anchor.setHref(resource);
            anchor.getElement().callJsFunction("click");
        });
        add(anchor, button);

        var toolbar = new HorizontalLayout(username,anchor,button);
        toolbar.setAlignItems(Alignment.CENTER);
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);
        toolbar.setWidthFull();
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    /**
     * Aktualisiert die Liste der Auswertungen im Grid.
     * Ruft die Auswertungen für die aktuelle Veranstaltung aus dem SuperService ab und setzt sie im Grid.
     *
     * @author Leon
     */
    private void updateList() {
        grid01.setItems(superService.findAllAuswertungenByVeranstaltung(veranstaltungsID));
    }

    /**
     * Methode, die vor dem Laden der Seite ausgeführt wird.
     * Liest die Veranstaltungs-ID aus den Route-Parametern und ruft die entsprechenden Daten ab.
     * Initialisiert die Toolbar, den Inhalt und konfiguriert das Grid.
     *
     * @param event das BeforeEnterEvent
     *
     * @author Leon
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Der Route-Parameter enthält die Veranstaltungs-ID, welche genutzt wird, um die aktuelle Veranstaltung zu suchen
        final Optional<String> optionalId = event.getRouteParameters().get("veranstaltungId");
        optionalId.ifPresentOrElse(id -> veranstaltungsID = Long.parseLong(id),
                () -> {
                    // Da es mindestens eine Veranstaltung gibt, soll hier der Minimum-Wert 1 genommen werden.
                    veranstaltungsID = 1L;
                });
        veranstaltung = veranstaltungenService.findVeranstaltungById(veranstaltungsID,user);
        auswertungen = superService.findAllAuswertungenByVeranstaltung(veranstaltungsID);
        add(getToolbar(), getContent());
        configureGrid();
        updateList();
    }
}

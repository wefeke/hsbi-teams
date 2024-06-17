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
import java.util.Optional;
//LEON
@Route(value = "auswertung/:veranstaltungId", layout = MainLayout.class)
@PageTitle("Auswertungen")
@RolesAllowed({"ADMIN", "USER"})
public class AuswertungView extends VerticalLayout implements BeforeEnterObserver{
    // Grid-Field
    Grid<Auswertung> grid01 = new Grid<>(Auswertung.class);
    //Services
    VeranstaltungenService veranstaltungenService;
    TeilnehmerService teilnehmerService;
    GruppenarbeitService gruppenarbeitService;
    GruppenarbeitTeilnehmerService gruppenarbeitTeilnehmerService;
    SuperService superService;
    UserService userService;
    // Fields
    AuthenticatedUser authenticatedUser;
    Optional<User> maybeUser;
    User user;
    Long veranstaltungsID;
    AuswertungExcelExporter auswertungExcelExporter;

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
        configureGrid();
    }

    private User validateUser( Optional<User> maybeUser) {
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            return user;
        } else {
            return new User();
        }
    }

    private Component getContent() {
        VerticalLayout content = new VerticalLayout(grid01);
        content.setFlexGrow(2, grid01);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
      //
    }

    private void configureGrid() {
        grid01.addClassNames("contact-grid");
        grid01.setSizeFull();
        grid01.getColumns().forEach(col -> col.setAutoWidth(true));
    }

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

        // Die eigentlichen Daten werden in diesem Objekt gespeichert und dem Anchor übergeben
        StreamResource resource = new StreamResource("auswertung_"+veranstaltungenService.findVeranstaltungById(veranstaltungsID,user).getTitel()+"_"+timeStamp+".xlsx", () -> {
            byte[] data = null; // Your method to fetch data
            try {
                data = auswertungExcelExporter.export(superService.findAllAuswertungenByVeranstaltung(veranstaltungsID));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return new ByteArrayInputStream(data);
        });
        anchor.setHref(resource);

        // Dieser Button wird gedrückt und führt ein Click-Event aus, um den Anchor zu triggern
        Button button = new Button("Download");
        button.addClickListener(event -> {
            anchor.getElement().callJsFunction("click");
        });
        add(anchor, button);
        var toolbar = new HorizontalLayout(username,anchor,button);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    // Unter der Nutzung der Methode updateList werden die Felder des Grids mit dem Daten aus dem SuperService gefüllt
    private void updateList() {
      grid01.setItems(superService.findAllAuswertungenByVeranstaltung(veranstaltungsID));
    }

    // Bevor die Seite geladen wird, sollen Felder und wichtige Methoden ausgeführt werden
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Der Route-Parameter enthält die Veranstaltungs-ID, welche genutzt wird, um die aktuelle Veranstaltung zu suchen
        final Optional<String> optionalId = event.getRouteParameters().get("veranstaltungId");
        optionalId.ifPresentOrElse(id -> {
                    veranstaltungsID = Long.parseLong(id);
                },
                () -> {
            // Da es mindestens eine Veranstaltung gibt, soll hier der Minimum-Wert 1 genommen werden.
            veranstaltungsID = 1L;
                });
        updateList(); // updateList muss hiernach aufgerufen werden, sonst ist veranstaltunsgId null
        add(getToolbar(), getContent());
    }
}


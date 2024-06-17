//Author: Joris
package com.example.application.views.veranstaltungen;

import com.example.application.models.User;
import com.example.application.models.Veranstaltung;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.*;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@PageTitle("Veranstaltungen")
@Route(value = "", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "USER"})
public class VeranstaltungenView extends VerticalLayout  {

    //Services
    private final VeranstaltungenService veranstaltungenService;
    private final TeilnehmerService teilnehmerService;
    private final UserService userService;
    private final VeranstaltungsterminService veranstaltungsterminService;
    private final GruppenarbeitService gruppenarbeitService;
    private final GruppeService gruppeService;
    private final AuthenticatedUser authenticatedUser;

    private final Div kachelContainer = new Div();

    //Dialog Instances
    private VeranstaltungLoeschenDialog veranstaltungLoeschenDialog;

    /**
     * Konstruktor für die VeranstaltungenView Klasse.
     * Initialisiert die Services und erstellt das Layout für die VeranstaltungenView.
     * Die Methode holt den angemeldeten Benutzer und begrüßt ihn.
     * Wenn kein Benutzer angemeldet ist, wird der Benutzer zur Login-Seite weitergeleitet und eine IllegalStateException wird geworfen.
     * Die Methode erstellt auch das Layout für die Veranstaltungen, einschließlich der Such- und Filterleiste.
     * Schließlich wird das Dialog zum Löschen von Veranstaltungen erstellt und der KachelContainer wird aktualisiert.
     *
     * @param veranstaltungenService der Service für die Veranstaltungen
     * @param userService der Service für die Benutzer
     * @param teilnehmerService der Service für die Teilnehmer
     * @param authenticatedUser der aktuell authentifizierte Benutzer
     * @param veranstaltungsterminService der Service für die Veranstaltungstermine
     * @param gruppenarbeitService der Service für die Gruppenarbeiten
     * @param gruppeService der Service für die Gruppen
     *
     * @autor Joris
     */
    @Autowired
    public VeranstaltungenView(VeranstaltungenService veranstaltungenService, UserService userService, TeilnehmerService teilnehmerService, AuthenticatedUser authenticatedUser, VeranstaltungsterminService veranstaltungsterminService, GruppenarbeitService gruppenarbeitService, GruppeService gruppeService) {
        this.veranstaltungenService = veranstaltungenService;
        this.teilnehmerService = teilnehmerService;
        this.userService = userService;
        this.authenticatedUser = authenticatedUser;
        this.veranstaltungsterminService = veranstaltungsterminService;
        this.gruppenarbeitService = gruppenarbeitService;
        this.gruppeService = gruppeService;

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();

        H1 username = new H1("Herzlich Willkommen!");
        username.getStyle().set("font-size", "28px");

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            username.setText("Herzlich Willkommen, " + user.getName() + "!");
        } else {
            getUI().ifPresent(ui -> ui.navigate("login"));
            throw new IllegalStateException("User is not authenticated");
        }

        HorizontalLayout lineWithText = createLineWithText();
        HorizontalLayout searchAndFilterBar = createSearchAndFilterBar();

        kachelContainer.addClassName("veranstaltungen-container");
        kachelContainer.getStyle().set("display", "flex");
        kachelContainer.getStyle().set("flexWrap", "wrap");
        kachelContainer.setSizeFull();

        kachelContainer.add(new Div());

        createVeranstaltungLoeschenDialog();

        updateKachelContainer("");
        mainLayout.add(username, lineWithText, searchAndFilterBar, kachelContainer);
        add(mainLayout);
    }

    private HorizontalLayout createSearchAndFilterBar() {
        // Textfeld für die Suche erstellen
        TextField searchField = new TextField();
        searchField.setPlaceholder("Suche...");
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> {
            String searchText = e.getValue().toLowerCase();
            updateKachelContainer(searchText);
        });

        VeranstaltungDialog createDialog = new VeranstaltungDialog(veranstaltungenService, teilnehmerService, userService, this, authenticatedUser);

        // Button zum Erstellen neuer Veranstaltungen erstellen
        Button newEventButton = new Button(new Icon(VaadinIcon.PLUS));
        newEventButton.addClickListener(e -> createDialog.open());

        Div spacer = new Div();
        spacer.getStyle().set("flex-grow", "1");

        return createHorizontalLayout(searchField, newEventButton, spacer);
    }

    /**
     * Erstellt die Such- und Filterleiste für die VeranstaltungenView.
     * Die Methode erstellt ein Textfeld für die Suche und einen Button zum Erstellen neuer Veranstaltungen.
     * Wenn der Benutzer Text in das Suchfeld eingibt, wird der KachelContainer mit dem eingegebenen Text aktualisiert.
     * Wenn der Benutzer auf den Button zum Erstellen neuer Veranstaltungen klickt, wird ein Dialog zum Erstellen neuer Veranstaltungen geöffnet.
     * Ein Spacer wird hinzugefügt, um Platz zwischen den Komponenten zu schaffen.
     * Schließlich wird ein HorizontalLayout mit dem Suchfeld, dem Button und dem Spacer erstellt und zurückgegeben.
     *
     * @return ein HorizontalLayout, das die Such- und Filterleiste darstellt
     *
     * @autor Joris
     */
    private HorizontalLayout createHorizontalLayout(TextField searchField, Button newEventButton, Div spacer) {
        Select<String> sortSelect = new Select<>();
        sortSelect.setItems("Datum aufsteigend", "Datum absteigend", "A-Z", "Z-A");
        sortSelect.setValue("Datum aufsteigend");
        sortSelect.addValueChangeListener(e -> {
            String selectedFilter = e.getValue();
            updateKachelContainerWithFilter(selectedFilter);
        });

        // Layout erstellen und Komponenten hinzufügen
        HorizontalLayout layout = new HorizontalLayout(searchField, newEventButton, spacer, sortSelect);
        layout.setAlignItems(Alignment.CENTER);
        layout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        layout.setWidthFull();
        return layout;
    }

    /**
     * Erstellt ein HorizontalLayout, das einen Text zwischen zwei horizontalen Linien darstellt.
     * Die Methode erstellt zunächst einen Text und zwei horizontale Linien (Hr).
     * Die Linien haben unterschiedliche Flex-Grow-Eigenschaften, um den Text zentriert zwischen ihnen zu positionieren.
     * Die Methode erstellt dann ein HorizontalLayout und fügt die Linien und den Text hinzu.
     * Das Layout wird so konfiguriert, dass es die volle Breite einnimmt und die Elemente zentriert ausrichtet.
     *
     * @return ein HorizontalLayout, das einen Text zwischen zwei horizontalen Linien darstellt
     *
     * @autor Joris
     */
    private HorizontalLayout createLineWithText() {
        Text text = new Text("Veranstaltungen");

        Hr lineBefore = new Hr();
        lineBefore.getStyle().set("flex-grow", "0");
        lineBefore.getStyle().set("flex-shrink", "0");
        lineBefore.getStyle().set("width", "30px");
        lineBefore.getStyle().set("margin-top", "15px");
        lineBefore.getStyle().set("margin-right", "-8px");

        Hr lineAfter = new Hr();
        lineAfter.getStyle().set("flex-grow", "1");
        lineAfter.getStyle().set("flex-shrink", "0");
        lineAfter.getStyle().set("margin-top", "15px");
        lineAfter.getStyle().set("margin-left", "-8px");

        HorizontalLayout lineWithText = new HorizontalLayout(lineBefore, text, lineAfter);
        lineWithText.setWidth("100%");
        lineWithText.setAlignItems(Alignment.CENTER);

        return lineWithText;
    }

    /**
     * Aktualisiert den KachelContainer basierend auf einem gegebenen Filter.
     * Die Methode entfernt zunächst alle Elemente aus dem KachelContainer.
     * Dann holt sie den angemeldeten Benutzer und alle Veranstaltungen dieses Benutzers aus der Datenbank.
     * Die Veranstaltungen werden dann basierend auf dem ausgewählten Filter sortiert.
     * Wenn kein Filter ausgewählt ist oder der Filter leer ist, werden die Veranstaltungen nach dem Semester sortiert.
     * Ansonsten werden die Veranstaltungen basierend auf dem ausgewählten Filter sortiert.
     * Schließlich werden die sortierten Veranstaltungen zum KachelContainer hinzugefügt.
     *
     * @param filter der ausgewählte Filter zum Sortieren der Veranstaltungen
     *
     * @autor Joris
     */
    public void updateKachelContainerWithFilter(String filter) {
        kachelContainer.removeAll();
        // Alle Veranstaltungen des angemeldeten Benutzers aus der Datenbank abrufen
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            List<Veranstaltung> veranstaltungen = veranstaltungenService.findAllVeranstaltungenByUser(user);
            // Sortieren der Veranstaltungen basierend auf dem ausgewählten Filter
            if (filter == null || filter.isEmpty()) {
                veranstaltungen.sort(Comparator.comparing(Veranstaltung::getSemester));
            } else {
                switch (filter) {
                    case "Datum aufsteigend":
                        veranstaltungen.sort(Comparator.comparing(Veranstaltung::getSemester));
                        break;
                    case "Datum absteigend":
                        veranstaltungen.sort(Comparator.comparing(Veranstaltung::getSemester).reversed());
                        break;
                    case "A-Z":
                        veranstaltungen.sort(Comparator.comparing(Veranstaltung::getTitel));
                        break;
                    case "Z-A":
                        veranstaltungen.sort(Comparator.comparing(Veranstaltung::getTitel).reversed());
                        break;
                }
            }
            // Hinzufügen der sortierten Veranstaltungen zum KachelContainer
            for (Veranstaltung veranstaltung : veranstaltungen) {
                HorizontalLayout veranstaltungKachel = createVeranstaltungKachel(veranstaltung);
                kachelContainer.add(veranstaltungKachel);
            }
        }
    }

    /**
     * Aktualisiert den KachelContainer basierend auf einem gegebenen Suchtext.
     * Die Methode entfernt zunächst alle Elemente aus dem KachelContainer.
     * Dann holt sie den angemeldeten Benutzer und alle Veranstaltungen dieses Benutzers aus der Datenbank.
     * Wenn die Liste der Veranstaltungen leer ist, wird das Standardlayout erstellt.
     * Ansonsten werden die Veranstaltungen basierend auf dem Suchtext gefiltert und nach dem Semester sortiert.
     * Wenn die gefilterte Liste der Veranstaltungen leer ist, wird ein Text angezeigt, der anzeigt, dass keine Veranstaltungen gefunden wurden.
     * Ansonsten werden die gefilterten Veranstaltungen zum KachelContainer hinzugefügt.
     *
     * @param searchText der Suchtext zum Filtern der Veranstaltungen
     *
     * @autor Joris
     */
    public void updateKachelContainer(String searchText) {
        kachelContainer.removeAll();

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            List<Veranstaltung> veranstaltungen = veranstaltungenService.findAllVeranstaltungenByUser(user);

            if (veranstaltungen.isEmpty()) {
                createDefaultLayout();
            } else {
                List<Veranstaltung> filteredVeranstaltungen = veranstaltungen.stream()
                        .filter(veranstaltung -> veranstaltung.getTitel().toLowerCase().contains(searchText))
                        .sorted(Comparator.comparing(Veranstaltung::getSemester))
                        .toList();

                if (filteredVeranstaltungen.isEmpty()) {
                    Text noEventsText = new Text("Keine Veranstaltungen für \"" + searchText + "\" gefunden.");
                    kachelContainer.add(noEventsText);
                } else {
                    for (Veranstaltung veranstaltung : filteredVeranstaltungen) {
                        HorizontalLayout veranstaltungKachel = createVeranstaltungKachel(veranstaltung);
                        kachelContainer.add(veranstaltungKachel);
                    }
                }
            }
        }
    }

    /**
     * Erstellt das Standardlayout für den KachelContainer.
     * Die Methode erstellt zunächst einen Dialog zum Erstellen neuer Veranstaltungen und einen Text, der anzeigt, dass keine Veranstaltungen vorhanden sind.
     * Dann wird ein Button zum Erstellen neuer Veranstaltungen erstellt. Wenn der Benutzer auf diesen Button klickt, wird der Dialog zum Erstellen neuer Veranstaltungen geöffnet.
     * Die Methode erstellt dann ein VerticalLayout und fügt den Text und den Button hinzu. Das Layout wird so konfiguriert, dass es die volle Größe einnimmt und die Elemente zentriert ausrichtet.
     * Schließlich wird das Layout zum KachelContainer hinzugefügt.
     *
     * @autor Joris
     */
    private void createDefaultLayout() {
        VeranstaltungDialog createDialog = new VeranstaltungDialog(veranstaltungenService, teilnehmerService, userService, this, authenticatedUser);

        Text noEventsText = new Text("Noch keine Veranstaltungen vorhanden, bitte legen Sie hier eine an:");
        Button createEventButton = new Button("Veranstaltung anlegen");
        createEventButton.addClickListener(e -> createDialog.open());

        // Layout erstellen und Komponenten hinzufügen
        VerticalLayout layout = new VerticalLayout(noEventsText, createEventButton);
        layout.setAlignItems(Alignment.CENTER);
        layout.setJustifyContentMode(JustifyContentMode.CENTER);
        layout.setSizeFull();

        // Layout zum KachelContainer hinzufügen
        kachelContainer.add(layout);
    }

    /**
     * Erstellt eine Kachel für eine bestimmte Veranstaltung.
     * Die Methode erstellt zunächst ein Div für das Veranstaltungsdatum und ein Div für die Veranstaltungsinformationen.
     * Dann werden ein Löschen-Button und ein Bearbeiten-Button erstellt.
     * Wenn der Benutzer auf den Löschen-Button klickt, wird der Dialog zum Löschen der Veranstaltung geöffnet.
     * Wenn der Benutzer auf den Bearbeiten-Button klickt, wird der Dialog zum Bearbeiten der Veranstaltung geöffnet.
     * Die Methode erstellt dann ein HorizontalLayout und fügt das Veranstaltungsdatum, die Veranstaltungsinformationen, einen Spacer und die beiden Buttons hinzu.
     * Das Layout wird so konfiguriert, dass es die volle Breite einnimmt und die Elemente zentriert ausrichtet.
     * Wenn der Benutzer auf die Kachel klickt, wird er zur Detailansicht der Veranstaltung weitergeleitet.
     *
     * @param veranstaltung die Veranstaltung, für die die Kachel erstellt wird
     * @return ein HorizontalLayout, das die Kachel für die Veranstaltung darstellt
     *
     * @autor Joris
     */
    private HorizontalLayout createVeranstaltungKachel(Veranstaltung veranstaltung) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        Div veranstaltungsDatum = new Div();
        veranstaltungsDatum.setText(veranstaltung.getSemester().format(dateFormatter));
        veranstaltungsDatum.getStyle().set("font-size", "18px");

        Div veranstaltungInfo = new Div();
        veranstaltungInfo.setText(veranstaltung.getTitel());
        veranstaltungInfo.setId("veranstaltung-info");
        veranstaltungInfo.getStyle().set("text-align", "left");
        veranstaltungInfo.getStyle().set("font-size", "18px");

        Div spacer = new Div();
        spacer.getStyle().set("flex-grow", "1");

        Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteButton.getStyle().set("cursor", "pointer");
        deleteButton.getElement().addEventListener("click", e ->
            veranstaltungLoeschenDialog.open()
        ).addEventData("event.stopPropagation()");

        VeranstaltungBearbeiten editDialog = new VeranstaltungBearbeiten(veranstaltungenService, teilnehmerService, userService, veranstaltung, this, authenticatedUser);

        veranstaltungLoeschenDialog.setVeranstaltung(veranstaltung);

        Button editButton = new Button(new Icon(VaadinIcon.EDIT));
        ((Icon)editButton.getIcon()).setColor("#2B64D6");
        editButton.getStyle().set("cursor", "pointer");
        editButton.getElement().addEventListener("click", e-> {
            editDialog.open();
            editDialog.readBean();
        }).addEventData("event.stopPropagation()");

        HorizontalLayout row = new HorizontalLayout(veranstaltungsDatum, veranstaltungInfo, spacer, editButton, deleteButton);
        row.addClassName("veranstaltung-row");
        row.setAlignItems(Alignment.CENTER);
        row.setJustifyContentMode(JustifyContentMode.BETWEEN);
        row.setWidthFull();

        row.addClickListener(e -> {
            String veranstaltungID = veranstaltung.getId().toString();
            getUI().ifPresent(ui -> ui.navigate("veranstaltung-detail/" + veranstaltungID));
        });

        return row;
    }

    /**
     * Erstellt den Dialog zum Löschen von Veranstaltungen.
     * Die Methode initialisiert die `veranstaltungLoeschenDialog` Instanz mit einem neuen `VeranstaltungLoeschenDialog`.
     * Der `VeranstaltungLoeschenDialog` benötigt mehrere Services, die für seine Funktionen benötigt werden.
     * Diese Services werden als Parameter an den Konstruktor des `VeranstaltungLoeschenDialog` übergeben.
     *
     * @autor Lilli
     */
    private void createVeranstaltungLoeschenDialog() {
        veranstaltungLoeschenDialog = new VeranstaltungLoeschenDialog(veranstaltungsterminService, gruppenarbeitService, gruppeService, veranstaltungenService, teilnehmerService);
    }
}

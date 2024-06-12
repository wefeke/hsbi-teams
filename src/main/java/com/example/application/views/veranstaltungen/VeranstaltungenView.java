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
        }

        HorizontalLayout lineWithText = createLineWithText();
        HorizontalLayout searchAndFilterBar = createSearchAndFilterBar();

        kachelContainer.addClassName("veranstaltungen-container");
        kachelContainer.getStyle().set("display", "flex");
        kachelContainer.getStyle().set("flexWrap", "wrap");

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

    public void updateKachelContainer(String searchText) {
        kachelContainer.removeAll();
        // Alle Veranstaltungen des angemeldeten Benutzers aus der Datenbank abrufen
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            List<Veranstaltung> veranstaltungen = veranstaltungenService.findAllVeranstaltungenByUser(user);
            // Überprüfen, ob die Liste der Veranstaltungen leer ist
            if (veranstaltungen.isEmpty()) {
                // Text und Button erstellen
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
            } else {
                // Filtern der Veranstaltungen basierend auf dem Suchtext
                List<Veranstaltung> filteredVeranstaltungen = veranstaltungen.stream()
                        .filter(veranstaltung -> veranstaltung.getTitel().toLowerCase().contains(searchText))
                        .sorted(Comparator.comparing(Veranstaltung::getSemester))
                        .toList();
                // Überprüfen, ob die gefilterte Liste der Veranstaltungen leer ist
                if (filteredVeranstaltungen.isEmpty()) {
                    // Text erstellen
                    Text noEventsText = new Text("Keine Veranstaltungen für \"" + searchText + "\" gefunden.");
                    // Text zum KachelContainer hinzufügen
                    kachelContainer.add(noEventsText);
                } else {
                    // Hinzufügen der gefilterten Veranstaltungen zum KachelContainer
                    for (Veranstaltung veranstaltung : filteredVeranstaltungen) {
                        HorizontalLayout veranstaltungKachel = createVeranstaltungKachel(veranstaltung);
                        kachelContainer.add(veranstaltungKachel);
                    }
                }
            }
        }
    }

    private HorizontalLayout createVeranstaltungKachel(Veranstaltung veranstaltung) {
        Div veranstaltungInfo = new Div();
        veranstaltungInfo.setText(veranstaltung.getTitel());
        veranstaltungInfo.getStyle().set("text-align", "left");
        veranstaltungInfo.getStyle().set("font-size", "18px");

        Div spacer = new Div();
        spacer.getStyle().set("flex-grow", "1");

        Div veranstaltungsDatum = new Div();
        veranstaltungsDatum.setText(veranstaltung.getSemester().toString());
        veranstaltungsDatum.getStyle().set("left", "5px");
        veranstaltungsDatum.getStyle().set("margin-top", "-18px");

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

        HorizontalLayout row = new HorizontalLayout(veranstaltungInfo, spacer, veranstaltungsDatum, editButton, deleteButton);
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

    //Lilli
    private void createVeranstaltungLoeschenDialog() {
        veranstaltungLoeschenDialog = new VeranstaltungLoeschenDialog(veranstaltungsterminService, gruppenarbeitService, gruppeService, veranstaltungenService, teilnehmerService);
    }

}




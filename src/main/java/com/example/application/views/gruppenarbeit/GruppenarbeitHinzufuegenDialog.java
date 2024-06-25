package com.example.application.views.gruppenarbeit;

import com.example.application.models.*;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.*;
import com.example.application.views.MainLayout;
import com.example.application.views.veranstaltungstermin.VeranstaltungsterminView;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.List;

/**
 * Diese Klasse implementiert einen Dialog zum Hinzufügen von Gruppenarbeiten.
 * Sie ermöglicht die Erstellung neuer Gruppenarbeiten für eine Veranstaltung, inklusive Auswahl der Teilnehmer,
 * Bildung von Gruppen und Speicherung der erstellten Gruppenarbeit.
 * <p>
 * Der Dialog bietet Funktionen zum Zufallsauslosen von Teilnehmern zu Gruppen, Anzeige und Neumischen der erstellten
 * Gruppen sowie zur Speicherung der Gruppenarbeit für einen bestimmten Veranstaltungstermin.
 *
 * @author Lilli
 */
@SuppressWarnings({"SpringTransactionalMethodCallsInspection", "SpringJavaInjectionPointsAutowiringInspection"})
@PageTitle("Gruppenarbeiten")
@Route(value = "gruppenarbeiten", layout = MainLayout.class)
public class GruppenarbeitHinzufuegenDialog extends Dialog {

    //Services
    private final TeilnehmerService teilnehmerService;

    //Data
    private final List<Teilnehmer> allParticipants = new ArrayList<>();
    private Set<Teilnehmer> selectedParticipants;
    private final String veranstaltungId;
    private final Gruppenarbeit gruppenarbeit = new Gruppenarbeit();
    private final List<Gruppe> gruppen = new ArrayList<>();

    //Binder
    private final Binder<Gruppenarbeit> binderGruppenarbeit = new Binder<>(Gruppenarbeit.class);

    //UI Elements
    private final TextField titleField = new TextField("Titel");
    private final TextArea descriptionArea = new TextArea("Beschreibung");
    private final MultiSelectListBox<Teilnehmer> participants = new MultiSelectListBox<>();
    private final Select<String> groupSize = new Select<>();
    private final Grid<Gruppe> groupsGrid = new Grid<>(Gruppe.class, false);
    private final Div groupsArea = new Div();
    private final Button saveBtn = new Button("Gruppenarbeit speichern");
    private final Button randomizeBtn = new Button("Neu mischen");
    private final Button cancelBtn = new Button("Abbrechen");
    private final Button clearGroupsBtn = new Button("Nur Gruppen");
    private final Button clearAllFieldsBtn = new Button("Formular");
    private final Button deselectAllParticipantsBtn = new Button("Entfernen");
    private final Button selectAllParticipantsBtn = new Button("Auswählen");
    private final H4 groupsTitle = new H4("Gruppen");

    /**
     * Konstruktor für den {@code GruppenarbeitHinzufuegenDialog}.
     * <p>
     * Erzeugt eine neue Instanz des Dialogs zum Hinzufügen einer Gruppenarbeit für eine bestimmte Veranstaltung.
     * Initialisiert die benötigten UI-Elemente und bindet die erforderlichen Felder.
     *
     * @param authenticatedUser Der authentifizierte Benutzer, der die Aktion ausführt.
     * @param veranstaltungId Die ID der Veranstaltung, zu der die Gruppenarbeit hinzugefügt wird.
     * @param gruppenarbeitService Der Service für die Verwaltung von Gruppenarbeiten.
     * @param teilnehmerService Der Service für die Verwaltung von Teilnehmern.
     * @param veranstaltungsterminService Der Service für die Verwaltung von Veranstaltungsterminen.
     * @param gruppeService Der Service für die Verwaltung von Gruppen.
     * @param veranstaltungsterminView Die Ansichtsklasse für die Darstellung eines Veranstaltungstermins.
     * @param veranstaltungenService Der Service für die Verwaltung von Veranstaltungen.
     * @param veranstaltungstermin Der spezifische Veranstaltungstermin, zu dem die Gruppenarbeit hinzugefügt wird.
     *
     * @author Lilli
     */
    @Autowired
    public GruppenarbeitHinzufuegenDialog(AuthenticatedUser authenticatedUser, String veranstaltungId, GruppenarbeitService gruppenarbeitService, TeilnehmerService teilnehmerService, VeranstaltungsterminService veranstaltungsterminService, GruppeService gruppeService, VeranstaltungsterminView veranstaltungsterminView, VeranstaltungenService veranstaltungenService, Veranstaltungstermin veranstaltungstermin) {
        this.veranstaltungId = veranstaltungId;
        this.teilnehmerService = teilnehmerService;
        configureDialog(authenticatedUser, veranstaltungId, gruppenarbeitService, veranstaltungsterminService, gruppeService, veranstaltungsterminView, veranstaltungenService, veranstaltungstermin);

        add(createLayout());
    }

    /**
     * Konfiguriert den Dialog zum Hinzufügen einer Gruppenarbeit.
     * <p>
     * Diese Methode initialisiert die notwendigen UI-Elemente, überprüft die Berechtigungen des authentifizierten
     * Benutzers für die angegebene Veranstaltung und bindet die erforderlichen Felder.
     *
     * @param authenticatedUser Der authentifizierte Benutzer, der die Aktion ausführt.
     * @param veranstaltungId Die ID der Veranstaltung, zu der die Gruppenarbeit hinzugefügt wird.
     * @param gruppenarbeitService Der Service für die Verwaltung von Gruppenarbeiten.
     * @param veranstaltungsterminService Der Service für die Verwaltung von Veranstaltungsterminen.
     * @param gruppeService Der Service für die Verwaltung von Gruppen.
     * @param veranstaltungsterminView Die Ansichtsklasse für die Darstellung eines Veranstaltungstermins.
     * @param veranstaltungenService Der Service für die Verwaltung von Veranstaltungen.
     * @param veranstaltungstermin Der spezifische Veranstaltungstermin, zu dem die Gruppenarbeit hinzugefügt wird.
     *
     * @author Lilli
     */
    private void configureDialog(AuthenticatedUser authenticatedUser, String veranstaltungId, GruppenarbeitService gruppenarbeitService, VeranstaltungsterminService veranstaltungsterminService, GruppeService gruppeService, VeranstaltungsterminView veranstaltungsterminView, VeranstaltungenService veranstaltungenService, Veranstaltungstermin veranstaltungstermin) {
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            if (veranstaltungenService.findVeranstaltungById(Long.parseLong(veranstaltungId), user) != null) {
                listBoxParticipants();
            }
        }

        configureGroupsArea();
        groupSizeSelect();
        bindFields();
        addBtnFunctionalities(gruppenarbeitService, veranstaltungsterminService, gruppeService, veranstaltungsterminView, veranstaltungstermin, maybeUser);

        groupSize.addValueChangeListener(event -> randomize(gruppen));
    }

    /**
     * Fügt den Buttons in der Benutzeroberfläche Funktionalitäten hinzu.
     * <p>
     * Diese Methode konfiguriert die Klickereignisse für folgende Buttons:
     * - "Neu mischen" ordnet die Teilnehmergruppen zufällig neu.
     * - "Gruppenarbeit speichern" speichert die aktuelle Gruppenarbeit und die zugeordneten Gruppen.
     * - "Abbrechen" setzt das Formular zurück und schließt den Dialog.
     * - "Nur Gruppen" löscht die angezeigten Gruppen und die ausgewählte Gruppengröße.
     * - "Formular" leert alle Eingabefelder im Formular.
     * - "Entfernen" hebt die Auswahl aller Teilnehmer in der MultiSelect-Liste auf.
     * - "Auswählen" wählt alle verfügbaren Teilnehmer in der MultiSelect-Liste aus.
     *
     * @param gruppenarbeitService Der Service für die Verwaltung von Gruppenarbeiten.
     * @param veranstaltungsterminService Der Service für die Verwaltung von Veranstaltungsterminen.
     * @param gruppeService Der Service für die Verwaltung von Gruppen.
     * @param veranstaltungsterminView Die Ansichtsklasse für die Darstellung eines Veranstaltungstermins.
     * @param veranstaltungstermin Der spezifische Veranstaltungstermin, zu dem die Gruppenarbeit hinzugefügt wird.
     * @param maybeUser Der optionale authentifizierte Benutzer, der die Aktion ausführt.
     *
     * @author Lilli
     */
    private void addBtnFunctionalities(GruppenarbeitService gruppenarbeitService, VeranstaltungsterminService veranstaltungsterminService, GruppeService gruppeService, VeranstaltungsterminView veranstaltungsterminView, Veranstaltungstermin veranstaltungstermin, Optional<User> maybeUser) {
        randomizeBtn.addClickListener(event -> randomizeBtnFunctionality());

        saveBtn.addClickListener(event -> saveBtnFunctionality(gruppenarbeitService, veranstaltungsterminService, gruppeService, veranstaltungsterminView, veranstaltungstermin, maybeUser));

        cancelBtn.addClickListener(event -> {
            clearFields();
            clearGroupsArea();
            groupSize.clear();
            groupsTitle.setVisible(false);
            close();
        });

        clearGroupsBtn.addClickListener(event -> {
            clearGroupsArea();
            groupSize.clear();
            groupsTitle.setVisible(false);
        });

        clearAllFieldsBtn.addClickListener(event -> {
            clearFields();
            groupSize.clear();
            clearGroupsArea();
            groupsTitle.setVisible(false);
        });

        deselectAllParticipantsBtn.addClickListener(event -> participants.deselectAll());

        selectAllParticipantsBtn.addClickListener(event -> {
            for(Teilnehmer p:allParticipants){
                participants.select(p);
            }
        });

    }

    /**
     * Behandelt das Klickereignis des Buttons "Neu mischen".
     * <p>
     * Überprüft zunächst, ob Gruppen vorhanden sind. Wenn keine Gruppen vorhanden sind,
     * wird eine Benachrichtigung angezeigt, dass keine Neuordnung möglich ist. Andernfalls
     * wird die Methode {@link #randomize(List)} aufgerufen, um die Teilnehmergruppen zufällig
     * neu anzuordnen.
     *
     * @author Lilli
     */
    private void randomizeBtnFunctionality() {
        if(gruppen.isEmpty()){
            Notification.show("Es existieren keine Gruppen. Kann nicht neu mischen.");
        }
        else {
            randomize(gruppen);
        }
    }

    /**
     * Behandelt das Klickereignis des Buttons "Gruppenarbeit speichern".
     * <p>
     * Validiert zuerst die Eingaben im Binder {@link #binderGruppenarbeit}. Wenn die Validierung erfolgreich ist,
     * wird die Methode {@link #saveGruppenarbeitWithGruppen(GruppenarbeitService, VeranstaltungsterminService,
     * GruppeService, Veranstaltungstermin, Optional)} aufgerufen, um die Gruppenarbeit und
     * zugeordneten Gruppen zu speichern. Anschließend wird eine Erfolgsmeldung angezeigt und der Dialog geschlossen.
     * Wenn die Validierung fehlschlägt, wird eine Fehlermeldung angezeigt.
     *
     * @param gruppenarbeitService Der Service für die Verwaltung von Gruppenarbeiten.
     * @param veranstaltungsterminService Der Service für die Verwaltung von Veranstaltungsterminen.
     * @param gruppeService Der Service für die Verwaltung von Gruppen.
     * @param veranstaltungsterminView Die Ansichtsklasse für die Darstellung eines Veranstaltungstermins.
     * @param veranstaltungstermin Der spezifische Veranstaltungstermin, zu dem die Gruppenarbeit gehört.
     * @param maybeUser Der optionale authentifizierte Benutzer, der die Aktion ausführt.
     *
     * @author Lilli
     */
    private void saveBtnFunctionality(GruppenarbeitService gruppenarbeitService, VeranstaltungsterminService veranstaltungsterminService, GruppeService gruppeService, VeranstaltungsterminView veranstaltungsterminView, Veranstaltungstermin veranstaltungstermin, Optional<User> maybeUser) {
        if(binderGruppenarbeit.writeBeanIfValid(gruppenarbeit)){
            saveGruppenarbeitWithGruppen(gruppenarbeitService, veranstaltungsterminService, gruppeService, veranstaltungstermin, maybeUser);

            Notification.show("Gruppenarbeit angelegt!");
            close();
            clearFields();

            if (gruppenarbeit.getVeranstaltungstermin() != null) {
                veranstaltungsterminView.setAktiveKachelVeranstaltungstermin(gruppenarbeit.getVeranstaltungstermin());

                if (gruppenarbeit != null) {
                    veranstaltungsterminView.setAktiveKachelGruppenarbeit(gruppenarbeit);
                }
            }
            veranstaltungsterminView.update();

        }
        else {
            Notification.show("Fehler");
        }
    }

    /**
     * Speichert die Gruppenarbeit und die zugeordneten Gruppen in der Datenbank.
     * <p>
     * Setzt zunächst den Benutzer für die Gruppenarbeit, sofern ein {@code maybeUser} vorhanden ist.
     * Anschließend wird die Gruppenarbeit dem Veranstaltungstermin zugeordnet und in der Datenbank gespeichert über
     * den {@code gruppenarbeitService}.
     * <p>
     * Wenn Gruppen vorhanden sind, werden diese ebenfalls gespeichert und dem Benutzer zugewiesen.
     * Die ausgewählten Teilnehmer werden der Gruppenarbeit zugewiesen und ebenfalls gespeichert, sofern Gruppen
     * mitgespeichert werden.
     * Jeder Gruppe wird die entsprechende Gruppenarbeit zugewiesen.
     *
     * @param gruppenarbeitService Der Service für die Verwaltung von Gruppenarbeiten.
     * @param veranstaltungsterminService Der Service für die Verwaltung von Veranstaltungsterminen.
     * @param gruppeService Der Service für die Verwaltung von Gruppen.
     * @param veranstaltungstermin Der spezifische Veranstaltungstermin, zu dem die Gruppenarbeit gehört.
     * @param maybeUser Der optionale authentifizierte Benutzer, der die Aktion ausführt.
     * <p>
     * {@code @transactional} Diese Methode ist transaktional, um die atomare Ausführung aller Datenbankoperationen
     * sicherzustellen.
     *
     * @author Lilli
     */
    @Transactional
    protected void saveGruppenarbeitWithGruppen(GruppenarbeitService gruppenarbeitService, VeranstaltungsterminService veranstaltungsterminService, GruppeService gruppeService, Veranstaltungstermin veranstaltungstermin, Optional<User> maybeUser) {
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            gruppenarbeit.setUser(user);
        }

        gruppenarbeit.setVeranstaltungstermin(veranstaltungstermin);
        gruppenarbeitService.save(gruppenarbeit);

        //der Gruppenarbeit die Teilnehmer übergeben
        if(!(gruppen.isEmpty())) {
            for (Gruppe gruppe : gruppen) {
                if (maybeUser.isPresent()) {
                    User user = maybeUser.get();
                    gruppe.setUser(user);
                }
                gruppeService.save(gruppe);
            }

            selectedParticipants = participants.getSelectedItems();
            gruppenarbeit.setTeilnehmer(selectedParticipants.stream().toList());
            gruppenarbeitService.save(gruppenarbeit);

            //den Gruppen die Gruppenarbeit übergeben
            for (Gruppe gruppe : gruppen) {
                gruppe.setGruppenarbeit(gruppenarbeit);
                gruppeService.save(gruppe);
            }
        }

        //Gruppenarbeit zum Veranstaltungstermin hinzufügen
        veranstaltungstermin.addGruppenarbeit(gruppenarbeit);
        veranstaltungsterminService.saveVeranstaltungstermin(veranstaltungstermin);
    }

    /**
     * Teilt ausgewählte Teilnehmer zufällig auf Gruppen zu und zeigt diese Zufallseinteilung dann mithilfe von Grids
     * an.
     * <p>
     * Überprüft zunächst, ob eine gültige Anzahl von Gruppen ausgewählt wurde und ob Teilnehmer ausgewählt sind.
     * Wenn ja, werden die ausgewählten Teilnehmer zufällig in die Gruppen eingeteilt.
     * Anschließend werden für jede Gruppe Grids erstellt und angezeigt, die die zugewiesenen Teilnehmer anzeigen.
     *
     * @param gruppen Die Liste der Gruppen, in die die Teilnehmer zufällig aufgeteilt werden sollen.
     */
    private void randomize(List<Gruppe> gruppen) {
        if(groupSize.getOptionalValue().isEmpty()){
            groupsTitle.setVisible(false);
            clearGroupsArea();
            clearGroupsList(gruppen);
        }
        else if(Objects.equals(groupSize.getValue(), "Keine Teilnehmer ausgewählt.")){
            Notification.show("Kann keine Gruppen erstellen, da keine Teilnehmer ausgewählt sind.");
            clearGroupsArea();
            clearGroupsList(gruppen);
            groupsTitle.setVisible(false);
        }
        else{
            clearGroupsList(gruppen);
            clearGroupsArea();
            int numberOfGroups = getNumberOfGroups();
            int[] sizes = groupSizes(numberOfGroups, participants.getSelectedItems().size());
            makeGroups(numberOfGroups, gruppen);
            randomizeParticipants(sizes, numberOfGroups, gruppen);
            groupGrids(numberOfGroups, gruppen);
            groupsTitle.setVisible(true);
        }
    }

    /**
     * Erstellt Grids für jede Gruppe und zeigt die zugewiesenen Teilnehmer in jedem Grid an.
     * <p>
     * Jedes Grid zeigt die Teilnehmer einer Gruppe mit ihren Matrikelnummern, Vornamen und Nachnamen an.
     * Die Grids werden der UI hinzugefügt, um die Zufallseinteilung der Teilnehmer in Gruppen zu visualisieren.
     *
     * @param numberOfGroups Die Anzahl der Gruppen, für die Grids erstellt werden sollen.
     * @param gruppen Eine Liste von Gruppen, deren Teilnehmer in den Grids angezeigt werden sollen.
     *
     * @author Lilli
     */
    private void groupGrids(int numberOfGroups, List<Gruppe> gruppen) {
        for(int i = 0; i< numberOfGroups; i++){
            Grid<Teilnehmer> grid = new Grid<>(Teilnehmer.class, false);
            grid.addColumn(Teilnehmer::getId).setHeader("Matrikelnr");
            grid.addColumn(Teilnehmer::getVorname).setHeader("Vorname");
            grid.addColumn(Teilnehmer::getNachname).setHeader("Nachname");
            grid.setItems(gruppen.get(i).getTeilnehmer());
            grid.setWidth("400px");
            grid.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS);

            H5 title = new H5("Gruppe " + (i+1) + ": " + gruppen.get(i).getTeilnehmer().size() + " Teilnehmer");
            title.addClassName("gruppen-gruppenarbeit-title");
            Div titleAndGroups = new Div(title, grid);
            titleAndGroups.addClassName("gruppen-gruppenarbeit");
            groupsArea.add(titleAndGroups);
        }
    }

    /**
     * Konfiguriert den Bereich für Gruppen.
     * <p>
     * Diese Methode setzt die Breite der `groupsArea` auf 100% und weist ihm die CSS-Klasse
     * "gruppen-container-gruppenarbeiten" zu.
     *
     * @author Lilli
     */
    private void configureGroupsArea() {
        groupsArea.setWidth("100%");
        groupsArea.setClassName("gruppen-container-gruppenarbeiten");
    }

    /**
     * Mischung der Teilnehmer zufällig in die Gruppen basierend auf den angegebenen Gruppengrößen.
     *
     * @param sizes          Ein Array von Größen der Gruppen, das angibt, wie viele Teilnehmer in jeder Gruppe sein
     *                       sollen.
     * @param numberOfGroups Die Anzahl der Gruppen, die erstellt wurden.
     * @param gruppen        Eine Liste von Gruppen, in die die Teilnehmer zufällig aufgeteilt werden.
     *
     * @author Lilli
     */
    private void randomizeParticipants(int[] sizes, int numberOfGroups, List<Gruppe> gruppen) {
        selectedParticipants = participants.getSelectedItems();
        List<Teilnehmer> selectedParticipantsList = new ArrayList<>(selectedParticipants.stream().toList());
        Collections.shuffle(selectedParticipantsList);
        Iterator<Teilnehmer> teilnehmerIterator = selectedParticipantsList.iterator();

        for(int j = 0; j< sizes[sizes.length-1]; j++) {
            for (int i = 0; i < numberOfGroups; i++) {
                if(teilnehmerIterator.hasNext()){
                    gruppen.get(i).addTeilnehmer(teilnehmerIterator.next());
                }
                else{
                    break;
                }
            }
        }
    }

    /**
     * Fügt der Liste {@code gruppen} die angegebene Anzahl von Gruppen hinzu.
     *
     * @param numberOfGroups Die Anzahl der zu erstellenden Gruppen.
     * @param gruppen        Die Liste, zu der die neuen Gruppen hinzugefügt werden sollen.
     *
     * @author Lilli
     */
    private void makeGroups(int numberOfGroups, List<Gruppe> gruppen) {
        for(int i = 0; i< numberOfGroups; i++){
            gruppen.add(new Gruppe((long) i+1));
        }
    }

    /**
     * Gibt die Anzahl der Gruppen zurück, die aus der ausgewählten Gruppengröße extrahiert wird.
     *
     * @return Die Anzahl der Gruppen als Ganzzahl.
     *
     * @author Lilli
     */
    private int getNumberOfGroups() {
        String num = groupSize.getValue();
        String[] splitString = num.split(" ");
        return Integer.parseInt(splitString[0]);
    }

    /**
     * Leert die Liste von Gruppen.
     * <p>
     * Diese Methode löscht alle Elemente aus der übergebenen Liste von Gruppen.
     *
     * @param gruppen Die Liste von Gruppen, die geleert werden soll.
     *
     * @author Lilli
     */
    private static void clearGroupsList(List<Gruppe> gruppen) {
        if(!gruppen.isEmpty()) {
            gruppen.clear();
        }
    }

    /**
     * Leert den Bereich, in dem die Gruppen-Grids angezeigt werden.
     * <p>
     * Diese Methode entfernt alle bereits vorhandenen Grids und deren Inhalte
     * aus dem Bereich, in dem die Gruppen-Grids angezeigt werden.
     *
     * @author Lilli
     */
    private void clearGroupsArea() {
        if(!(groupsArea.getComponentCount()==0)){
            groupsArea.removeAll();
        }
    }

    /**
     * Konfiguriert die Select-Box für die Auswahl der Gruppengröße.
     * <p>
     * Diese Methode setzt die Optionen und das Label für die Select-Box,
     * basierend auf der Anzahl der ausgewählten Teilnehmer in der MultiSelectListBox.
     * Wenn keine Teilnehmer ausgewählt sind, wird eine entsprechende Meldung angezeigt.
     *
     * @author Lilli
     */
    private void groupSizeSelect() {
        List<String> groups = getGroups();
        groupSize.setLabel("Gruppen wählen");
        groupSize.setItems(groups);
    }

    /**
     * Leert die Eingabefelder für Titel und Beschreibung.
     * <p>
     * Diese Methode setzt den Textinhalt der Textfelder für Titel und Beschreibung auf leer.
     * Dadurch werden alle zuvor eingegebenen oder angezeigten Daten in diesen Feldern entfernt.
     *
     * @author Lilli
     */
    private void clearFields() {
        titleField.clear();
        descriptionArea.clear();
    }

    /**
     * Gibt eine Liste von Strings zurück, die die verfügbaren Gruppenoptionen darstellen.
     * <p>
     * Diese Methode erstellt eine Liste von Strings, die die verfügbaren Gruppenoptionen basierend
     * auf der Anzahl der ausgewählten Teilnehmer in der MultiSelectListBox darstellen. Wenn keine
     * Teilnehmer ausgewählt sind, wird eine entsprechende Meldung angezeigt.
     *
     * @return Eine Liste von Strings, die die verfügbaren Gruppenoptionen darstellen.
     *
     * @author Lilli
     */
    private List<String> getGroups() {
        List<String> groups = new ArrayList<>();
        if(participants.getSelectedItems().isEmpty()){
            groups.add("Keine Teilnehmer ausgewählt.");
            return groups;
        }
        groups = groupNumbersAndSizes(participants.getSelectedItems().size());
        return groups;
    }

    /**
     * Befüllt die MultiSelectListBox mit Teilnehmern und konfiguriert die Auswahllogik.
     * <p>
     * Diese Methode lädt alle Teilnehmer einer Veranstaltung in die MultiSelectListBox und
     * konfiguriert die Auswahllogik für die Teilnehmer. Zusätzlich wird die Anzeige der
     * verfügbaren Gruppengrößen aktualisiert, basierend auf den ausgewählten Teilnehmern.
     *
     * @author Lilli
     */
    private void listBoxParticipants() {
        allParticipants.addAll(teilnehmerService.findTeilnehmerByVeranstaltungId(Long.parseLong(veranstaltungId)));

        participants.setItems(allParticipants);
        for(Teilnehmer p:allParticipants){
            participants.select(p);
        }
        participants.addSelectionListener(event -> {
            List<String> groups = getGroups();
            groupSize.setItems(groups);
            groupsGrid.setVisible(false);
        });
        participants.setRenderer(new ComponentRenderer<>(GruppenarbeitHinzufuegenDialog::getRenderLayoutParticipants));
    }


    /**
     * Erstellt ein HorizontalLayout zur Darstellung eines Teilnehmers mit Avatar und Informationen.
     * <p>
     * Diese Methode erstellt ein HorizontalLayout, das einen Avatar des Teilnehmers sowie den
     * Vor- und Nachnamen des Teilnehmers darstellt.
     *
     * @param participant Der Teilnehmer, für den das Layout erstellt werden soll.
     * @return Ein HorizontalLayout zur Darstellung des Teilnehmers.
     *
     * @author Lilli
     */
    private static HorizontalLayout getRenderLayoutParticipants(Teilnehmer participant) {
        HorizontalLayout row = new HorizontalLayout();
        row.setAlignItems(FlexComponent.Alignment.CENTER);

        Avatar avatar = new Avatar();
        avatar.setName(participant.getFullName());
        avatar.setImage(null);
        avatar.addClassName("profilbild");

        Span name = new Span(participant.getFullName());
        Span matrikelnr = new Span(String.valueOf(participant.getId()));
        matrikelnr.getStyle()
                .set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "var(--lumo-font-size-s)");

        VerticalLayout column = new VerticalLayout(name, matrikelnr);
        column.setPadding(false);
        column.setSpacing(false);

        row.add(avatar, column);
        row.getStyle().set("line-height", "var(--lumo-line-height-m)");
        return row;
    }


    /**
     * Berechnet die maximale Anzahl von Gruppen basierend auf der Anzahl der Teilnehmer.
     *
     * @param participants Die Anzahl der Teilnehmer, für die die maximale Anzahl von Gruppen berechnet werden soll.
     * @return Die maximale Anzahl von Gruppen, die gebildet werden können.
     *
     * @author Lilli
     */
    private int groupMax(int participants){
        return participants/2;
    }

    /**
     * Diese Methode berechnet die Anzahlen der Gruppen basierend auf der Anzahl der Teilnehmer.
     * Das sind dann eine mit allen Teilnehmern, 2 bis zur maximalen Anzahl und dann noch eine Gruppengröße, um alle
     * Teilnehmer in eine eigene Gruppe zu packen.
     *
     * @param participants Die Anzahl der Teilnehmer, für die die Gruppen berechnet werden sollen.
     * @return Ein Array von ganzen Zahlen, das die Anzahl der Gruppen und die Anzahl der Teilnehmer enthält.
     *
     * @author Lilli
     */
    private int[] groupNumbers(int participants){
        int groupMax = groupMax(participants);
        int[] groupNumbers = new int[groupMax+1];
        //Gruppen von 1 bis zum Maximum
        for(int i=0; i<groupMax; i++){
            groupNumbers[i] = i+1;
        }
        //Gruppenanzahl in der Anzahl der Teilnehmer, s.d. man jeden Teilnehmer in eigene Gruppe packen kann
        groupNumbers[groupMax] = participants;
        return groupNumbers;
    }

    /**
     * Diese Methode berechnet die Größen der Gruppen basierend auf der Anzahl der Gruppen und der Teilnehmerzahl.
     *
     * @param groups      Die Anzahl der Gruppen, in die die Teilnehmer aufgeteilt werden sollen.
     * @param participants Die Gesamtanzahl der Teilnehmer, die auf die Gruppen aufgeteilt werden sollen.
     * @return Ein Array von ganzen Zahlen, das die Größen der Gruppen enthält.
     *
     * @author Lilli
     */
    private int[] groupSizes(int groups, int participants){
        if(participants%groups == 0){
            return new int[]{participants/groups};
        }
        else{
            return new int[]{participants/groups, participants/groups+1};
        }
    }

    /**
     * Erstellt eine Liste von Strings, die die Gruppenanzahlen und deren Größen basierend auf der Anzahl der
     * Teilnehmer enthält.
     *
     * @param participants Die Anzahl der Teilnehmer.
     * @return Eine Liste von Strings, die die Gruppennummern und deren Größen darstellen.
     *
     * @author Lilli
     */
    private List<String> groupNumbersAndSizes(int participants){
        List<String> groupStrings = new ArrayList<>();
        for(int i:groupNumbers(participants))
        {
            StringBuilder str = new StringBuilder();
            str.append(i);
            str.append(" x ");

            for(Iterator<Integer> it = Arrays.stream(groupSizes(i, participants)).iterator(); it.hasNext();){
                String nextSize = it.next().toString();
                if(it.hasNext()){
                    str.append(nextSize);
                    str.append(" und ");
                }
                else{
                    str.append(nextSize);
                    str.append(" Teilnehmer");
                }
            }
            groupStrings.add(str.toString());
        }
        return groupStrings;
    }

    /**
     * Erstellt das Layout für das Dialogfenster zur Hinzufügung einer Gruppenarbeit.
     * <p>
     * Diese Methode erstellt ein VerticalLayout, das alle UI-Elemente für das Dialogfenster
     * zur Hinzufügung einer Gruppenarbeit enthält. Dazu gehören Eingabefelder für den Titel
     * und die Beschreibung der Gruppenarbeit, eine MultiSelectListBox für die Teilnehmerauswahl,
     * Buttons für verschiedene Aktionen sowie ein Bereich zur Anzeige der Gruppeneinteilungen.
     *
     * @return Das VerticalLayout mit allen UI-Elementen für das Gruppenarbeit-Dialogfenster.
     *
     * @author Lilli
     */
    private VerticalLayout createLayout(){
        VerticalLayout mainPageLayout = new VerticalLayout();
        HorizontalLayout gruppenarbeitData = new HorizontalLayout();
        VerticalLayout gruppenarbeitText = new VerticalLayout();
        VerticalLayout buttonsLayout = new VerticalLayout();

        setHeaderTitle("Gruppenarbeit hinzufügen");

        titleField.setWidth("400px");
        gruppenarbeitText.add(titleField);
        descriptionArea.setHeight("240px");
        descriptionArea.setWidth("400px");
        gruppenarbeitText.add(descriptionArea);

        groupSize.setWidth("320px");
        randomizeBtn.setWidth("320px");
        clearAllFieldsBtn.setWidth("150px");
        clearGroupsBtn.setWidth("150px");
        deselectAllParticipantsBtn.setWidth("150px");
        selectAllParticipantsBtn.setWidth("150px");

        VerticalLayout clearBtnsWithText = new VerticalLayout();
        clearBtnsWithText.setAlignItems(FlexComponent.Alignment.CENTER);
        HorizontalLayout clearBtns = new HorizontalLayout(clearAllFieldsBtn, clearGroupsBtn);
        H5 clear = new H5("Leeren");
        clear.getStyle().set("color", "#0562dc");
        clearBtnsWithText.add(clear, clearBtns);
        clearBtnsWithText.getStyle().set("padding", "0px");

        VerticalLayout allParticipantsBtnsWithText = new VerticalLayout();
        allParticipantsBtnsWithText.setAlignItems(FlexComponent.Alignment.CENTER);
        HorizontalLayout allParticipantsBtns = new HorizontalLayout(selectAllParticipantsBtn, deselectAllParticipantsBtn);
        H5 allParticipantsText = new H5("Alle Teilnehmer");
        allParticipantsText.getStyle().set("color", "#0562dc");
        allParticipantsBtnsWithText.add(allParticipantsText, allParticipantsBtns);
        allParticipantsBtnsWithText.getStyle().set("padding", "0px");

        buttonsLayout.add(groupSize, randomizeBtn, clearBtnsWithText, allParticipantsBtnsWithText);
        buttonsLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        getFooter().add(cancelBtn);
        getFooter().add(saveBtn);
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        gruppenarbeitData.add(gruppenarbeitText);
        participants.setHeight("400px");
        gruppenarbeitData.add(participants);
        gruppenarbeitData.add(buttonsLayout);
        gruppenarbeitData.setWidthFull();

        mainPageLayout.add(gruppenarbeitData, groupsTitle, groupsArea);
        groupsTitle.setVisible(false);

        return mainPageLayout;
    }

    /**
     * Bindet die UI-Felder an die Eigenschaften der Gruppenarbeit.
     * <p>
     * Diese Methode konfiguriert den Binder, um die Eingabefelder für Titel und Beschreibung
     * der Gruppenarbeit an die entsprechenden Eigenschaften der Gruppenarbeit zu binden. Dabei
     * werden auch Validatoren für die Eingabefelder gesetzt, um sicherzustellen, dass die
     * eingegebenen Daten den Anforderungen entsprechen.
     *
     * @author Lilli
     */
    private void bindFields(){
        binderGruppenarbeit.forField(titleField)
                .asRequired("Titel muss gefüllt sein")
                .withValidator(titel -> titel.length() <= 255, "Der Titel darf maximal 255 Zeichen lang sein")
                .bind(Gruppenarbeit::getTitel, Gruppenarbeit::setTitel);
        titleField.setMaxLength(255);
        binderGruppenarbeit.forField(descriptionArea)
                .withValidator(beschreibung -> beschreibung.length() <= 255, "Die Beschreibung darf maximal 255 Zeichen lang sein")
                .bind(Gruppenarbeit::getBeschreibung, Gruppenarbeit::setBeschreibung);
        descriptionArea.setMaxLength(255);
    }

}

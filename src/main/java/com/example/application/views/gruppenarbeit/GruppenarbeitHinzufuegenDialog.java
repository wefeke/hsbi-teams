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
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.List;

//Lilli
@PageTitle("Gruppenarbeiten")
@Route(value = "gruppenarbeiten", layout = MainLayout.class)
public class GruppenarbeitHinzufuegenDialog extends Dialog {
    private final VeranstaltungenService veranstaltungenService;

    //Services
    private final GruppenarbeitService gruppenarbeitService;
    private final TeilnehmerService teilnehmerService;
    private final VeranstaltungsterminService veranstaltungsterminService;
    private final GruppeService gruppeService;

    //User
    private AuthenticatedUser authenticatedUser;

    private VeranstaltungsterminView veranstaltungsterminView;

    //Data
    private List<Teilnehmer> allParticipants = new ArrayList<>();
    private Set<Teilnehmer> selectedParticipants;
    private List<Teilnehmer> selectedParticipantsList;
    private Veranstaltungstermin veranstaltungstermin;
    private final String veranstaltungId;
    private Gruppenarbeit gruppenarbeit = new Gruppenarbeit();
    private List<Gruppe> gruppen = new ArrayList<Gruppe>();

    //Binder
    Binder<Gruppenarbeit> binderGruppenarbeit = new Binder<>(Gruppenarbeit.class);

    //Dialog Items
    TextField titleField = new TextField("Titel");
    TextArea descriptionArea = new TextArea("Beschreibung");
    MultiSelectListBox<Teilnehmer> participants = new MultiSelectListBox<>();
    Select<String> groupSize = new Select<>();
    Grid<Gruppe> groupsGrid = new Grid<>(Gruppe.class, false);
    //TextField gruppenGroesse = new TextField("Teilnehmeranzahl");
    Div groupsArea = new Div();
    Button saveBtn = new Button("Gruppenarbeit speichern");
    Button randomizeBtn = new Button("Neu mischen");
    Button cancelBtn = new Button("Abbrechen");
    Button clearGroupsBtn = new Button("Nur Gruppen leeren");
    Button clearAllFieldsBtn = new Button("Formular leeren");
    Button deselectAllParticipantsBtn = new Button("Alle Teilnehmer entfernen");
    Button selectAllParticipantsBtn = new Button("Alle Teilnehmer auswählen");
    H4 groupsTitle = new H4("Gruppen");

    //Konstruktor
    public GruppenarbeitHinzufuegenDialog(AuthenticatedUser authenticatedUser, String veranstaltungId, GruppenarbeitService gruppenarbeitService, TeilnehmerService teilnehmerService, VeranstaltungsterminService veranstaltungsterminService, GruppeService gruppeService, VeranstaltungsterminView veranstaltungsterminView, VeranstaltungenService veranstaltungenService, Veranstaltungstermin veranstaltungstermin) {
        this.veranstaltungId = veranstaltungId;
        this.veranstaltungstermin = veranstaltungstermin;

        this.gruppenarbeitService = gruppenarbeitService;
        this.teilnehmerService = teilnehmerService;
        this.veranstaltungsterminService = veranstaltungsterminService;
        this.gruppeService = gruppeService;
        this.authenticatedUser = authenticatedUser;
        this.veranstaltungsterminView = veranstaltungsterminView;

        //gruppenGroesse.setReadOnly(true);

        configureDialog(authenticatedUser, veranstaltungId, gruppenarbeitService, veranstaltungsterminService, gruppeService, veranstaltungsterminView, veranstaltungenService, veranstaltungstermin);

        //Finales Zeugs
        add(createLayout());
        this.veranstaltungenService = veranstaltungenService;
    }

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

        groupSize.addValueChangeListener(event -> {
            randomize(gruppen);
        });
    }

    private void addBtnFunctionalities(GruppenarbeitService gruppenarbeitService, VeranstaltungsterminService veranstaltungsterminService, GruppeService gruppeService, VeranstaltungsterminView veranstaltungsterminView, Veranstaltungstermin veranstaltungstermin, Optional<User> maybeUser) {
        randomizeBtn.addClickListener(event -> {
            randomizeBtnFunctionality();
        });

        saveBtn.addClickListener(event -> {
            saveBtnFunctionality(gruppenarbeitService, veranstaltungsterminService, gruppeService, veranstaltungsterminView, veranstaltungstermin, maybeUser);
        });

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

        deselectAllParticipantsBtn.addClickListener(event -> {
            participants.deselectAll();
        });

        selectAllParticipantsBtn.addClickListener(event -> {
            for(Teilnehmer p:allParticipants){
                participants.select(p);
            }
        });

    }

    private void randomizeBtnFunctionality() {
        if(gruppen.isEmpty()){
            Notification.show("Es existieren keine Gruppen. Kann nicht neu mischen.");
        }
        else {
            randomize(gruppen);
        }
    }

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

    //Teilt ausgewählte Teilnehmer zufällig auf Gruppen zu und zeigt diese Zufallseinteilung dann mithilfe von Grids an
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
            //changeLabelText(numberOfGroups, participants.getSelectedItems().size());
            //Notification.show(gruppenGroesse.getValue());
            randomizeParticipants(sizes, numberOfGroups, gruppen);
            groupGrids(numberOfGroups, gruppen);
            groupsTitle.setVisible(true);
        }
    }

    //Erstellt die Grids für die Gruppen
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

    //Konfiguriert den Bereich, in dem die Gruppen-Grids angezeigt werden
    private void configureGroupsArea() {
        groupsArea.setWidth("100%");
        groupsArea.setClassName("gruppen-container-gruppenarbeiten");
    }

    //Teilt Teilnehmer zufällig auf Gruppen zu
    private void randomizeParticipants(int[] sizes, int numberOfGroups, List<Gruppe> gruppen) {
        selectedParticipants = participants.getSelectedItems();
        selectedParticipantsList = new ArrayList<>(selectedParticipants.stream().toList());
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

    //Erstellt benötigte Anzahl an Gruppen
    //TODO: This was static but I changed it bc I didn't know why. See if there are any problems.
    private void makeGroups(int numberOfGroups, List<Gruppe> gruppen) {
        for(int i = 0; i< numberOfGroups; i++){
            gruppen.add(new Gruppe((long) i+1));
        }
    }

    //Holt benötigte Anzahl an Gruppen aus der ausgewählten Option des Drop-Down-Feldes
    private int getNumberOfGroups() {
        String num = groupSize.getValue();
        String[] splitString = num.split(" ");
        return Integer.parseInt(splitString[0]);
    }

    //Leert die Gruppenliste
    private static void clearGroupsList(List<Gruppe> gruppen) {
        if(!gruppen.isEmpty()) {
            gruppen.clear();
        }
    }

    //Leert die Anzeige der Gruppen
    private void clearGroupsArea() {
        if(!(groupsArea.getComponentCount()==0)){
            groupsArea.removeAll();
        }
    }

    private void groupSizeSelect() {
        List<String> groups = getGroups();
        groupSize.setLabel("Gruppen wählen");
        groupSize.setItems(groups);
    }

    private void clearFields() {
        titleField.clear();
        descriptionArea.clear();
    }

    //Für die Select-Box der Gruppengrößen
    private List<String> getGroups() {
        List<String> groups = new ArrayList<>();
        if(participants.getSelectedItems().isEmpty()){
            groups.add("Keine Teilnehmer ausgewählt.");
            return groups;
        }
        groups = groupNumbersAndSizes(participants.getSelectedItems().size());
        return groups;
    }

    //Für die Felder der Teilnehmer in der ListBox
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
        participants.setRenderer(new ComponentRenderer<>(participant ->{
            HorizontalLayout row = new HorizontalLayout();
            row.setAlignItems(FlexComponent.Alignment.CENTER);

            Avatar avatar = new Avatar();
            avatar.setName(participant.getFullName());
            avatar.setImage(null);

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
        }));
    }


    //Berechnet die maximale Anzahl an Gruppen bei gegebener Teilnehmergröße
    private int groupMax(int participants){
        return participants/2;
    }

    //Berechnet alle möglichen Gruppenanzahlen
    //Das sind dann eine mit allen Teilnehmern, 2 bis zur maximalen Anzahl und dann noch eine Gruppengröße, um alle
    //Teilnehmer in eine eigene Gruppe zu packen
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

    //Berechnet die Gruppengröße(n) bei gegebener Gruppen- und Gesamtteilnehmeranzahl
    private int[] groupSizes(int groups, int participants){
        if(participants%groups == 0){
            return new int[]{participants/groups};
        }
        else{
            return new int[]{participants/groups, participants/groups+1};
        }
    }

    //Gibt alle möglichen Gruppengrößen und zugehörige Teilnehmeranzahlen als Strings in einer Liste zurück
    private List<String> groupNumbersAndSizes(int participants){
        List<String> groupStrings = new ArrayList<String>();
        for(int i:groupNumbers(participants))
        {
            String str = "";
            str += i;
            str += " x ";

            for(Iterator<Integer> it = Arrays.stream(groupSizes(i, participants)).iterator(); it.hasNext();){
                String nextSize = it.next().toString();
                if(it.hasNext()){
                    str += nextSize;
                    str += " und ";
                }
                else{
                    str += nextSize;
                    str += " Teilnehmer";
                }
            }
            groupStrings.add(str);
        }
        return groupStrings;
    }

    //Layout des Fensters
    private VerticalLayout createLayout(){
        VerticalLayout mainPageLayout = new VerticalLayout();
        HorizontalLayout gruppenarbeitData = new HorizontalLayout();
        VerticalLayout gruppenarbeitText = new VerticalLayout();
        VerticalLayout buttonsLayout = new VerticalLayout();

        setHeaderTitle("Gruppenarbeit hinzufügen");

        titleField.setWidth("400px");
        gruppenarbeitText.add(titleField);
        descriptionArea.setHeight("270px");
        descriptionArea.setWidth("400px");
        gruppenarbeitText.add(descriptionArea);

        groupSize.setWidth("230px");
        buttonsLayout.add(groupSize, randomizeBtn, clearAllFieldsBtn, clearGroupsBtn, deselectAllParticipantsBtn, selectAllParticipantsBtn);
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

    //Felder binden
    private void bindFields(){
        binderGruppenarbeit.forField(titleField)
                .asRequired("Titel muss gefüllt sein")
                .bind(Gruppenarbeit::getTitel, Gruppenarbeit::setTitel);
        binderGruppenarbeit.forField(descriptionArea)
                .bind(Gruppenarbeit::getBeschreibung, Gruppenarbeit::setBeschreibung);
    }

//    public void setVeranstaltungstermin(Veranstaltungstermin veranstaltungstermin){
//        this.veranstaltungstermin = veranstaltungstermin;
//    }

//    private void changeLabelText(int groups, int participants) {
//        int[] sizes = groupSizes(groups, participants);
//        String str = "";
//        for(Iterator<Integer> it = Arrays.stream(sizes).iterator(); it.hasNext();){
//            String nextSize = it.next().toString();
//            if(it.hasNext()){
//                str += nextSize;
//                str += " ";
//            }
//            else{
//                str += " und ";
//                str += nextSize;
//                str += " Teilnehmer";
//            }
//        }
//        gruppenGroesse.setValue(str);
//        }

}

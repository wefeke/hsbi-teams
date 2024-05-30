package com.example.application.views.gruppenarbeit;

import com.example.application.models.*;
import com.example.application.services.GruppeService;
import com.example.application.services.GruppenarbeitService;
import com.example.application.services.TeilnehmerService;
import com.example.application.services.VeranstaltungsterminService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.List;

//Lilli
@PageTitle("Gruppenarbeiten")
@Route(value = "gruppenarbeiten", layout = MainLayout.class)
public class GruppenarbeitHinzufuegenDialog extends Dialog {

    //TestStuff
    Button saveBtn = new Button("Gruppenarbeit speichern");
    Button randomizeBtn = new Button("Neu mischen");

    //Services
    private final GruppenarbeitService gruppenarbeitService;
    private final TeilnehmerService teilnehmerService;
    private final VeranstaltungsterminService veranstaltungsterminService;
    private final GruppeService gruppeService;

    //Data
    List<Teilnehmer> allParticipants = new ArrayList<>();
    Set<Teilnehmer> selectedParticipants;
    List<Teilnehmer> selectedParticipantsList;
    Veranstaltungstermin veranstaltungstermin;
    Veranstaltung veranstaltung;

    //Binder
    Binder<Gruppenarbeit> binderGruppenarbeit = new Binder<>(Gruppenarbeit.class);

    //Dialog Items
    TextField titleField = new TextField("Titel");
    TextArea descriptionArea = new TextArea("Beschreibung");
    H2 infoText = new H2("Gruppenarbeit anlegen");
    MultiSelectListBox<Teilnehmer> participants = new MultiSelectListBox<>();
    Select<String> groupSize = new Select<>();
    Grid<Gruppe> groupsGrid = new Grid<>(Gruppe.class, false);
    TextField gruppenGroesse = new TextField("Teilnehmeranzahl");
    Div groupsArea = new Div();

    //Konstruktor
    @Autowired
    public GruppenarbeitHinzufuegenDialog(Veranstaltung veranstaltung, GruppenarbeitService gruppenarbeitService, TeilnehmerService teilnehmerService, VeranstaltungsterminService veranstaltungsterminService, GruppeService gruppeService) {
        this.veranstaltung = veranstaltung;
        this.gruppenarbeitService = gruppenarbeitService;
        this.teilnehmerService = teilnehmerService;
        this.veranstaltungsterminService = veranstaltungsterminService;
        this.gruppeService = gruppeService;
        this.veranstaltungstermin = null;

        gruppenGroesse.setReadOnly(true);

        if(this.veranstaltung!=null){
            listBoxParticipants();
        }

        groupsGridVisual();
        groupSizeSelect();
        bindFields();

        //TestStuff
        //TODO: funktionierendes vernünftig in die Klasse in Methoden etc. integrieren
        Gruppenarbeit gruppenarbeit = new Gruppenarbeit();
        List<Gruppe> gruppen = new ArrayList<Gruppe>();
        List<Grid<Teilnehmer>> gruppenGrids = new ArrayList<Grid<Teilnehmer>>();

        randomizeBtn.addClickListener(event -> {
            if(groupSize.getOptionalValue().isEmpty()){
                if(!(groupsArea.getComponentCount()==0)){
                    groupsArea.removeAll();
                }
            }
            else if(Objects.equals(groupSize.getValue(), "Error: Keine Teilnehmer ausgewählt. Kann keine Gruppen erstellen")){
                Notification.show("Fehler");
            }
            else{
                if(!gruppen.isEmpty()) {
                    gruppen.clear();
                }

                if(!(groupsArea.getComponentCount()==0)){
                    groupsArea.removeAll();
                }

                groupsGrid.setVisible(true);
                String num = groupSize.getValue();
                String[] splitString = num.split(" ");

                int numberOfGroups = Integer.parseInt(splitString[0]);

                int[] sizes = groupSizes(numberOfGroups, participants.getSelectedItems().size());

                for(int i=0;i<numberOfGroups;i++){
                    gruppen.add(new Gruppe((long) i+1));
                }



//              changeLabelText(numberOfGroups, participants.getSelectedItems().size());
                Notification.show(gruppenGroesse.getValue());

                selectedParticipants = participants.getSelectedItems();
                selectedParticipantsList = new ArrayList<>(selectedParticipants.stream().toList());

                Collections.shuffle(selectedParticipantsList);
                Iterator<Teilnehmer> teilnehmerIterator = selectedParticipantsList.iterator();

                for(int j = 0; j<sizes[sizes.length-1];j++) {
                    for (int i = 0; i < numberOfGroups; i++) {
                        if(teilnehmerIterator.hasNext()){
                            gruppen.get(i).addTeilnehmer(teilnehmerIterator.next());
                        }
                        else{
                            break;
                        }
                    }
                }

                groupsArea.setWidth("100%");
                groupsArea.setClassName("gruppen-container-gruppenarbeiten");

                groupsGrid.setItems(gruppen);
                for(int i=0;i<numberOfGroups;i++){
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
        });

        groupSize.addValueChangeListener(event -> {
            if(groupSize.getOptionalValue().isEmpty()){
                if(!(groupsArea.getComponentCount()==0)){
                    groupsArea.removeAll();
                }
            }
            else if(Objects.equals(groupSize.getValue(), "Error: Keine Teilnehmer ausgewählt. Kann keine Gruppen erstellen")){
                Notification.show("Fehler");
            }
            else{
                if(!gruppen.isEmpty()) {
                    gruppen.clear();
                }

                if(!(groupsArea.getComponentCount()==0)){
                    groupsArea.removeAll();
                }

                groupsGrid.setVisible(true);
                String num = groupSize.getValue();
                String[] splitString = num.split(" ");

                int numberOfGroups = Integer.parseInt(splitString[0]);

                int[] sizes = groupSizes(numberOfGroups, participants.getSelectedItems().size());

                for(int i=0;i<numberOfGroups;i++){
                    gruppen.add(new Gruppe((long) i+1));
                }



//              changeLabelText(numberOfGroups, participants.getSelectedItems().size());
                Notification.show(gruppenGroesse.getValue());

                selectedParticipants = participants.getSelectedItems();
                selectedParticipantsList = new ArrayList<>(selectedParticipants.stream().toList());

                Collections.shuffle(selectedParticipantsList);
                Iterator<Teilnehmer> teilnehmerIterator = selectedParticipantsList.iterator();

                for(int j = 0; j<sizes[sizes.length-1];j++) {
                    for (int i = 0; i < numberOfGroups; i++) {
                        if(teilnehmerIterator.hasNext()){
                            gruppen.get(i).addTeilnehmer(teilnehmerIterator.next());
                        }
                        else{
                            break;
                        }
                    }
                }

                groupsArea.setWidth("100%");
                groupsArea.setClassName("gruppen-container-gruppenarbeiten");

                groupsGrid.setItems(gruppen);
                for(int i=0;i<numberOfGroups;i++){
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
        });

        saveBtn.addClickListener(event -> {
            if(binderGruppenarbeit.writeBeanIfValid(gruppenarbeit)){

                gruppenarbeit.setVeranstaltungstermin(this.veranstaltungstermin);

                for(int i = 0; i<gruppen.size(); i++){
                    gruppeService.save(gruppen.get(i));
                }

                gruppenarbeit.setGruppe(gruppen);
                gruppenarbeit.setTeilnehmer(selectedParticipantsList);

                gruppenarbeitService.save(gruppenarbeit);

                //Gruppenarbeit zum Veranstaltungstermin hinzufügen
                veranstaltungstermin.addGruppenarbeit(gruppenarbeit);
                veranstaltungsterminService.saveVeranstaltungstermin(veranstaltungstermin);
                Notification.show("Gruppenarbeit angelegt!");
                close();
                clearFields();
                UI.getCurrent().getPage().reload();


            }
            else {
                Notification.show("Fehler");
            }
        });

        //Finales Zeugs
        add(createLayout());
    }

    private void groupsGridVisual() {
        groupsGrid.setVisible(false);
        groupsGrid.addColumn(Gruppe::getNummer).setHeader("Gruppennummer");
        groupsGrid.addColumn(Gruppe::getTeilnehmer).setHeader("Teilnehmer");
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
            groups.add("Error: Keine Teilnehmer ausgewählt. Kann keine Gruppen erstellen");
            return groups;
        }
        groups = groupNumbersAndSizes(participants.getSelectedItems().size());
        return groups;
    }

    //Für die Felder der Teilnehmer in der ListBox
    private void listBoxParticipants() {
        allParticipants.addAll(teilnehmerService.findTeilnehmerByVeranstaltungId(this.veranstaltung.getVeranstaltungsId()));


        participants.setItems(allParticipants);
        for(Teilnehmer p:allParticipants){
            participants.select(p);
        }
        participants.addSelectionListener(event -> {
            List<String> groups = getGroups();
            groupSize.setItems(groups);
            groupsGrid.setVisible(false);

        });
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

        titleField.setWidth("400px");
        gruppenarbeitText.add(titleField);
        descriptionArea.setHeight("270px");
        descriptionArea.setWidth("400px");
        gruppenarbeitText.add(descriptionArea);

        groupSize.setWidth("230px");
        buttonsLayout.add(groupSize, saveBtn, randomizeBtn);

        gruppenarbeitData.add(gruppenarbeitText);
        participants.setWidth("250px");
        participants.setHeight("400px");
        gruppenarbeitData.add(participants);
        gruppenarbeitData.add(buttonsLayout);

        mainPageLayout.add(infoText, gruppenarbeitData, new H3("Gruppen"), groupsArea);



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

    public void setVeranstaltungstermin(Veranstaltungstermin veranstaltungstermin){
        this.veranstaltungstermin = veranstaltungstermin;
    }

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

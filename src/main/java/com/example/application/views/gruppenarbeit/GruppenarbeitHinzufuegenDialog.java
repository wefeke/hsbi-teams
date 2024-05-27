package com.example.application.views.gruppenarbeit;

import com.example.application.models.Gruppe;
import com.example.application.models.Gruppenarbeit;
import com.example.application.models.Teilnehmer;
import com.example.application.services.GruppeService;
import com.example.application.services.GruppenarbeitService;
import com.example.application.services.TeilnehmerService;
import com.example.application.services.VeranstaltungsterminService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.menubar.MenuBar;
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

//Lilli
@PageTitle("Gruppenarbeiten")
@Route(value = "gruppenarbeiten", layout = MainLayout.class)
public class GruppenarbeitHinzufuegenDialog extends Dialog {

    private final GruppeService gruppeService;
    //TestStuff
    Button saveBtn = new Button("Gruppenarbeit speichern");

    //Services
    private final GruppenarbeitService gruppenarbeitService;
    private final TeilnehmerService teilnehmerService;
    private final VeranstaltungsterminService veranstaltungsterminService;

    //Data
    List<Teilnehmer> allParticipants = new ArrayList<>();
    Set<Teilnehmer> selectedParticipants;
    List<Teilnehmer> selectedParticipantsList;

    //Binder
    Binder<Gruppenarbeit> binderGruppenarbeit = new Binder<>(Gruppenarbeit.class);

    //Dialog Items
    TextField titleField = new TextField("Titel");
    TextArea descriptionArea = new TextArea("Beschreibung");
    H2 infoText = new H2("Gruppenarbeit anlegen");
    MultiSelectListBox<Teilnehmer> participants = new MultiSelectListBox<>();
    Select<String> groupSize = new Select<>();
    Grid<Gruppe> groupsGrid = new Grid<>(Gruppe.class, false);

    //Konstruktor
    @Autowired
    public GruppenarbeitHinzufuegenDialog(GruppenarbeitService gruppenarbeitService, TeilnehmerService teilnehmerService, VeranstaltungsterminService veranstaltungsterminService, GruppeService gruppeService) {
        this.gruppenarbeitService = gruppenarbeitService;
        this.teilnehmerService = teilnehmerService;
        this.veranstaltungsterminService = veranstaltungsterminService;

        participants();
        groupsGridVisual();
        groupSizeSelect();
        bindFields();

        //TestStuff
        //TODO: funktionierendes vernünftig in die Klasse in Methoden etc. integrieren
        Gruppenarbeit gruppenarbeit = new Gruppenarbeit();
        List<Gruppe> gruppen = new ArrayList<Gruppe>();

        groupSize.addValueChangeListener(event -> {
            if(groupSize.getOptionalValue().isEmpty()){
                Notification.show("Empty!");
            }
            else if(Objects.equals(groupSize.getValue(), "Error: Keine Teilnehmer ausgewählt. Kann keine Gruppen erstellen")){
                Notification.show("Fehler");
            }
            else{
                if(!gruppen.isEmpty()) {
                    gruppen.clear();
                }

                groupsGrid.setVisible(true);
                char num = groupSize.getValue().charAt(0);
                int numberOfGroups = Integer.parseInt(num+"");

                int[] sizes = groupSizes(numberOfGroups, participants.getSelectedItems().size());

                for(int i=0;i<numberOfGroups;i++){
                    gruppen.add(new Gruppe((long) i+1));
                }

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


                groupsGrid.setItems(gruppen);
            }
        });

        saveBtn.addClickListener(event -> {
            if(binderGruppenarbeit.writeBeanIfValid(gruppenarbeit)){
                //Testweise hier hardgecodeter Termin
                gruppenarbeit.setVeranstaltungstermin(veranstaltungsterminService.findVeranstaltungsterminById(1L));

                for(int i = 0; i<gruppen.size(); i++){
                    gruppeService.save(gruppen.get(i));
                }

                gruppenarbeit.setGruppe(gruppen);
                gruppenarbeit.setTeilnehmer(selectedParticipantsList);

                gruppenarbeitService.save(gruppenarbeit);
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
        add(createLayout(), saveBtn, groupsGrid);
        this.gruppeService = gruppeService;
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
    private void participants() {
        allParticipants.addAll(teilnehmerService.findAllTeilnehmer());

        participants.setItems(allParticipants);
        for(Teilnehmer p:allParticipants){
            participants.select(p);
        }
        participants.addSelectionListener(event -> {
            List<String> groups = getGroups();
            groupSize.setItems(groups);
            groupsGrid.setVisible(false);

        });
        participants.setWidth("30%");
    }


    //Berechnet die maximale Anzahl an Gruppen bei gegebener Teilnehmergröße
    private int groupMax(int participants){
        return participants/2;
    }

    //Berechnet alle möglichen Gruppenanzahlen bei gegebener maximaler Gruppenanzahl
    //Das sind dann alle ab 2 bis zur maximalen Anzahl
//    private int[] groupNumbers(int groupMax){
//        int[] groupNumbers = new int[groupMax];
//        for(int i=0; i<groupMax; i++){
//            groupNumbers[i] = i+1;
//        }
//        return groupNumbers;
//    }

    private int[] groupNumbers(int participants){
        int groupMax = groupMax(participants);
        int[] groupNumbers = new int[groupMax+1];
        for(int i=0; i<groupMax; i++){
            groupNumbers[i] = i+1;
        }
        groupNumbers[groupMax] = participants;
        return groupNumbers;
    }

    //Berechnet die Gruppengröße bei gegebener Gruppen- und Gesamtteilnehmeranzahl
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

        gruppenarbeitText.add(titleField);
        gruppenarbeitText.add(descriptionArea);

        gruppenarbeitData.add(gruppenarbeitText);
        gruppenarbeitData.add(participants);

        mainPageLayout.add(infoText);
        mainPageLayout.add(gruppenarbeitData);
        mainPageLayout.add(groupSize);

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





}

package com.example.application.views.gruppenarbeit;

import com.example.application.models.Teilnehmer;
import com.example.application.services.GruppenarbeitService;
import com.example.application.services.TeilnehmerService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

//Lilli
@PageTitle("Gruppenarbeiten")
@Route(value = "gruppenarbeiten", layout = MainLayout.class)
public class GruppenarbeitHinzufuegenDialog extends Dialog {

    //Services
    private final GruppenarbeitService gruppenarbeitService;
    private final TeilnehmerService teilnehmerService;

    //Data
    Set<Teilnehmer> allParticipants = new HashSet<>();

    //Dialog Items
    TextField titleField = new TextField("Titel");
    TextArea descriptionArea = new TextArea("Beschreibung");
    H2 infoText = new H2("Gruppenarbeit anlegen");
    MultiSelectListBox<String> participants = new MultiSelectListBox<>();
    Select<String> groupSize = new Select<>();

    //Konstruktor
    @Autowired
    public GruppenarbeitHinzufuegenDialog(GruppenarbeitService gruppenarbeitService, TeilnehmerService teilnehmerService) {
        this.gruppenarbeitService = gruppenarbeitService;
        this.teilnehmerService = teilnehmerService;

        participants();

        List<String> groups = getGroups();
        groupSize.setLabel("Gruppen wählen");
        groupSize.setItems(groups);

        //add(infoText, titleField, descriptionArea, participants, groupSize);
        add(createLayout());

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
        Set<String> stringParticipants = new HashSet<>();
        for(Teilnehmer t:allParticipants){
            stringParticipants.add(t.toString());
        }
        participants.setItems(stringParticipants);
        for(String s:stringParticipants){
            participants.select(s);
        }
        participants.addSelectionListener(event -> {
            List<String> groups = getGroups();
            groupSize.setItems(groups);

        });
        participants.setWidth("30%");
    }


    //Berechnet die maximale Anzahl an Gruppen bei gegebener Teilnehmergröße
    private int groupMax(int participants){
        return participants/2;
    }

    //Berechnet alle möglichen Gruppenanzahlen bei gegebener maximaler Gruppenanzahl
    //Das sind dann alle ab 2 bis zur maximalen Anzahl
    private int[] groupNumbers(int groupMax){
        int[] groupNumbers = new int[groupMax-1];
        for(int i=0; i<groupMax-1; i++){
            groupNumbers[i] = i+2;
        }
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
        int groupsTotal = groupMax(participants);
        for(int i:groupNumbers(groupsTotal))
        {
            String str = "";
            str += i;
            str += " x ";
            //for(int j:groupSizes(i, participants)){
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



}

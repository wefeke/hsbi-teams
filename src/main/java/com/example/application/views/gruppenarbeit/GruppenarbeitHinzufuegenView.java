package com.example.application.views.gruppenarbeit;

import com.example.application.services.GruppenarbeitService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@PageTitle("Gruppenarbeiten")
@Route(value = "gruppenarbeiten", layout = MainLayout.class)
public class GruppenarbeitHinzufuegenView extends VerticalLayout {
    private final GruppenarbeitService gruppenarbeitService;

    HorizontalLayout formData = new HorizontalLayout();
    HorizontalLayout buttons = new HorizontalLayout();
    VerticalLayout dataAndParticipants = new VerticalLayout();
    List<String> allParticipants = new ArrayList<String>();

    TextField titleField = new TextField("Titel");
    TextArea descriptionArea = new TextArea("Beschreibung");
    H2 infoText = new H2("Gruppenarbeit anlegen");
    MultiSelectListBox<String> participants = new MultiSelectListBox<>();
    Button save = new Button("Speichern");
    Button clear = new Button("Leeren");
    Select<String> groupSize = new Select<>();


    @Autowired
    public GruppenarbeitHinzufuegenView(GruppenarbeitService gruppenarbeitService) {
        addParticipants();
        this.gruppenarbeitService = gruppenarbeitService;
        participants();
        formData();
        buttons();
        selectGroupsize();
        add(infoText, formData, buttons, groupSize);

    }

    private void addParticipants(){
        allParticipants.add("Test 1");
        allParticipants.add("Test 2");
        allParticipants.add("Test 3");
        allParticipants.add("Test 4");
        allParticipants.add("Test 5");
        allParticipants.add("Test 6");
        allParticipants.add("Test 7");
        allParticipants.add("Test 8");
        allParticipants.add("Test 9");
        allParticipants.add("Test 10");
    }

    private void buttons() {
        save.addClickListener(e -> groupSize.setVisible(true));
        buttons.add(save,clear);
    }

    private void formData() {
        descriptionArea();
        titleField.setWidthFull();
        dataAndParticipants.add(titleField, descriptionArea);
        formData.setWidth("80%");
        formData.add(dataAndParticipants, participants);
    }

    private void selectGroupsize(){
        List<String> groups = groupNumbersAndSizes(allParticipants.size());
        groupSize.setLabel("Gruppen wählen");
        groupSize.setWidth("25%");
        groupSize.setItems(groups);
        groupSize.setVisible(false);
    }
    private void descriptionArea() {
        descriptionArea.setWidthFull();
        descriptionArea.setHeight("150px");
    }

    private void participants() {
        participants.setItems(allParticipants);
        participants.setWidth("30%");
    }

    private int groupMax(int participants){
        return participants/2;
    }

    private int[] groupNumbers(int groupMax){
        int[] groupNumbers = new int[groupMax-1];
        for(int i=0; i<groupMax-1; i++){
            groupNumbers[i] = i+2;
        }
        return groupNumbers;
    }

    private int[] groupSizes(int groups, int participants){
        if(participants%groups == 0){
            return new int[]{participants/groups};
        }
        else{
            return new int[]{participants/groups, participants/groups+1};
        }
    }

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



}

//Author: Joris
package com.example.application.views.veranstaltungen;

import com.example.application.models.Teilnehmer;
import com.example.application.models.Veranstaltung;
import com.example.application.services.TeilnehmerService;
import com.example.application.services.UserService;
import com.example.application.services.VeranstaltungenService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@PageTitle("Veranstaltungen")
@Route(value = "", layout = MainLayout.class)
public class VeranstaltungenView extends VerticalLayout {

    private final VeranstaltungenService veranstaltungenService;
    private final TeilnehmerService teilnehmerService;
    private final UserService userService;
    private Dialog addDialog;

    @Autowired
    public VeranstaltungenView(VeranstaltungenService veranstaltungenService, UserService userService, TeilnehmerService teilnehmerService) {
        this.veranstaltungenService = veranstaltungenService;
        this.teilnehmerService = teilnehmerService;

        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setSizeFull();

        Div kachelContainer = new Div();
        kachelContainer.addClassName("veranstaltungen-container");
        kachelContainer.getStyle().set("display", "flex");
        kachelContainer.getStyle().set("flexWrap", "wrap");

        // Alle Veranstaltungen aus der Datenbank abrufen
        List<Veranstaltung> veranstaltungen = veranstaltungenService.findAllVeranstaltungen();

        // Kacheln für vorhandene Veranstaltungen erstellen
        for (Veranstaltung veranstaltung : veranstaltungen) {
            kachelContainer.add(createVeranstaltungKachel(veranstaltung));
        }

        // Kachel für neue Veranstaltung hinzufügen
        kachelContainer.add(createKachel("add-veranstaltung"));

        mainLayout.add(kachelContainer);
        add(mainLayout);
        this.userService = userService;


        //addVeranstaltungDialog = new Dialog(new VeranstaltungenHinzufuegen(veranstaltungenService, userService, teilnehmerService));
        createAddDialog();
    }

    /**
     * Erstellt eine Kachel für eine spezifische Veranstaltung.
     *
     * @param veranstaltung Die Veranstaltung, für die eine Kachel erstellt werden soll.
     * @return Die erstellte Kachel als {@link Div}-Element, fertig zum Hinzufügen zum Container.
     */
    private Div createVeranstaltungKachel(Veranstaltung veranstaltung) {
        Div veranstaltungsInfo = new Div();
        veranstaltungsInfo.setText(veranstaltung.getTitel());
        veranstaltungsInfo.getStyle().set("text-align", "center");
        veranstaltungsInfo.getStyle().set("margin", "auto");

        Div kachelContent = new Div(veranstaltungsInfo);
        kachelContent.getStyle().set("display", "flex");
        kachelContent.getStyle().set("flex-direction", "column");

        Div kachel = new Div(kachelContent);
        kachel.getStyle()
                .set("position", "relative")
                .set("border", "2px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "10px")
                .set("padding", "1em")
                .set("margin", "0.5em")
                .set("cursor", "pointer")
                .set("box-shadow", "0 4px 8px 0 rgba(0,0,0,0.2)");
        kachel.setWidth("150px");
        kachel.setHeight("150px");

        Div deleteIcon = new Div();
        deleteIcon.setText("🗑️");
        deleteIcon.addClassName("delete-icon");
        deleteIcon.getStyle().set("position", "absolute");
        deleteIcon.getStyle().set("bottom", "5px");
        deleteIcon.getStyle().set("right", "5px");
        deleteIcon.getStyle().set("visibility", "hidden");

        Dialog confirmationDialog = new Dialog();
        confirmationDialog.add(new Text("Möchten Sie die Veranstaltung " + veranstaltung.getTitel() + " wirklich löschen?"));

        Button yesButton = new Button("Ja", event -> {
            veranstaltungenService.deleteVeranstaltung(veranstaltung);
            Notification.show("Veranstaltung gelöscht");
            getUI().ifPresent(ui -> ui.getPage().reload());
            confirmationDialog.close();
        });

        Button noButton = new Button("Nein", event -> {
            confirmationDialog.close();
            kachel.getStyle().set("background-color", "");
            deleteIcon.getStyle().set("visibility", "hidden");
        });

        confirmationDialog.add(yesButton, noButton);

        deleteIcon.getElement().addEventListener("click", e -> {
            confirmationDialog.open();
        }).addEventData("event.stopPropagation()");

        kachel.add(deleteIcon);

        kachel.getElement().addEventListener("mouseover", e -> {
            kachel.getStyle().set("background-color", "lightblue");
            deleteIcon.getStyle().set("visibility", "visible");
        });

        kachel.getElement().addEventListener("mouseout", e -> {
            kachel.getStyle().set("background-color", "");
            deleteIcon.getStyle().set("visibility", "hidden");
        });

        kachel.addClickListener(e -> {
            String veranstaltungID = veranstaltung.getVeranstaltungsId().toString();
            getUI().ifPresent(ui -> ui.navigate("veranstaltung-detail/" + veranstaltungID));
        });

        return kachel;
    }

    /**
     * Erstellt eine Kachel mit einem spezifischen Navigationsziel.
     *
     * Diese Kachel zeigt ein Pluszeichen und navigiert zur angegebenen Route, wenn sie angeklickt wird.
     * Die Kachel hat eine Hover-Effekt, der die Hintergrundfarbe der Kachel ändert, wenn der Mauszeiger darüber schwebt.
     *
     * @param navigationalTarget Die Route, zu der navigiert wird, wenn auf die Kachel geklickt wird.
     * @return Die erstellte Kachel als {@link Div}-Element, bereit zum Hinzufügen zum Container.
     */
    private Div createKachel(String navigationalTarget) {
        //Ich kann diese Methode nicht als static machen, weil getUi() nicht statisch ist.
        //Dadurch muss ich diese Methode in jeder Klasse neu einbauen, wo ich sie verwenden möchte.
        Div plusSymbol = new Div();
        plusSymbol.setText("+");
        plusSymbol.getStyle()
                .set("font-size", "40px")
                .set("text-align", "center")
                .set("margin", "auto");

        Div neueVeranstaltungKachel = new Div(plusSymbol);
        neueVeranstaltungKachel.getStyle()
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "10px")
                .set("padding", "1em")
                .set("margin", "0.5em")
                .set("cursor", "pointer")
                .set("box-shadow", "0 4px 8px 0 rgba(0,0,0,0.2)")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center");
        neueVeranstaltungKachel.setWidth("150px");
        neueVeranstaltungKachel.setHeight("150px");

        neueVeranstaltungKachel.getElement().addEventListener("mouseover", e -> {
            neueVeranstaltungKachel.getStyle().set("background-color", "lightblue");
        });

        neueVeranstaltungKachel.getElement().addEventListener("mouseout", e -> {
            neueVeranstaltungKachel.getStyle().set("background-color", "");
        });

        neueVeranstaltungKachel.addClickListener(e -> {
            // Create a Dialog

            // Add the VeranstaltungDetailView to the Dialog
            addDialog.open();
            //getUI().ifPresent(ui -> ui.navigate(navigationalTarget));
        });

        return neueVeranstaltungKachel;
    }

    public void createAddDialog() {

        addDialog = new Dialog();
            addDialog.setHeaderTitle("Veranstaltung hinzufügen");
            addDialog.setWidth("90vh");

        TextField titelField = new TextField("Titel");
        DatePicker datePicker = new DatePicker("Datum");

        MultiSelectComboBox<Teilnehmer> comboBox = new MultiSelectComboBox<>("Teilnehmer");
            comboBox.setItems(teilnehmerService.findAllTeilnehmer());
            comboBox.setItemLabelGenerator(Teilnehmer::getVorname);

        Grid<Teilnehmer> grid = new Grid<>(Teilnehmer.class, false);
            grid.setItems(teilnehmerService.findAllTeilnehmer());
            grid.addColumn(Teilnehmer::getVorname).setHeader("Vorname");
            //grid.addColumn(Teilnehmer::getNachname).setHeader("Nachname");
            grid.addColumn(Teilnehmer::getId).setHeader("ID");
            grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);

            /*
            grid.asSingleSelect().addValueChangeListener(event -> {
                // Get the selected student
                Teilnehmer selectedStudent = event.getValue();

                // Add the selected student to the combobox
                if (selectedStudent != null) {
                    comboBox.setValue(selectedStudent);
                }
            });
             */
        Button saveButton = new Button("Save", e -> persistVeranstaltung(titelField, datePicker, comboBox, grid));
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e -> addDialog.close());
        addDialog.getFooter().add(cancelButton);
        addDialog.getFooter().add(saveButton);

        addDialog.add(
                new HorizontalLayout(
                        new VerticalLayout(
                                titelField,
                                datePicker,
                                comboBox
                                ),
                        new VerticalLayout(
                                grid
                        )
                )
        );
    }

    private void persistVeranstaltung(TextField titelField, DatePicker datePicker, MultiSelectComboBox<Teilnehmer> comboBox, Grid<Teilnehmer> grid) {
        String titel = titelField.getValue();
        Date date = Date.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

        Veranstaltung veranstaltung = new Veranstaltung();
        veranstaltung.setTitel(titel);
        veranstaltung.setSemester(date);
        veranstaltung.setUser(userService.findAdmin());

        veranstaltung.setTeilnehmer(new ArrayList<>(comboBox.getSelectedItems()));

        veranstaltungenService.saveVeranstaltung(veranstaltung);

        Notification.show("Veranstaltung angelegt!");

        titelField.clear();
        datePicker.clear();
        comboBox.clear();

        UI.getCurrent().getPage().reload();
    }

}




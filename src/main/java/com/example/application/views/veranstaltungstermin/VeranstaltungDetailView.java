package com.example.application.views.veranstaltungstermin;

import com.example.application.models.Veranstaltung;
import com.example.application.models.Veranstaltungstermin;
import com.example.application.services.VeranstaltungenService;
import com.example.application.services.VeranstaltungsterminService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.*;
import java.util.List;

@PageTitle("Veranstaltung Detail")
@Route(value = "veranstaltung-detail/:veranstaltungId", layout = MainLayout.class)
public class VeranstaltungDetailView extends VerticalLayout implements HasUrlParameter<String> {

    //Services
    private final VeranstaltungenService veranstaltungService;
    private final VeranstaltungsterminService veranstaltungsterminService;

    //Data
    private String veranstaltungIdString;
    private long veranstaltungIdLong;
    private Veranstaltung veranstaltung;
    private List<Veranstaltungstermin> termine;

    //UI Elements
    private Div kachelContainer;
    private H1 veranstaltungTitle;
    private Text text;
    private Hr lineBefore;
    private Hr lineAfter;

    //Dialog Instance
    private VeranstaltungsterminDialog veranstaltungsterminDialog;

    public VeranstaltungDetailView(VeranstaltungenService veranstaltungService, VeranstaltungsterminService veranstaltungsterminService) {
        this.veranstaltungService = veranstaltungService;
        this.veranstaltungsterminService = veranstaltungsterminService;

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();

        this.kachelContainer = new Div();
        kachelContainer.addClassName("veranstaltungen-container");
        kachelContainer.getStyle().set("display", "flex");
        kachelContainer.getStyle().set("flexWrap", "wrap");

        this.veranstaltungTitle = new H1();
        veranstaltungTitle.getStyle().set("font-size", "28px");

        this.text = new Text("Veranstaltungstermine");

        this.lineBefore = new Hr();
        lineBefore.getStyle().set("flex-grow", "0");
        lineBefore.getStyle().set("flex-shrink", "0");
        lineBefore.getStyle().set("width", "30px");
        lineBefore.getStyle().set("margin-top", "15px");
        lineBefore.getStyle().set("margin-right", "-8px");

        this.lineAfter = new Hr();
        lineAfter.getStyle().set("flex-grow", "1");
        lineAfter.getStyle().set("flex-shrink", "0");
        lineAfter.getStyle().set("margin-top", "15px");
        lineAfter.getStyle().set("margin-left", "-8px");

        HorizontalLayout lineWithText = new HorizontalLayout(lineBefore, text, lineAfter);
        lineWithText.setWidth("100%");
        lineWithText.setAlignItems(Alignment.CENTER);

        mainLayout.add(veranstaltungTitle, lineWithText, kachelContainer);
        add(mainLayout);
    }

    @Override
    public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
        Location location = event.getLocation();
        List<String> segments = location.getSegments();

        if (!segments.isEmpty()) {
            this.veranstaltungIdString = segments.get(segments.size() - 1);
            try {
                veranstaltungIdLong = Long.parseLong(veranstaltungIdString);
                termine = veranstaltungsterminService.findVeranstaltungstermineByVeranstaltungId(veranstaltungIdLong);
                veranstaltung = veranstaltungService.findVeranstaltungById(veranstaltungIdLong);
            } catch (NumberFormatException e) {
                System.err.println("Invalid veranstaltungId: " + veranstaltungIdString);
            }
        } else {
            System.err.println("No veranstaltungId in URL");
        }

        createVeranstaltungsterminDialog();
        //init Methode ist wichtig, da erst hier die termine gesetzt werden, weil sonst im Konstruktor die termine noch nicht gesetzt sind,
        // wenn er aufgerufen wird, wodurch es zu einem Fehler kommt.
        init();
    }

    private void init() {
        veranstaltungTitle.setText(veranstaltung.getTitel());

        // Kacheln für vorhandene Veranstaltungstermine der Veranstaltung erstellen
        for (Veranstaltungstermin termin : termine) {
            kachelContainer.add(createVeranstaltungsterminKachel(termin));
        }

        // Kachel für neue Veranstaltung hinzufügen
        kachelContainer.add(createKachel());
    }

    private Div createVeranstaltungsterminKachel(Veranstaltungstermin veranstaltungstermin) {
        Div terminInfo = new Div();
        terminInfo.setText(veranstaltungstermin.getDatum().toString());

        terminInfo.getStyle().set("text-align", "center");
        terminInfo.getStyle().set("margin", "auto");

        Div kachelContent = new Div(terminInfo);
        kachelContent.getStyle().set("display", "flex");
        kachelContent.getStyle().set("flex-direction", "column");

        Div kachel = new Div(kachelContent);
        kachel.getStyle()
                .set("position", "relative")
                .set("border", "1px solid var(--lumo-contrast-20pct)")
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
        confirmationDialog.add(new Text("Möchten Sie den Veranstaltungstermin " + veranstaltungstermin.getDatum() + " wirklich löschen?"));

        Button yesButton = new Button("Ja", event -> {
            veranstaltungsterminService.deleteVeranstaltungstermin(veranstaltungstermin);
            Notification.show("Veranstaltungstermin gelöscht");
            getUI().ifPresent(ui -> ui.getPage().reload());
            confirmationDialog.close();
        });

        Button noButton = new Button("Nein", event -> {
            confirmationDialog.close();
            kachel.getStyle().set("background-color", "");
            deleteIcon.getStyle().set("visibility", "hidden");
        });

        confirmationDialog.add(yesButton, noButton);

        deleteIcon.getElement().addEventListener("click", e ->
            confirmationDialog.open()).addEventData("event.stopPropagation()");

        kachel.add(deleteIcon);

        kachel.getElement().addEventListener("mouseover", e -> {
            kachel.getStyle().set("background-color", "lightblue");
            deleteIcon.getStyle().set("visibility", "visible");
        });

        kachel.getElement().addEventListener("mouseout", e -> {
            kachel.getStyle().set("background-color", "");
            deleteIcon.getStyle().set("visibility", "hidden");
        });

        kachel.addClickListener(e ->
            Notification.show("Veranstaltungstermin geklickt!"));

        return kachel;
    }

    /**
     * Erstellt eine Kachel mit einem spezifischen Navigationsziel.
     *
     * Diese Kachel zeigt ein Pluszeichen und navigiert zur angegebenen Route, wenn sie angeklickt wird.
     * Die Kachel hat eine Hover-Effekt, der die Hintergrundfarbe der Kachel ändert, wenn der Mauszeiger darüber schwebt.
     *
     * @return Die erstellte Kachel als {@link Div}-Element, bereit zum Hinzufügen zum Container.
     */
    private Div createKachel() {
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

        neueVeranstaltungKachel.getElement().addEventListener("mouseover", e ->
                neueVeranstaltungKachel.getStyle().set("background-color", "lightblue"));

        neueVeranstaltungKachel.getElement().addEventListener("mouseout", e ->
                neueVeranstaltungKachel.getStyle().set("background-color", ""));

        neueVeranstaltungKachel.addClickListener(e ->
                veranstaltungsterminDialog.open());

        return neueVeranstaltungKachel;
    }

    public void createVeranstaltungsterminDialog () {
        veranstaltungsterminDialog = new VeranstaltungsterminDialog(veranstaltungService, veranstaltungsterminService, veranstaltungIdString);
    }
}
package com.example.application.views.veranstaltungstermin;

import com.example.application.models.Gruppenarbeit;
import com.example.application.models.Veranstaltung;
import com.example.application.models.Veranstaltungstermin;
import com.example.application.services.GruppenarbeitService;
import com.example.application.services.TeilnehmerService;
import com.example.application.services.VeranstaltungenService;
import com.example.application.services.VeranstaltungsterminService;
import com.example.application.views.MainLayout;
import com.example.application.views.gruppenarbeit.GruppenarbeitHinzufuegenDialog;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
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
    private final GruppenarbeitService gruppenarbeitService;
    private final TeilnehmerService teilnehmerService;

    //Data
    private String veranstaltungIdString;
    private long veranstaltungIdLong;
    private Veranstaltung veranstaltung;
    private List<Veranstaltungstermin> termine;
    private Div aktiveKachel = null;

    //UI Elements
    private Div veranstaltungsterminContainer;
    private Div gruppenarbeitContainer;
    private H1 veranstaltungTitle;

    //Dialog Instance
    private VeranstaltungsterminDialog veranstaltungsterminDialog;
    private GruppenarbeitHinzufuegenDialog gruppenarbeitHinzufuegenDialog;

    //Layout
    private final VerticalLayout mainLayout;
    private HorizontalLayout gruppenarbeitline;

    public VeranstaltungDetailView(VeranstaltungenService veranstaltungService, VeranstaltungsterminService veranstaltungsterminService, GruppenarbeitService gruppenarbeitService, TeilnehmerService teilnehmerService) {
        this.veranstaltungService = veranstaltungService;
        this.veranstaltungsterminService = veranstaltungsterminService;
        this.gruppenarbeitService = gruppenarbeitService;

        this.mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();

        this.veranstaltungTitle = new H1();
        veranstaltungTitle.getStyle().set("font-size", "28px");

        this.veranstaltungsterminContainer = new Div();
        veranstaltungsterminContainer.addClassName("veranstaltungen-container");
        veranstaltungsterminContainer.getStyle().set("display", "flex");
        veranstaltungsterminContainer.getStyle().set("flexWrap", "wrap");

        this.gruppenarbeitContainer = new Div();
        gruppenarbeitContainer.addClassName("gruppenarbeiten-container");
        gruppenarbeitContainer.getStyle().set("display", "flex");
        gruppenarbeitContainer.getStyle().set("flexWrap", "wrap");

        mainLayout.add(veranstaltungTitle, createLineWithText("Veranstaltungstermine"), veranstaltungsterminContainer);
        add(mainLayout);
        this.teilnehmerService = teilnehmerService;
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
        createGruppenarbeitHinzufuegenDialog();
        //init Methode ist wichtig, da erst hier die termine gesetzt werden, weil sonst im Konstruktor die termine noch nicht gesetzt sind,
        // wenn er aufgerufen wird, wodurch es zu einem Fehler kommt.
        init();
    }

    private void init() {
        veranstaltungTitle.setText(veranstaltung.getTitel());

        // Kacheln für vorhandene Veranstaltungstermine der Veranstaltung erstellen
        for (Veranstaltungstermin termin : termine) {
            veranstaltungsterminContainer.add(veranstaltungsterminKachel(termin));
        }

        // Kachel für neue Veranstaltung hinzufügen
        veranstaltungsterminContainer.add(createVeranstaltungsterminKachel());
    }

    private Div veranstaltungsterminKachel(Veranstaltungstermin veranstaltungstermin) {
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
        HorizontalLayout buttonLayout = new HorizontalLayout(
                new Button("Ja", event -> {
                    veranstaltungsterminService.deleteVeranstaltungstermin(veranstaltungstermin);
                    Notification.show("Veranstaltungstermin gelöscht");
                    getUI().ifPresent(ui -> ui.getPage().reload());
                    confirmationDialog.close();
                }),
                new Button("Nein", event -> {
                    confirmationDialog.close();
                    kachel.getStyle().set("background-color", "");
                    deleteIcon.getStyle().set("visibility", "hidden");
                })
        );
        buttonLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        confirmationDialog.add(
                new VerticalLayout(
                        new Text("Möchten Sie den Veranstaltungstermin für den " + veranstaltungstermin.getDatum() + " wirklich löschen?"),
                        buttonLayout
                )
        );

        deleteIcon.getElement().addEventListener("click", e ->
            confirmationDialog.open()).addEventData("event.stopPropagation()");

        kachel.add(deleteIcon);

        kachel.getElement().addEventListener("mouseover", e -> {
            if (!kachel.equals(aktiveKachel)) {
                kachel.getStyle().set("background-color", "lightblue");
            }
            deleteIcon.getStyle().set("visibility", "visible");
        });

        kachel.getElement().addEventListener("mouseout", e -> {
            if (!kachel.equals(aktiveKachel)) {
                kachel.getStyle().set("background-color", "");
            }
            deleteIcon.getStyle().set("visibility", "hidden");
        });

        kachel.addClickListener(e -> {
            // Wenn die aktive Kachel die angeklickte Kachel ist, entfernen Sie die Gruppenarbeit-Kacheln und die Linie.
            if (kachel.equals(aktiveKachel)) {
                gruppenarbeitContainer.removeAll();
                gruppenarbeitline.setVisible(false);
                aktiveKachel.getStyle().set("background-color", "");
                aktiveKachel = null;
            } else {
                // Setzen Sie die Hintergrundfarbe der aktiven Kachel zurück
                if (aktiveKachel != null) {
                    aktiveKachel.getStyle().set("background-color", "");
                }

                // Setzen Sie die Hintergrundfarbe der angeklickten Kachel auf blau und machen Sie sie zur aktiven Kachel
                kachel.getStyle().set("background-color", "lightblue");
                aktiveKachel = kachel;

                // Entfernen Sie alle vorhandenen Gruppenarbeit-Kacheln
                gruppenarbeitContainer.removeAll();

                if (gruppenarbeitline == null) {
                    gruppenarbeitline = createLineWithText("Gruppenarbeiten");
                    mainLayout.add(gruppenarbeitline, gruppenarbeitContainer);
                }

                // Fügen Sie Kacheln für jede Gruppenarbeit des Veranstaltungstermins hinzu
                for (Gruppenarbeit gruppenarbeit : veranstaltungstermin.getGruppenarbeiten()) {
                    gruppenarbeitContainer.add(gruppenarbeitKachel(gruppenarbeit));
                }

                // Fügen Sie die Kachel zum Erstellen einer neuen Gruppenarbeit hinzu
                gruppenarbeitContainer.add(createGruppenarbeitKachel());

                gruppenarbeitline.setVisible(true);
                gruppenarbeitContainer.setVisible(true);

                // Setzen Sie die Hintergrundfarbe der angeklickten Kachel auf Blau und machen Sie sie zur aktiven Kachel
                kachel.getStyle().set("background-color", "#4682B4");
                aktiveKachel = kachel;
            }
        });

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

    private Div createVeranstaltungsterminKachel() {
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

    public void createGruppenarbeitHinzufuegenDialog() {
        gruppenarbeitHinzufuegenDialog = new GruppenarbeitHinzufuegenDialog(gruppenarbeitService, teilnehmerService, veranstaltungsterminService);
        gruppenarbeitHinzufuegenDialog.setWidth("1500px");
    }

    private Div gruppenarbeitKachel(Gruppenarbeit gruppenarbeit) {
        Div gruppenarbeitInfo = new Div();
        gruppenarbeitInfo.setText(gruppenarbeit.getTitel());

        gruppenarbeitInfo.getStyle().set("text-align", "center");
        gruppenarbeitInfo.getStyle().set("margin", "auto");

        Div kachelContent = new Div(gruppenarbeitInfo);
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
        HorizontalLayout buttonLayout = new HorizontalLayout(
                new Button("Ja", event -> {
                    gruppenarbeitService.deleteGruppenarbeit(gruppenarbeit);
                    confirmationDialog.close();
                }),
                new Button("Nein", event -> {
                    confirmationDialog.close();
                    kachel.getStyle().set("background-color", "");
                    deleteIcon.getStyle().set("visibility", "hidden");
                })
        );
        buttonLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        confirmationDialog.add(
                new VerticalLayout(
                        new Text("Möchten Sie die Gruppenarbeit " + gruppenarbeit.getTitel() + " wirklich löschen?"),
                        buttonLayout
                )
        );

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
            Notification.show("Gruppenarbeit geklickt!"));

        return kachel;
    }

    private Div createGruppenarbeitKachel() {
        //Ich kann diese Methode nicht als static machen, weil getUi() nicht statisch ist.
        //Dadurch muss ich diese Methode in jeder Klasse neu einbauen, wo ich sie verwenden möchte.
        Div plusSymbol = new Div();
        plusSymbol.setText("+");
        plusSymbol.getStyle()
                .set("font-size", "40px")
                .set("text-align", "center")
                .set("margin", "auto");

        Div neueGruppenarbeitKachel = new Div(plusSymbol);
        neueGruppenarbeitKachel.getStyle()
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "10px")
                .set("padding", "1em")
                .set("margin", "0.5em")
                .set("cursor", "pointer")
                .set("box-shadow", "0 4px 8px 0 rgba(0,0,0,0.2)")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center");
        neueGruppenarbeitKachel.setWidth("150px");
        neueGruppenarbeitKachel.setHeight("150px");

        neueGruppenarbeitKachel.getElement().addEventListener("mouseover", e ->
                neueGruppenarbeitKachel.getStyle().set("background-color", "lightblue"));

        neueGruppenarbeitKachel.getElement().addEventListener("mouseout", e ->
                neueGruppenarbeitKachel.getStyle().set("background-color", ""));

        neueGruppenarbeitKachel.addClickListener(e ->
                gruppenarbeitHinzufuegenDialog.open());

        return neueGruppenarbeitKachel;
    }

    private HorizontalLayout createLineWithText(String text) {
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

        Text lineText = new Text(text);

        HorizontalLayout lineWithText = new HorizontalLayout(lineBefore, lineText, lineAfter);
        lineWithText.setWidth("100%");
        lineWithText.setAlignItems(Alignment.CENTER);

        return lineWithText;
    }
}
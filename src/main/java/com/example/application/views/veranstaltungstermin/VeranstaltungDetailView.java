package com.example.application.views.veranstaltungstermin;

import com.example.application.models.*;
import com.example.application.services.*;
import com.example.application.views.MainLayout;
import com.example.application.views.gruppenarbeit.GruppeAuswertungDialog;
import com.example.application.views.gruppenarbeit.GruppenarbeitHinzufuegenDialog;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@PageTitle("Veranstaltung Detail")
@Route(value = "veranstaltung-detail/:veranstaltungId", layout = MainLayout.class)
@RolesAllowed({"ADMIN"})
public class VeranstaltungDetailView extends VerticalLayout implements HasUrlParameter<String> {

    //Services
    private final VeranstaltungenService veranstaltungService;
    private final VeranstaltungsterminService veranstaltungsterminService;
    private final GruppenarbeitService gruppenarbeitService;
    private final TeilnehmerService teilnehmerService;
    private final GruppeService gruppeService;

    //Data
    private String veranstaltungIdString;
    private Veranstaltung veranstaltung;
    private List<Veranstaltungstermin> termine;
    private Div aktiveKachelVeranstaltungstermin = null;
    private Div aktiveKachelGruppenarbeit = null;

    //UI Elements
    private final Div veranstaltungsterminContainer;
    private final Div gruppenarbeitContainer;
    private final Div gruppenContainer;
    private final H1 veranstaltungTitle;

    //Dialog Instance
    private VeranstaltungsterminDialog veranstaltungsterminDialog;
    private GruppenarbeitHinzufuegenDialog gruppenarbeitHinzufuegenDialog;
    private GruppeAuswertungDialog gruppeAuswertungDialog;

    //Layout
    private final VerticalLayout mainLayout;
    private HorizontalLayout gruppenarbeitLinie;
    private HorizontalLayout gruppenLinie;

    public VeranstaltungDetailView(VeranstaltungenService veranstaltungService, VeranstaltungsterminService veranstaltungsterminService, GruppenarbeitService gruppenarbeitService, TeilnehmerService teilnehmerService, GruppeService gruppeService) {
        this.veranstaltungService = veranstaltungService;
        this.veranstaltungsterminService = veranstaltungsterminService;
        this.gruppenarbeitService = gruppenarbeitService;
        this.gruppeService = gruppeService;

        this.mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();

        this.veranstaltungTitle = new H1();
        veranstaltungTitle.addClassName("veranstaltung-title");

        this.veranstaltungsterminContainer = new Div();
        veranstaltungsterminContainer.addClassName("veranstaltungen-container");

        this.gruppenarbeitContainer = new Div();
        gruppenarbeitContainer.addClassName("gruppenarbeiten-container");

        this.gruppenContainer = new Div();
        gruppenContainer.addClassName("gruppen-container");

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
                long veranstaltungIdLong = Long.parseLong(veranstaltungIdString);
                termine = veranstaltungsterminService.findVeranstaltungstermineByVeranstaltungId(veranstaltungIdLong);
                veranstaltung = veranstaltungService.findVeranstaltungById(veranstaltungIdLong);
            } catch (NumberFormatException e) {
                System.err.println("Invalid veranstaltungId: " + veranstaltungIdString);
            }
        } else {
            System.err.println("No veranstaltungId in URL");
        }

        createVeranstaltungsterminDialog();
        createGruppenarbeitDialog();

        //init Methode ist wichtig, da erst hier die termine gesetzt werden, weil sonst im Konstruktor die termine noch nicht gesetzt sind,
        // wenn er aufgerufen wird, wodurch es zu einem Fehler kommt.
        init();
    }

    private void init() {
        veranstaltungTitle.setText(veranstaltung.getTitel());

        for (Veranstaltungstermin termin : termine) {
            veranstaltungsterminContainer.add(veranstaltungsterminKachel(termin));
        }

        veranstaltungsterminContainer.add(createVeranstaltungsterminKachel());
    }

    private Div veranstaltungsterminKachel(Veranstaltungstermin veranstaltungstermin) {
        Div terminInfo = new Div();
        terminInfo.setText(veranstaltungstermin.getDatum().toString());
        terminInfo.addClassName("text-center");

        Div kachelContent = new Div(terminInfo);
        kachelContent.addClassName("kachel-content");

        Div kachel = new Div(kachelContent);
        kachel.addClassName("kachel");

        Div deleteIcon = new Div();
        deleteIcon.setText("🗑️");
        deleteIcon.addClassName("delete-icon");

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
                    kachel.removeClassName("kachel-active");
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
            if (!kachel.equals(aktiveKachelVeranstaltungstermin)) {
                kachel.addClassName("kachel-hover");
            }
            deleteIcon.getStyle().set("visibility", "visible");
        });

        kachel.getElement().addEventListener("mouseout", e -> {
            if (!kachel.equals(aktiveKachelVeranstaltungstermin)) {
                kachel.removeClassName("kachel-hover");
            }
            deleteIcon.getStyle().set("visibility", "hidden");
        });

        kachel.addClickListener(e -> {
            if (kachel.equals(aktiveKachelVeranstaltungstermin)) {
                gruppenarbeitContainer.removeAll();
                gruppenarbeitLinie.setVisible(false);
                aktiveKachelVeranstaltungstermin.removeClassName("kachel-active");
                aktiveKachelVeranstaltungstermin = null;
            } else {

                if (aktiveKachelVeranstaltungstermin != null) {
                    aktiveKachelVeranstaltungstermin.removeClassName("kachel-active");
                }

                gruppenarbeitContainer.removeAll();

                if (gruppenarbeitLinie == null) {
                    gruppenarbeitLinie = createLineWithText("Gruppenarbeiten");
                    mainLayout.add(gruppenarbeitLinie, gruppenarbeitContainer);
                }

                for (Gruppenarbeit gruppenarbeit : veranstaltungstermin.getGruppenarbeiten()) {
                    gruppenarbeitContainer.add(gruppenarbeitKachel(gruppenarbeit));
                }

                gruppenarbeitContainer.add(createGruppenarbeitKachel());

                gruppenarbeitLinie.setVisible(true);
                gruppenarbeitContainer.setVisible(true);

                kachel.addClassName("kachel-active");
                aktiveKachelVeranstaltungstermin = kachel;
            }
        });

        return kachel;
    }

    private Div createVeranstaltungsterminKachel() {
        //Ich kann diese Methode nicht als static machen, weil getUi() nicht statisch ist.
        //Dadurch muss ich diese Methode in jeder Klasse neu einbauen, wo ich sie verwenden möchte.
        Div plusSymbol = new Div();
        plusSymbol.setText("+");
        plusSymbol.addClassName("plus-symbol");

        Div neueVeranstaltungKachel = new Div(plusSymbol);
        neueVeranstaltungKachel.addClassName("neue-veranstaltung-kachel");

        neueVeranstaltungKachel.getElement().addEventListener("mouseover", e ->
                neueVeranstaltungKachel.addClassName("hover"));

        neueVeranstaltungKachel.getElement().addEventListener("mouseout", e ->
                neueVeranstaltungKachel.removeClassName("hover"));

        neueVeranstaltungKachel.addClickListener(e ->
                veranstaltungsterminDialog.open());

        return neueVeranstaltungKachel;
    }


    public void createVeranstaltungsterminDialog () {
        veranstaltungsterminDialog = new VeranstaltungsterminDialog(veranstaltungService, veranstaltungsterminService, veranstaltungIdString);
    }

    public void createGruppenarbeitDialog() {
        gruppenarbeitHinzufuegenDialog = new GruppenarbeitHinzufuegenDialog(gruppenarbeitService, teilnehmerService, veranstaltungsterminService);
        gruppenarbeitHinzufuegenDialog.setWidth("1500px");
    }

    private Div gruppenarbeitKachel(Gruppenarbeit gruppenarbeit) {
        Div gruppenarbeitInfo = new Div();
        gruppenarbeitInfo.setText(gruppenarbeit.getTitel());
        gruppenarbeitInfo.addClassName("text-center");

        Div kachelContent = new Div(gruppenarbeitInfo);
        kachelContent.addClassName("kachel-content");

        Div kachel = new Div(kachelContent);
        kachel.addClassName("kachel");

        Div deleteIcon = new Div();
        deleteIcon.setText("🗑️");
        deleteIcon.addClassName("delete-icon");

        Dialog confirmationDialog = new Dialog();
        HorizontalLayout buttonLayout = new HorizontalLayout(
                new Button("Ja", event -> {
                    gruppenarbeitService.deleteGruppenarbeit(gruppenarbeit);
                    confirmationDialog.close();
                }),
                new Button("Nein", event -> {
                    confirmationDialog.close();
                    kachel.removeClassName("kachel-active");
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
            if (!kachel.equals(aktiveKachelGruppenarbeit)) {
                kachel.addClassName("kachel-hover");
            }
            deleteIcon.getStyle().set("visibility", "visible");
        });

        kachel.getElement().addEventListener("mouseout", e -> {
            if (!kachel.equals(aktiveKachelGruppenarbeit)) {
                kachel.removeClassName("kachel-hover");
            }
            deleteIcon.getStyle().set("visibility", "hidden");
        });

        kachel.addClickListener(e -> {
            if (kachel.equals(aktiveKachelGruppenarbeit)) {
                gruppenContainer.removeAll();
                gruppenLinie.setVisible(false);
                aktiveKachelGruppenarbeit.removeClassName("kachel-active");
                aktiveKachelGruppenarbeit = null;
            } else {

                if (aktiveKachelGruppenarbeit != null) {
                    aktiveKachelGruppenarbeit.removeClassName("kachel-active");
                }

                gruppenContainer.removeAll();

                if (gruppenLinie == null) {
                    gruppenLinie = createLineWithText("Gruppen");
                    mainLayout.add(gruppenLinie, gruppenContainer);
                }

                Gruppenarbeit fullGruppenarbeit = gruppenarbeitService.findGruppenarbeitByIdWithGruppen(gruppenarbeit.getId());

                if (fullGruppenarbeit.getGruppen() != null) {
                    for (Gruppe gruppe : fullGruppenarbeit.getGruppen()) {
                        gruppenContainer.add(gruppenKachel(gruppe));
                    }
                } else {
                    System.out.println("Keine Gruppen gefunden.");
                    //hier was einbauen, um Nutzer anzuzeigen, dass es keine Gruppen gibt
                }

                gruppenLinie.setVisible(true);
                gruppenContainer.setVisible(true);

                kachel.addClassName("kachel-active");
                aktiveKachelGruppenarbeit = kachel;
            }
        });

        return kachel;
    }

    private Div createGruppenarbeitKachel() {
        Div plusSymbol = new Div();
        plusSymbol.setText("+");
        plusSymbol.addClassName("plus-symbol");

        Div neueGruppenarbeitKachel = new Div(plusSymbol);
        neueGruppenarbeitKachel.addClassName("neue-gruppenarbeit-kachel");

        neueGruppenarbeitKachel.getElement().addEventListener("mouseover", e ->
                neueGruppenarbeitKachel.addClassName("hover"));

        neueGruppenarbeitKachel.getElement().addEventListener("mouseout", e ->
                neueGruppenarbeitKachel.removeClassName("hover"));

        neueGruppenarbeitKachel.addClickListener(e ->
                gruppenarbeitHinzufuegenDialog.open());

        return neueGruppenarbeitKachel;
    }

    private HorizontalLayout createLineWithText(String text) {
        Hr lineBefore = new Hr();
        lineBefore.addClassName("line-before");

        Hr lineAfter = new Hr();
        lineAfter.addClassName("line-after");

        Text lineText = new Text(text);

        HorizontalLayout lineWithText = new HorizontalLayout(lineBefore, lineText, lineAfter);
        lineWithText.addClassName("line-with-text");

        return lineWithText;
    }

    private Div gruppenKachel(Gruppe gruppe) {
        // Laden der Gruppe mit den Teilnehmern
        Gruppe fullGruppe = gruppeService.findGruppeByIdWithTeilnehmer(gruppe.getId());

        Div gruppenInfo = new Div();
        gruppenInfo.setText("Gruppe " + fullGruppe.getNummer());
        gruppenInfo.addClassName("text-center");

        // Erstellen der Teilnehmerliste
        VirtualList<Teilnehmer> teilnehmerList = new VirtualList<>();
        teilnehmerList.setItems(fullGruppe.getTeilnehmer());
        teilnehmerList.setRenderer(new ComponentRenderer<>(teilnehmer -> {
            Div teilnehmerDiv = new Div();
            teilnehmerDiv.addClassName("teilnehmer-item");

            // Profilbild mit Initialen
            Span profilbild = new Span();
            profilbild.setText(teilnehmer.getVorname().charAt(0) + "" + teilnehmer.getNachname().charAt(0));
            profilbild.addClassName("profilbild");

            // Name des Teilnehmers
            Span name = new Span(teilnehmer.getVorname() + " " + teilnehmer.getNachname());
            name.addClassName("teilnehmer-name");

            teilnehmerDiv.add(profilbild, name);

            // Klick-Listener für Teilnehmer
            teilnehmerDiv.addClickListener(e -> {
                gruppeAuswertungDialog = new GruppeAuswertungDialog(teilnehmer);
                gruppeAuswertungDialog.open();
            });

            return teilnehmerDiv;
        }));

        // Hinzufügen der Teilnehmerliste zur Kachel
        Div kachelContent = new Div(gruppenInfo, teilnehmerList);
        kachelContent.addClassName("kachel-content");

        Div kachel = new Div(kachelContent);
        kachel.addClassName("kachel-gruppen");

        return kachel;
    }

}
//Author: Joris
package com.example.application.views.veranstaltungen;

import com.example.application.models.Veranstaltung;
import com.example.application.services.TeilnehmerService;
import com.example.application.services.UserService;
import com.example.application.services.VeranstaltungenService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@PageTitle("Veranstaltungen")
@Route(value = "", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class VeranstaltungenView extends VerticalLayout {

    //Services
    private final VeranstaltungenService veranstaltungenService;
    private final TeilnehmerService teilnehmerService;
    private final UserService userService;

    private Div kachelContainer = new Div();

    //UI Elements
    private H1 username;
    private Text text;
    private Hr lineBefore;
    private Hr lineAfter;

    @Autowired
    public VeranstaltungenView(VeranstaltungenService veranstaltungenService, UserService userService, TeilnehmerService teilnehmerService) {
        this.veranstaltungenService = veranstaltungenService;
        this.teilnehmerService = teilnehmerService;
        this.userService = userService;

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();

        //Hier später noch die Logik für den Namen des Users einbauen.
        this.username = new H1("Herzlich Willkommen, XY");
        username.getStyle().set("font-size", "28px");

        this.text = new Text("Veranstaltungen");

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

        kachelContainer.addClassName("veranstaltungen-container");
        kachelContainer.getStyle().set("display", "flex");
        kachelContainer.getStyle().set("flexWrap", "wrap");

        updateKachelContainer();
        mainLayout.add(username, lineWithText, kachelContainer);
        add(mainLayout);
    }

    public void updateKachelContainer() {
        kachelContainer.removeAll();


        // Alle Veranstaltungen aus der Datenbank abrufen
        List<Veranstaltung> veranstaltungen = veranstaltungenService.findAllVeranstaltungen();

        // Kacheln für vorhandene Veranstaltungen erstellen
        for (Veranstaltung veranstaltung : veranstaltungen) {
            kachelContainer.add(createVeranstaltungKachel(veranstaltung));
        }

        // Kachel für neue Veranstaltung hinzufügen
        kachelContainer.add(createKachel("add-veranstaltung"));

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

        Div editIcon = new Div();
        editIcon.setText("✏️");
        editIcon.addClassName("edit-icon");
        editIcon.getStyle().set("position", "absolute");
        editIcon.getStyle().set("bottom", "5px");
        editIcon.getStyle().set("left", "5px");
        editIcon.getStyle().set("visibility", "hidden");


        //Confirm-Dialog initialisieren
        Dialog confirmationDialog = new Dialog();
        //Bearbeiten-Dialog initialisieren
        VeranstaltungBearbeiten editDialog = new VeranstaltungBearbeiten(veranstaltungenService, teilnehmerService, userService, veranstaltung, this);

        Button confirmbutton = new Button("Ja", event -> {
            veranstaltungenService.deleteVeranstaltung(veranstaltung);
            Notification.show("Veranstaltung gelöscht");
            this.updateKachelContainer();
            //getUI().ifPresent(ui -> ui.getPage().reload());
            confirmationDialog.close();
        });

        Button cancelButton = new Button("Nein", event -> {
            confirmationDialog.close();
            kachel.getStyle().set("background-color", "");
        });
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        VerticalLayout verticalLayout = new VerticalLayout(
                new Text("Möchten Sie die Veranstaltung (" + veranstaltung.getTitel() + ") wirklich löschen?"),
                new HorizontalLayout(
                        confirmbutton,
                        cancelButton
                )
        );

        verticalLayout.setAlignItems(Alignment.CENTER);

        confirmationDialog.add(
                verticalLayout
        );

        //Icons
        deleteIcon.getElement().addEventListener("click", e -> {
            confirmationDialog.open();
        }).addEventData("event.stopPropagation()");

        editIcon.getElement().addEventListener("click", e-> {
            editDialog.open();
            editDialog.readBean();
        }).addEventData("event.stopPropagation()");

        kachel.add(deleteIcon);
        kachel.add(editIcon);

        kachel.getElement().addEventListener("mouseover", e -> {
            kachel.getStyle().set("background-color", "lightblue");
            deleteIcon.getStyle().set("visibility", "visible");
            editIcon.getStyle().set("visibility", "visible");
        });

        kachel.getElement().addEventListener("mouseout", e -> {
            kachel.getStyle().set("background-color", "");
            deleteIcon.getStyle().set("visibility", "hidden");
            editIcon.getStyle().set("visibility", "hidden");
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

        //
        VeranstaltungDialog createDialog = new VeranstaltungDialog(veranstaltungenService, teilnehmerService, userService, this);

        neueVeranstaltungKachel.addClickListener(e -> {
            createDialog.open();
        });

        return neueVeranstaltungKachel;
    }

}




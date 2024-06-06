//Author: Joris
package com.example.application.views.veranstaltungen;

import com.example.application.models.User;
import com.example.application.models.Veranstaltung;
import com.example.application.security.AuthenticatedUser;
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
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.List;
import java.util.Optional;

@PageTitle("Veranstaltungen")
@Route(value = "", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "USER"})
public class VeranstaltungenView extends VerticalLayout {

    //Services
    private final VeranstaltungenService veranstaltungenService;
    private final TeilnehmerService teilnehmerService;
    private final UserService userService;
    private AuthenticatedUser authenticatedUser;

    private final Div kachelContainer = new Div();

    @Autowired
    public VeranstaltungenView(VeranstaltungenService veranstaltungenService, UserService userService, TeilnehmerService teilnehmerService, AuthenticatedUser authenticatedUser) {
        this.veranstaltungenService = veranstaltungenService;
        this.teilnehmerService = teilnehmerService;
        this.userService = userService;
        this.authenticatedUser = authenticatedUser;

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();

        H1 username = new H1("Herzlich Willkommen!");
        username.getStyle().set("font-size", "28px");

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            username.setText("Herzlich Willkommen, " + user.getName() + "!");
        }

        Text text = new Text("Veranstaltungen");

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

        // Alle Veranstaltungen des angemeldeten Benutzers aus der Datenbank abrufen
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            List<Veranstaltung> veranstaltungen = veranstaltungenService.findAllVeranstaltungenByUser(user);

            // Kacheln für vorhandene Veranstaltungen erstellen
            for (Veranstaltung veranstaltung : veranstaltungen) {
                kachelContainer.add(createVeranstaltungKachel(veranstaltung));
            }
        } else {
            Notification.show("Bitte melden Sie sich an, um Ihre Veranstaltungen zu sehen.");
        }

        // Kachel für neue Veranstaltung hinzufügen
        kachelContainer.add(createKachel());
    }

    /**
     * Erstellt eine Kachel für eine spezifische Veranstaltung.
     *
     * @param veranstaltung Die Veranstaltung, für die eine Kachel erstellt werden soll.
     * @return Die erstellte Kachel als {@link Div}-Element, fertig zum Hinzufügen zum Container.
     */
    private Div createVeranstaltungKachel(Veranstaltung veranstaltung) {
        Div veranstaltungInfo = new Div();
        veranstaltungInfo.setText(veranstaltung.getTitel());
        veranstaltungInfo.addClassName("text-center");

        Div kachelContent = new Div(veranstaltungInfo);
        kachelContent.addClassName("kachel-content");

        Div kachel = new Div(kachelContent);
        kachel.addClassName("kachel");

        SvgIcon deleteIconSvg = LineAwesomeIcon.TRASH_ALT.create();
        deleteIconSvg.setColor("#D2042D");
        Div deleteIcon = new Div(deleteIconSvg);
        deleteIcon.add();
        deleteIcon.addClassName("delete-icon");

        SvgIcon editIconSvg = LineAwesomeIcon.EDIT.create();
        editIconSvg.setColor("#2B64D6");
        Div editIcon = new Div(editIconSvg);
        editIcon.addClassName("edit-icon");

        //Confirm-Dialog initialisieren
        Dialog confirmationDialog = new Dialog();
        //Bearbeiten-Dialog initialisieren
        VeranstaltungBearbeiten editDialog = new VeranstaltungBearbeiten(veranstaltungenService, teilnehmerService, userService, veranstaltung, this, authenticatedUser);

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
            confirmationDialog.add( verticalLayout );

        //Bearbeiten-Dialog initialisieren
        editDialog.addOpenedChangeListener(e -> {
            if (!e.isOpened()) {
                deleteIcon.getStyle().set("visibility", "hidden");
                editIcon.getStyle().set("visibility", "hidden");
                kachel.getStyle().set("background-color", "");
            }
        });

        //Icons
        deleteIcon.getElement().addEventListener("click", e ->
            confirmationDialog.open()
        ).addEventData("event.stopPropagation()");

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
            String veranstaltungID = veranstaltung.getId().toString();
            getUI().ifPresent(ui -> ui.navigate("veranstaltung-detail/" + veranstaltungID));
        });

        return kachel;
    }

    private Div createKachel() {
        //Ich kann diese Methode nicht als static machen, weil getUi() nicht statisch ist.
        //Dadurch muss ich diese Methode in jeder Klasse neu einbauen, wo ich sie verwenden möchte.
        Div plusSymbol = new Div();
        plusSymbol.setText("+");
        plusSymbol.addClassName("plus-symbol");

        Div neueVeranstaltungKachel = new Div(plusSymbol);
        neueVeranstaltungKachel.addClassName("neue-veranstaltung-kachel");

        neueVeranstaltungKachel.getElement().addEventListener("mouseover", e ->
            neueVeranstaltungKachel.addClassName("hover")
        );

        neueVeranstaltungKachel.getElement().addEventListener("mouseout", e ->
            neueVeranstaltungKachel.removeClassName("hover")
        );

        VeranstaltungDialog createDialog = new VeranstaltungDialog(veranstaltungenService, teilnehmerService, userService, this, authenticatedUser);

        neueVeranstaltungKachel.addClickListener(e ->
            createDialog.open()
        );

        return neueVeranstaltungKachel;
    }

}




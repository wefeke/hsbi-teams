package com.example.application.views.veranstaltungen;

import com.example.application.models.*;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Ein Dialogfenster zum endgültigen Löschen einer Veranstaltung und aller zugehörigen Daten.
 * Die Klasse bietet Funktionen zum Anzeigen von Informationen über die zu löschende Veranstaltung,
 * zum Hinzufügen von Funktionalitäten zu den Buttons (Löschen und Abbrechen) und zur transaktionalen
 * Löschung der Veranstaltung samt zugehöriger Termine, Gruppenarbeiten und Gruppen.
 *
 * @author Lilli
 */
@SuppressWarnings("SpringTransactionalMethodCallsInspection")
public class VeranstaltungLoeschenDialog extends Dialog {
    //Data
    private Veranstaltung veranstaltung;

    //UI Elements
    private final H2 infoText = new H2("Empty");
    private final Button deleteBtn = new Button("Veranstaltung endgültig löschen");
    private final Button cancelBtn = new Button("Abbrechen");
    private final Paragraph warningText = new Paragraph("Empty");
    private final Paragraph noReturn = new Paragraph("Empty");

    /**
     * Konstruktor für den VeranstaltungLoeschenDialog.
     *
     * @param veranstaltungsterminService Der Service für Veranstaltungstermine.
     * @param gruppenarbeitService       Der Service für Gruppenarbeiten.
     * @param gruppeService              Der Service für Gruppen.
     * @param veranstaltungenService     Der Service für Veranstaltungen.
     * @param veranstaltungenView        Die View für Veranstaltungen.
     * @param authenticatedUser          Der aktuell authentifizierte Benutzer.
     *
     * @author Lilli
     */
    public VeranstaltungLoeschenDialog(VeranstaltungsterminService veranstaltungsterminService, GruppenarbeitService gruppenarbeitService, GruppeService gruppeService, VeranstaltungenService veranstaltungenService, VeranstaltungenView veranstaltungenView, AuthenticatedUser authenticatedUser) {
        styleElements();
        addButtonFunctionalities(veranstaltungsterminService, gruppenarbeitService, gruppeService, veranstaltungenService, veranstaltungenView, authenticatedUser);
        add(createLayout());
    }

    /**
     * Fügt den Buttons (Löschen und Abbrechen) ihre Funktionalitäten hinzu.
     *
     * @param veranstaltungsterminService Der Service für Veranstaltungstermine.
     * @param gruppenarbeitService       Der Service für Gruppenarbeiten.
     * @param gruppeService              Der Service für Gruppen.
     * @param veranstaltungenService     Der Service für Veranstaltungen.
     * @param veranstaltungenView        Die View für Veranstaltungen.
     * @param authenticatedUser          Der aktuell authentifizierte Benutzer.
     *
     * @author Lilli
     */

    private void addButtonFunctionalities(VeranstaltungsterminService veranstaltungsterminService, GruppenarbeitService gruppenarbeitService, GruppeService gruppeService, VeranstaltungenService veranstaltungenService, VeranstaltungenView veranstaltungenView, AuthenticatedUser authenticatedUser) {
        deleteBtn.addClickListener(event -> {
            Optional<User> maybeUser = authenticatedUser.get();
            if (maybeUser.isPresent()) {
                User user = maybeUser.get();
                this.veranstaltung = veranstaltungenService.findVeranstaltungById(this.veranstaltung.getId(), user);
            }
            deleteEverything(veranstaltungsterminService, gruppenarbeitService, gruppeService, veranstaltungenService);
            veranstaltungenView.updateKachelContainer("");
            close();
        });
        cancelBtn.addClickListener(event -> close());
    }

    /**
     * Passt das Erscheinungsbild der UI-Elemente des Dialogs an.
     *
     * @author Lilli
     */
    private void styleElements() {
        warningText.addClassName("warning-text-delete");
        warningText.getStyle().set("white-space", "pre-line");
        noReturn.addClassName("no-return-text-delete");
        noReturn.getStyle().set("white-space", "pre-line");

        deleteBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    }

    /**
     * Transaktionale Methode zum vollständigen Löschen aller Daten der Veranstaltung und ihrer zugehörigen Elemente.
     *
     * @param veranstaltungsterminService Der Service für Veranstaltungstermine.
     * @param gruppenarbeitService       Der Service für Gruppenarbeiten.
     * @param gruppeService              Der Service für Gruppen.
     * @param veranstaltungenService     Der Service für Veranstaltungen.
     *
     * @author Lilli
     */
    @Transactional
    protected void deleteEverything(VeranstaltungsterminService veranstaltungsterminService, GruppenarbeitService gruppenarbeitService, GruppeService gruppeService, VeranstaltungenService veranstaltungenService) {
        assert veranstaltung != null;
        List<Veranstaltungstermin> termine = veranstaltung.getVeranstaltungstermine();
        veranstaltung.removeAllTermine();
        veranstaltung.getTeilnehmer();
        veranstaltung.removeAllTeilnehmer();
        veranstaltungenService.saveVeranstaltung(veranstaltung);

        for(Veranstaltungstermin termin: termine){
            List<Gruppenarbeit> gruppenarbeiten = termin.getGruppenarbeiten();
            termin.removeAllGruppenarbeiten();
            veranstaltungsterminService.saveVeranstaltungstermin(termin);

            for(Gruppenarbeit gruppenarbeit: gruppenarbeiten){
                List<Gruppe> gruppen = gruppenarbeit.getGruppen();
                gruppenarbeit.removeAllGruppen();
                gruppenarbeitService.save(gruppenarbeit);

                for (Gruppe gruppe : gruppen) {
                    gruppeService.deleteGruppe(gruppe);
                }

                termin.removeGruppenarbeit(gruppenarbeit);
                veranstaltungsterminService.saveVeranstaltungstermin(termin);

                gruppenarbeitService.deleteGruppenarbeit(gruppenarbeit);
            }

            veranstaltungsterminService.deleteVeranstaltungstermin(termin);
        }

        veranstaltungenService.deleteVeranstaltung(veranstaltung);
    }

    /**
     * Setzt die Veranstaltung, die gelöscht werden soll, und aktualisiert die Anzeigeinformationen entsprechend.
     *
     * @param veranstaltung Die zu löschende Veranstaltung.
     *
     * @author Lilli
     */
    public void setVeranstaltung(Veranstaltung veranstaltung) {
        this.veranstaltung = veranstaltung;
        int anzGruppenarbeiten = 0;
        int anzGruppen = 0;
        List<Veranstaltungstermin> termine = this.veranstaltung.getVeranstaltungstermine();
        List<Gruppenarbeit> gruppenarbeiten;
        List<Gruppe> gruppen;
        for(Veranstaltungstermin termin: termine){
            gruppenarbeiten = termin.getGruppenarbeiten();
            anzGruppenarbeiten += gruppenarbeiten.size();
            for(Gruppenarbeit gruppenarbeit:gruppenarbeiten){
                gruppen = gruppenarbeit.getGruppen();
                anzGruppen += gruppen.size();
            }
        }

        infoText.setText("Veranstaltung " + this.veranstaltung.getTitel() + " löschen");
        warningText.setText("Wenn du die Veranstaltung " +
                this.veranstaltung.getTitel() +
                " löscht,\n werden " + "auch alle zugehörigen Termine (Anzahl: " +
                termine.size() + "), \ndie zu den Terminen gehörenden Gruppenarbeiten (Gesamtzahl: " +
                anzGruppenarbeiten + ") \nund die zu den Gruppenarbeiten gehörenden Gruppen (Gesamtzahl: "
                + anzGruppen + ") gelöscht.");
        noReturn.setText("Bist du sicher, dass du die Veranstaltung " + this.veranstaltung.getTitel() +
                " löschen willst?\n" + "Das kann nicht rückgängig gemacht werden!");
    }

    /**
     * Erstellt das Layout des Dialogs mit den erforderlichen UI-Elementen und deren Anordnung.
     *
     * @return Das erstellte VerticalLayout für den Dialog.
     *
     * @author Lilli
     */
    private VerticalLayout createLayout(){
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        mainLayout.add(infoText);
        mainLayout.add(warningText);
        mainLayout.add(noReturn);
        getFooter().add(cancelBtn);
        getFooter().add(deleteBtn);
        return mainLayout;
    }

}

package com.example.application.views.veranstaltungstermin;

import com.example.application.models.Gruppe;
import com.example.application.models.Gruppenarbeit;
import com.example.application.models.Veranstaltungstermin;
import com.example.application.services.GruppeService;
import com.example.application.services.GruppenarbeitService;
import com.example.application.services.VeranstaltungsterminService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Ein Dialog zur endgültigen Löschung eines Veranstaltungstermins und aller zugehörigen Daten.
 * Verwendet Vaadin Flow für die Benutzeroberfläche und bietet Funktionen zum Setzen des zu löschenden
 * Veranstaltungstermins, Anpassen des Dialoglayouts sowie zum transaktionalen Löschen der Daten über
 * entsprechende Service-Klassen.
 *
 * @see com.example.application.models.Gruppe
 * @see com.example.application.models.Gruppenarbeit
 * @see com.example.application.models.Veranstaltungstermin
 * @see com.example.application.services.GruppeService
 * @see com.example.application.services.GruppenarbeitService
 * @see com.example.application.services.VeranstaltungsterminService
 *
 * @author Lilli
 */
@SuppressWarnings("SpringTransactionalMethodCallsInspection")
public class VeranstaltungsterminLoeschenDialog extends Dialog {
    //Data
    private Veranstaltungstermin veranstaltungstermin;

    //UI-Elements
    private final H2 infoText = new H2("Empty");
    private final Button deleteBtn = new Button("Veranstaltungstermin endgültig löschen");
    private final Button cancelBtn = new Button("Abbrechen");
    private final Paragraph warningText = new Paragraph("Empty");
    private final Paragraph noReturn = new Paragraph("Empty");

    /**
     * Erzeugt einen neuen Dialog zur endgültigen Löschung eines Veranstaltungstermins.
     * Initialisiert die UI-Elemente und bindet die Funktionalitäten für die Buttons zum Löschen
     * und Abbrechen des Löschvorgangs.
     *
     * @param gruppeService              Der Service für Gruppen, der für die Datenmanipulation benötigt wird.
     * @param gruppenarbeitService       Der Service für Gruppenarbeiten, der für die Datenmanipulation benötigt wird.
     * @param veranstaltungsterminView   Die Ansicht, die aktualisiert werden soll, nachdem der Veranstaltungstermin
     *                                   gelöscht wurde.
     * @param aktiverVeranstaltungstermin Der aktive Veranstaltungstermin, der gelöscht werden soll.
     * @param aktiveGruppenarbeit        Die aktive Gruppenarbeit, die mit dem Veranstaltungstermin verbunden ist.
     * @param veranstaltungsterminService Der Service für Veranstaltungstermine, der für die Datenmanipulation benötigt
     *                                    wird.
     *
     * @author Lilli
     */
    public VeranstaltungsterminLoeschenDialog(GruppeService gruppeService, GruppenarbeitService gruppenarbeitService, VeranstaltungsterminView veranstaltungsterminView, Veranstaltungstermin aktiverVeranstaltungstermin, Gruppenarbeit aktiveGruppenarbeit, VeranstaltungsterminService veranstaltungsterminService) {
        this.veranstaltungstermin = null;
        styleElements();
        addButtonsFunctionalities(gruppeService, gruppenarbeitService, veranstaltungsterminView, aktiverVeranstaltungstermin, aktiveGruppenarbeit, veranstaltungsterminService);
        add(createLayout());
    }

    /**
     * Fügt den Buttons des Dialogs die entsprechenden Funktionalitäten hinzu.
     * Der Button zum Löschen des Veranstaltungstermins führt die Löschoperation aus,
     * während der Abbrechen-Button den Dialog schließt.
     *
     * @param gruppeService              Der Service für Gruppen, der für die Datenmanipulation benötigt wird.
     * @param gruppenarbeitService       Der Service für Gruppenarbeiten, der für die Datenmanipulation benötigt wird.
     * @param veranstaltungsterminView   Die Ansicht, die aktualisiert werden soll, nachdem der Veranstaltungstermin
     *                                  gelöscht wurde.
     * @param aktiverVeranstaltungstermin Der aktive Veranstaltungstermin, der gelöscht werden soll.
     * @param aktiveGruppenarbeit        Die aktive Gruppenarbeit, die mit dem Veranstaltungstermin verbunden ist.
     * @param veranstaltungsterminService Der Service für Veranstaltungstermine, der für die Datenmanipulation benötigt
     *                                   wird.
     *
     * @author Lilli
     */
    private void addButtonsFunctionalities(GruppeService gruppeService, GruppenarbeitService gruppenarbeitService, VeranstaltungsterminView veranstaltungsterminView, Veranstaltungstermin aktiverVeranstaltungstermin, Gruppenarbeit aktiveGruppenarbeit, VeranstaltungsterminService veranstaltungsterminService) {
        deleteBtn.addClickListener(event -> {
            if (aktiverVeranstaltungstermin != null) {
                veranstaltungsterminView.setAktiveKachelVeranstaltungstermin(aktiverVeranstaltungstermin);

                if (aktiveGruppenarbeit != null) {
                    veranstaltungsterminView.setAktiveKachelGruppenarbeit(aktiveGruppenarbeit);
                }
            }
            deleteEverything(gruppeService, gruppenarbeitService, veranstaltungsterminService);
            veranstaltungsterminView.update();
            close();
        });

        cancelBtn.addClickListener(event -> close());
    }

    /**
     * Stilisiert die UI-Elemente des Dialogs entsprechend den Designanforderungen.
     * Fügt CSS-Klassen hinzu und setzt spezifische CSS-Eigenschaften für die Warnungs- und Rückgabentext-Paragraphen.
     * Der Löschen-Button wird mit einem primären Theme-Varianten versehen, um ihn hervorzuheben.
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
     * Löscht den ausgewählten Veranstaltungstermin und alle damit verbundenen Daten aus der Datenbank.
     * Dies beinhaltet das Entfernen aller Gruppenarbeiten und deren zugehörigen Gruppen.
     *
     * @param gruppeService              Der Service für Gruppen, der für die Datenmanipulation benötigt wird.
     * @param gruppenarbeitService       Der Service für Gruppenarbeiten, der für die Datenmanipulation benötigt wird.
     * @param veranstaltungsterminService Der Service für Veranstaltungstermine, der für die Datenmanipulation benötigt
     *                                   wird.
     * @author Lilli
     */
    @Transactional
    protected void deleteEverything(GruppeService gruppeService, GruppenarbeitService gruppenarbeitService, VeranstaltungsterminService veranstaltungsterminService) {
        List<Gruppenarbeit> gruppenarbeiten = veranstaltungstermin.getGruppenarbeiten();
        veranstaltungstermin.removeAllGruppenarbeiten();
        veranstaltungsterminService.saveVeranstaltungstermin(veranstaltungstermin);

        for(Gruppenarbeit gruppenarbeit: gruppenarbeiten){
            List<Gruppe> gruppen = gruppenarbeit.getGruppen();
            gruppenarbeit.removeAllGruppen();
            gruppenarbeitService.save(gruppenarbeit);

            for (Gruppe gruppe : gruppen) {
                gruppeService.deleteGruppe(gruppe);
            }

            this.veranstaltungstermin.removeGruppenarbeit(gruppenarbeit);
            veranstaltungsterminService.saveVeranstaltungstermin(veranstaltungstermin);

            gruppenarbeitService.deleteGruppenarbeit(gruppenarbeit);
        }

        veranstaltungsterminService.deleteVeranstaltungstermin(veranstaltungstermin);
    }

    /**
     * Setzt den zu löschenden Veranstaltungstermin und aktualisiert die entsprechenden UI-Elemente
     * mit den Informationen des Veranstaltungstermins und den zugehörigen zu löschenden Daten.
     *
     * @param veranstaltungstermin Der Veranstaltungstermin, der gelöscht werden soll.
     *
     * @author Lilli
     */
    public void setVeranstaltungstermin(Veranstaltungstermin veranstaltungstermin) {
        this.veranstaltungstermin = veranstaltungstermin;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        infoText.setText("Veranstaltungstermin " + this.veranstaltungstermin.getTitel() + " am " +
                this.veranstaltungstermin.getDatum().format(dateFormatter) + " löschen");
        int anzGruppen = 0;
        for(Gruppenarbeit gruppenarbeit: this.veranstaltungstermin.getGruppenarbeiten()){
            anzGruppen+=gruppenarbeit.getGruppen().size();
        }
        warningText.setText("Wenn du den Veranstaltungstermin " +
                this.veranstaltungstermin.getTitel() + " am " +
                this.veranstaltungstermin.getDatum().format(dateFormatter) +
                " löscht,\n werden " + "auch alle zugehörigen Gruppenarbeiten (Anzahl: " +
                this.veranstaltungstermin.getGruppenarbeiten().size() + ") \nund die zu den Gruppenarbeiten" +
                " gehörenden Gruppen (Gesamtzahl: " + anzGruppen + ") gelöscht.");
        noReturn.setText("Bist du sicher, dass du den Veranstaltungstermin " + this.veranstaltungstermin.getTitel() +
                " am " + this.veranstaltungstermin.getDatum().format(dateFormatter) + " löschen willst?\n" +
                "Das kann nicht rückgängig gemacht werden!");
    }

    /**
     * Erstellt das Layout des Dialogs zur Löschung eines Veranstaltungstermins.
     * Das Layout enthält einen Titel für den zu löschenden Veranstaltungstermin,
     * Warnungstexte über die Konsequenzen der Löschung und Buttons zum Bestätigen oder Abbrechen.
     *
     * @return Ein {@link VerticalLayout} mit den UI-Elementen für den Löschdialog.
     *
     * @author Lilli
     */
    public VerticalLayout createLayout(){
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

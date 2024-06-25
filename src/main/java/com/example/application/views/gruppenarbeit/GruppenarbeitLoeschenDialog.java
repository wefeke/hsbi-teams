package com.example.application.views.gruppenarbeit;

import com.example.application.models.Gruppe;
import com.example.application.models.Gruppenarbeit;
import com.example.application.models.Veranstaltungstermin;
import com.example.application.services.GruppeService;
import com.example.application.services.GruppenarbeitService;
import com.example.application.services.VeranstaltungsterminService;
import com.example.application.views.veranstaltungstermin.VeranstaltungsterminView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Dialog-Klasse zur Verwaltung des Löschens einer Gruppenarbeit in einer Veranstaltungsterminansicht.
 * Diese Dialog-Klasse zeigt Informationen zur zu löschenden Gruppenarbeit an und bietet
 * Funktionalitäten zum endgültigen Löschen dieser und aller zugehörigen Daten.
 *
 * @author Lilli
 */
@SuppressWarnings("SpringTransactionalMethodCallsInspection")
public class GruppenarbeitLoeschenDialog extends Dialog {
    //Data
    private Gruppenarbeit gruppenarbeit;
    private Veranstaltungstermin veranstaltungstermin;
    private final VeranstaltungsterminView veranstaltungsterminView;

    //UI-Elemente
    private final H2 infoText = new H2("Empty");
    private final Paragraph warningText = new Paragraph("Empty");
    private final Paragraph noReturn = new Paragraph("Das kann nicht rückgängig gemacht werden!");
    private final Button deleteBtn = new Button("Gruppenarbeit endgültig löschen");
    private final Button cancelBtn = new Button("Abbrechen");

    /**
     * Erzeugt einen Dialog zur Verwaltung des Löschens einer Gruppenarbeit in einer Veranstaltungsterminansicht.
     * Der Dialog enthält eine Benutzeroberfläche mit Texten und Schaltflächen zum Bestätigen oder Abbrechen
     * des Löschvorgangs.
     *
     * @param gruppenarbeitService     Der Service für die Verwaltung von Gruppenarbeiten
     * @param gruppeService            Der Service für die Verwaltung von Gruppen
     * @param veranstaltungsterminService Der Service für die Verwaltung von Veranstaltungsterminen
     * @param veranstaltungsdetailView Die Ansicht des Veranstaltungstermins, in der der Dialog verwendet wird
     * @param aktiveGruppenarbeit      Die Gruppenarbeit, die gelöscht werden soll
     *
     * @author Lilli
     */
    public GruppenarbeitLoeschenDialog(GruppenarbeitService gruppenarbeitService, GruppeService gruppeService, VeranstaltungsterminService veranstaltungsterminService, VeranstaltungsterminView veranstaltungsdetailView, Gruppenarbeit aktiveGruppenarbeit) {
        this.veranstaltungsterminView = veranstaltungsdetailView;
        this.gruppenarbeit = null;
        this.veranstaltungstermin = null;
        styleElements();
        addButtonFunctionalities(gruppenarbeitService, gruppeService, veranstaltungsterminService, aktiveGruppenarbeit);
        add(createLayout());
    }

    /**
     * Setzt die Funktionalitäten der Buttons in diesem Dialog.
     * <p>
     * Der "Löschen"-Button führt den Löschvorgang für die aktuelle Gruppenarbeit durch und aktualisiert
     * anschließend die Ansicht des Veranstaltungstermins.
     * <p>
     * Der "Abbrechen"-Button schließt den Dialog, ohne weitere Aktionen auszuführen.
     *
     * @param gruppenarbeitService     Der Service für die Verwaltung von Gruppenarbeiten
     * @param gruppeService            Der Service für die Verwaltung von Gruppen
     * @param veranstaltungsterminService Der Service für die Verwaltung von Veranstaltungsterminen
     * @param aktiveGruppenarbeit      Die aktuell ausgewählte Gruppenarbeit
     *
     * @author Lilli
     */
    private void addButtonFunctionalities(GruppenarbeitService gruppenarbeitService, GruppeService gruppeService, VeranstaltungsterminService veranstaltungsterminService, Gruppenarbeit aktiveGruppenarbeit) {
        deleteBtn.addClickListener(event -> {
            deleteEverything(gruppenarbeitService, gruppeService, veranstaltungsterminService, aktiveGruppenarbeit);
            close();
            veranstaltungsterminView.update();
        });
        cancelBtn.addClickListener(event -> close());
    }

    /**
     * Stylt die UI-Elemente dieses Dialogs entsprechend.
     * <p>
     * Der Warnungstext wird formatiert und die Stileigenschaften für die Anzeige angepasst.
     * Der "Löschen"-Button erhält das Primär-Theme für die visuelle Hervorhebung.
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
     * Führt den Löschvorgang für die aktuelle Gruppenarbeit und alle zugehörigen Daten durch.
     * <p>
     * Diese Methode entfernt die Gruppenarbeit aus allen zugehörigen Gruppen, löscht diese Gruppen
     * und aktualisiert den Veranstaltungstermin entsprechend.
     *
     * @param gruppenarbeitService     Der Service für die Verwaltung von Gruppenarbeiten
     * @param gruppeService            Der Service für die Verwaltung von Gruppen
     * @param veranstaltungsterminService Der Service für die Verwaltung von Veranstaltungsterminen
     * @param aktiveGruppenarbeit      Die aktuell ausgewählte Gruppenarbeit
     *
     * @author Lilli
     */
    @Transactional
    protected void deleteEverything(GruppenarbeitService gruppenarbeitService, GruppeService gruppeService, VeranstaltungsterminService veranstaltungsterminService, Gruppenarbeit aktiveGruppenarbeit) {
        List<Gruppe> gruppen = gruppenarbeit.getGruppen();
        gruppenarbeit.removeAllGruppen();
        gruppenarbeitService.save(gruppenarbeit);

        for (Gruppe gruppe : gruppen) {
            gruppeService.deleteGruppe(gruppe);
        }
        this.veranstaltungstermin.removeGruppenarbeit(gruppenarbeit);
        veranstaltungsterminService.saveVeranstaltungstermin(veranstaltungstermin);

        if (veranstaltungstermin != null) {
            veranstaltungsterminView.setAktiveKachelVeranstaltungstermin(veranstaltungstermin);

            if (aktiveGruppenarbeit != gruppenarbeit && aktiveGruppenarbeit != null) {
                veranstaltungsterminView.setAktiveKachelGruppenarbeit(aktiveGruppenarbeit);
            }
        }

        gruppenarbeitService.deleteGruppenarbeit(gruppenarbeit);
    }

    /**
     * Setzt die Gruppenarbeit, die gelöscht werden soll, und aktualisiert die entsprechenden Texte im Dialog.
     *
     * @param gruppenarbeit Die Gruppenarbeit, die gelöscht werden soll
     *
     * @author Lilli
     */
    public void setGruppenarbeit(Gruppenarbeit gruppenarbeit) {
        this.gruppenarbeit = gruppenarbeit;
        infoText.setText("Gruppenarbeit " + this.gruppenarbeit.getTitel() + " löschen");
        warningText.setText("Wenn du die Gruppenarbeit " + this.gruppenarbeit.getTitel() + " löscht,\n werden " +
                "auch alle zugehörigen Gruppen (Anzahl: " + this.gruppenarbeit.getGruppen().size() + ") gelöscht.");

        noReturn.setText("Bist du sicher, dass du die Gruppenarbeit " + this.gruppenarbeit.getTitel() + " löschen " +
                "willst?\nDas kann nicht rückgängig gemacht werden!");
    }

    /**
     * Setzt den Veranstaltungstermin für die aktuelle Gruppenarbeit.
     *
     * @param veranstaltungstermin Der Veranstaltungstermin, der mit der Gruppenarbeit verbunden ist
     *
     * @author Lilli
     */
    public void setVeranstaltungstermin(Veranstaltungstermin veranstaltungstermin) {
        this.veranstaltungstermin = veranstaltungstermin;
    }

    /**
     * Erzeugt und liefert das Layout für den Dialog zur Löschung einer Gruppenarbeit.
     * <p>
     * Das Layout enthält Texte und Buttons zum Bestätigen oder Abbrechen des Löschvorgangs.
     *
     * @return Das VerticalLayout mit den UI-Elementen für den Löschdialog
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

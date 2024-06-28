package com.example.application.views.veranstaltungstermin;


import com.example.application.models.Teilnehmer;
import com.example.application.models.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.TeilnehmerService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import jakarta.annotation.security.RolesAllowed;

import java.util.Optional;

@RolesAllowed({"ADMIN", "USER"})
public class TeilnehmerErstellenDialog extends Dialog {

    private final TeilnehmerService teilnehmerService;
    private final AuthenticatedUser authenticatedUser;
    private final TeilnehmerHinzufuegenDialog teilnehmerHinzufuegenDialog;
    private Teilnehmer teilnehmer;

    TextField firstName = new TextField("Vorname");
    TextField lastName = new TextField("Nachname");
    NumberField matrikelNr = new NumberField("Matrikelnummer");
    Button save = new Button("Speichern");
    Button cancel = new Button("Abbrechen");
    Binder<Teilnehmer> binder = new Binder<>(Teilnehmer.class);


    /**
     * Konstruktor für die Klasse TeilnehmerErstellenDialog.
     * Dieser Konstruktor initialisiert den TeilnehmerService, den authentifizierten Benutzer und den Dialog zum Hinzufügen von Teilnehmern.
     * Es setzt den Titel des Dialogs, fügt das Layout hinzu und konfiguriert die Schaltflächen und Felder.
     *
     * @param teilnehmerService Der Service zur Handhabung von Teilnehmern.
     * @param authenticatedUser Der aktuell authentifizierte Benutzer.
     * @param teilnehmerHinzufuegenDialog Der Dialog zum Hinzufügen von Teilnehmern.
     * @author Tobias
     */
    public TeilnehmerErstellenDialog(TeilnehmerService teilnehmerService, AuthenticatedUser authenticatedUser,TeilnehmerHinzufuegenDialog teilnehmerHinzufuegenDialog){
        this.teilnehmerService = teilnehmerService;
        this.authenticatedUser = authenticatedUser;
        this.teilnehmerHinzufuegenDialog = teilnehmerHinzufuegenDialog;


        setHeaderTitle("Studierenden hinzufügen");
        add(createLayout());
        getFooter().add(cancel, save);


        configureButtons();
        bindFields();
    }

    /**
     * Erstellt das Layout für den Dialog zur Erstellung eines Teilnehmers.
     * Dieses Layout enthält Felder für den Vornamen, Nachnamen und die Matrikelnummer des Teilnehmers.
     * Der "Speichern"-Button wird als primärer Button markiert und ein Tastenkürzel für die Eingabetaste wird hinzugefügt.
     *
     * @return Das erstellte FormLayout, das dem Dialog hinzugefügt werden soll.
     * @author Tobias
     */
    private FormLayout createLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);
        FormLayout formLayout = new FormLayout();
        formLayout.add(firstName, lastName, matrikelNr);
        return formLayout;
    }

    /**
     * Konfiguriert die Aktionen der Schaltflächen "Speichern" und "Abbrechen".
     * Wenn der "Speichern"-Button geklickt wird, wird überprüft, ob die Eingabe gültig ist und ob die Matrikelnummer bereits existiert.
     * Wenn die Eingabe ungültig ist, wird eine Benachrichtigung angezeigt, die den Benutzer auffordert, alle Felder auszufüllen.
     * Wenn die Matrikelnummer bereits existiert, wird eine Benachrichtigung angezeigt, die den Benutzer darüber informiert.
     * Wenn die Eingabe gültig ist und die Matrikelnummer nicht existiert, wird der Teilnehmer gespeichert.
     * Wenn der "Abbrechen"-Button geklickt wird, wird der Dialog geschlossen.
     *
     * @author Tobias
     */
    private void configureButtons() {
        save.addClickListener(event -> {
            if (isValidInput()) {
                if (isDuplicateMatrikelNr()) {
                    Notification.show("Matrikelnummer existiert bereits", 3000, Notification.Position.MIDDLE);
                } else {
                    saveTeilnehmer();
                }
            } else {
                Notification.show("Bitte füllen Sie alle Felder aus", 3000, Notification.Position.MIDDLE);
            }
        });
        cancel.addClickListener(event -> close());
    }

    /**
     * Überprüft, ob die Eingaben für Vorname, Nachname und Matrikelnummer gültig sind.
     * Gültige Eingaben sind nicht-leere Strings für Vorname und Nachname und eine nicht-null Wert für Matrikelnummer.
     *
     * @return true, wenn alle Eingaben gültig sind, sonst false.
     * @author Tobias
     */
    private boolean isValidInput() {
        return !firstName.isEmpty() && !lastName.isEmpty() && matrikelNr.getValue() != null;
    }

    /**
     * Überprüft, ob die Matrikelnummer bereits existiert.
     * Die Methode holt die Matrikelnummer und den authentifizierten Benutzer und überprüft, ob ein Teilnehmer mit der gleichen Matrikelnummer und Benutzer-ID bereits existiert.
     * Wenn der Teilnehmer bereits existiert und nicht der aktuell bearbeitete Teilnehmer ist, wird true zurückgegeben, sonst false.
     *
     * @return true, wenn die Matrikelnummer bereits existiert und nicht dem aktuell bearbeiteten Teilnehmer gehört, sonst false.
     * @author Tobias
     */
    private boolean isDuplicateMatrikelNr() {
        Long matrikelNrValue = matrikelNr.getValue().longValue();
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            Long userId = maybeUser.get().getId();
            Optional<Teilnehmer> existingTeilnehmer = teilnehmerService.findByMatrikelNrAndUserId(matrikelNrValue, userId);
            return existingTeilnehmer.isPresent() && (teilnehmer == null || !existingTeilnehmer.get().getId().equals(teilnehmer.getId()));
        }
        return false;
    }

    /**
     * Speichert einen neuen Teilnehmer, wenn die Matrikelnummer gültig ist und der Teilnehmer valide ist.
     * Die Methode erstellt einen neuen Teilnehmer und überprüft, ob die Matrikelnummer genau 7 Zahlen enthält.
     * Wenn die Matrikelnummer gültig ist und der Teilnehmer valide ist, wird der Teilnehmer gespeichert, das Grid aktualisiert, die Felder geleert und der Dialog geschlossen.
     * Wenn der Teilnehmer nicht gespeichert werden kann, wird eine Benachrichtigung angezeigt.
     *
     * @author Tobias
     */
    private void saveTeilnehmer() {
        Teilnehmer teilnehmer = new Teilnehmer();

        if (matrikelNr.getValue() != null && String.valueOf(matrikelNr.getValue().longValue()).matches("\\d{7}")) {
            if (binder.writeBeanIfValid(teilnehmer)) {
                Optional<User> maybeUser = authenticatedUser.get();
                if (maybeUser.isPresent()) {
                    User user = maybeUser.get();
                    teilnehmerService.saveTeilnehmer(teilnehmer, user);
                    teilnehmerHinzufuegenDialog.updateGrid();
                    clearFields();
                    close();
                    Notification.show("Teilnehmer wurde angelegt");
                } else {
                    Notification.show("Fehler beim Speichern");
                }
            }
        }
    }

    /**
     * Bindet die Felder Vorname, Nachname und Matrikelnummer an den Binder.
     * Für jedes Feld wird eine Validierung hinzugefügt:
     * - Der Vorname muss gefüllt sein und darf maximal 255 Zeichen lang sein.
     * - Der Nachname muss gefüllt sein und darf maximal 255 Zeichen lang sein.
     * - Die Matrikelnummer muss gefüllt sein und genau 7 Zahlen enthalten.
     * Die Felder werden an die entsprechenden Getter- und Setter-Methoden des Teilnehmer-Objekts gebunden.
     * Für das Feld Matrikelnummer wird ein Konverter hinzugefügt, um den Wert von Double in Long umzuwandeln und umgekehrt.
     *
     * @author Tobias
     */
    private void bindFields() {
        binder.forField(firstName)
                .asRequired("Vorname muss gefüllt sein")
                .withValidator(vorname -> vorname.length() <= 255, "Der Vorname darf maximal 255 Zeichen lang sein")
                .bind(Teilnehmer::getVorname, Teilnehmer::setVorname);

        binder.forField(lastName)
                .asRequired("Nachname muss gefüllt sein")
                .withValidator(nachname -> nachname.length() <= 255, "Der Nachname darf maximal 255 Zeichen lang sein")
                .bind(Teilnehmer::getNachname, Teilnehmer::setNachname);

        binder.forField(matrikelNr)
                .asRequired("Matrikelnummer muss gefüllt sein")
                .withValidator(matrikelNr -> String.valueOf(matrikelNr.longValue()).matches("\\d{7}"), "Matrikelnummer muss genau 7 Zahlen enthalten")
                .withConverter(Double::longValue, Long::doubleValue)
                .bind(Teilnehmer::getId, Teilnehmer::setId);
    }

    /**
     * Leert die Felder für Vorname, Nachname und Matrikelnummer.
     * Nach dem Aufruf dieser Methode sind alle genannten Felder leer.
     *
     * @author Tobias
     */
    private void clearFields() {
        firstName.clear();
        lastName.clear();
        matrikelNr.clear();
    }

    /**
     * Setzt den Teilnehmer für diesen Dialog.
     * Diese Methode wird verwendet, um den Teilnehmer, der in diesem Dialog bearbeitet wird, festzulegen.
     *
     * @param teilnehmer der Teilnehmer, der für diesen Dialog festgelegt werden soll
     * @author Tobias
     */
    public void setTeilnehmer(Teilnehmer teilnehmer) {
        this.teilnehmer = teilnehmer;
    }
}

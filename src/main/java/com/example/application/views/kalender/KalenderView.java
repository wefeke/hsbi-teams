package com.example.application.views.kalender;

import com.example.application.models.*;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.*;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.stefan.fullcalendar.CalendarViewImpl;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

// LEON
@Route(value = "kalender", layout = MainLayout.class)
@PageTitle("Kalender")
@RolesAllowed({"ADMIN", "USER"})
public class KalenderView extends VerticalLayout {
    private final VeranstaltungsterminService veranstaltungsterminService;
    // Kalender
    FullCalendar fullCalendar;
    // Services
    VeranstaltungenService veranstaltungenService;
    TeilnehmerService teilnehmerService;
    GruppenarbeitService gruppenarbeitService;
    GruppenarbeitTeilnehmerService gruppenarbeitTeilnehmerService;
    SuperService superService;
    UserService userService;
    // Fields
    AuthenticatedUser authenticatedUser;
    Optional<User> maybeUser;
    User user;
    // Ranges
    LocalDate currentDate = getCurrentDate();
    // Buttons
    Button zuvor = new Button("Zuvor");
    Button naechste = new Button("Nächste");
    // Auswahl der Ansichten
    ComboBox<KalenderAuswahl> kalenderAuswahlComboBox = new ComboBox<>();

    /**
     * Konstruktor für die KalenderView Klasse.
     * Initialisiert die Services und die benötigten Felder, erstellt das Layout und konfiguriert die Elemente.
     *
     * @param veranstaltungenService der Service für die Veranstaltungen
     * @param teilnehmerService der Service für die Teilnehmer
     * @param gruppenarbeitService der Service für die Gruppenarbeiten
     * @param gruppenarbeitTeilnehmerService der Service für die Gruppenarbeitsteilnehmer
     * @param userService der Service für die Benutzer
     * @param authenticatedUser der aktuell authentifizierte Benutzer
     * @param superService der Service für übergeordnete Funktionen
     * @param veranstaltungsterminService der Service für die Veranstaltungstermine
     *
     * @author Leon
     */
    public KalenderView(
            VeranstaltungenService veranstaltungenService,
            TeilnehmerService teilnehmerService,
            GruppenarbeitService gruppenarbeitService,
            GruppenarbeitTeilnehmerService gruppenarbeitTeilnehmerService,
            UserService userService,
            AuthenticatedUser authenticatedUser,
            SuperService superService, VeranstaltungsterminService veranstaltungsterminService) {
        this.veranstaltungenService = veranstaltungenService;
        this.teilnehmerService = teilnehmerService;
        this.gruppenarbeitService = gruppenarbeitService;
        this.gruppenarbeitTeilnehmerService = gruppenarbeitTeilnehmerService;
        this.userService = userService;
        this.authenticatedUser = authenticatedUser;
        this.superService = superService;
        this.veranstaltungsterminService = veranstaltungsterminService;
        addClassName("kalender-view");
        maybeUser = authenticatedUser.get();
        user = validateUser(maybeUser);
        fullCalendar = getCalendar();
        configureButtons();
        configureKalenderAuswahl();
        add(getToolbar(), fullCalendar);
        updateCalendar();
        setSizeFull();
    }

    /**
     * Aktualisiert den Kalender mit den Veranstaltungsterminen.
     * Erstellt für jeden Termin einen Kalendereintrag und fügt ihn zum Kalender hinzu.
     *
     * @author Leon
     */
    private void updateCalendar() {
        List<Veranstaltungstermin> veranstaltungstermine = veranstaltungsterminService.findAllVeranstaltungstermine(user);
        for (Veranstaltungstermin veranstaltungstermin : veranstaltungstermine) {
            Entry entry = new Entry();
            entry.setTitle(veranstaltungstermin.getTitel());
            entry.setColor(randomColor());
            entry.setConstraint(veranstaltungstermin.getVeranstaltung().getId().toString());
            if (veranstaltungstermin.getStartZeit() == null) {
                entry.setStart(dateTimeConverter(veranstaltungstermin.getDatum()));
                entry.setEnd(entry.getStart().plusHours(23));
            } else {
                entry.setStart(dateTimeConverter(veranstaltungstermin.getDatum(), veranstaltungstermin.getStartZeit()));
                entry.setEnd(entry.getStart().plusHours(veranstaltungstermin.getEndZeit().getHour()));
            }
            fullCalendar.getEntryProvider().asInMemory().addEntries(entry);
        }
    }

    /**
     * Gibt das aktuelle Datum zurück.
     *
     * @return das aktuelle Datum
     *
     * @author Leon
     */
    private LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    /**
     * Konfiguriert die Auswahlmöglichkeiten im Kalender.
     * Setzt die verfügbaren Optionen und definiert das Verhalten bei Auswahländerungen.
     *
     * @author Leon
     */
    private void configureKalenderAuswahl() {
        kalenderAuswahlComboBox.setItems(KalenderAuswahl.getAllWerte());
        kalenderAuswahlComboBox.setItemLabelGenerator(KalenderAuswahl::toString);
        kalenderAuswahlComboBox.addValueChangeListener(event -> validateKalenderAuswahl());
    }

    /**
     * Konvertiert ein LocalDate und LocalTime in ein LocalDateTime.
     *
     * @param localDate das lokale Datum
     * @param localTime die lokale Zeit
     * @return die kombinierte LocalDateTime
     *
     * @author Leon
     */
    private LocalDateTime dateTimeConverter(LocalDate localDate, LocalTime localTime) {
        return localDate.atTime(localTime);
    }

    /**
     * Konvertiert ein LocalDate in ein LocalDateTime.
     *
     * @param localDate das lokale Datum
     * @return die kombinierte LocalDateTime
     *
     * @author Leon
     */
    private LocalDateTime dateTimeConverter(LocalDate localDate) {
        return localDate.atStartOfDay();
    }

    /**
     * Generiert eine zufällige Farbe im Hexadezimalformat.
     *
     * @return eine zufällige Farbe als String
     *
     * @author Leon
     */
    private String randomColor() {
        Random rand = new Random();
        int myRandomNumber = rand.nextInt(0xFFFFFF + 1);
        return String.format("#%06x", myRandomNumber);
    }

    /**
     * Erstellt und konfiguriert den Kalender.
     * Setzt die Größe des Kalenders und definiert das Verhalten bei Klicks auf Einträge.
     *
     * @return der konfigurierte FullCalendar
     *
     * @author Leon
     */
    private FullCalendar getCalendar() {
        FullCalendar calendar = FullCalendarBuilder.create().build();
        calendar.setLocale(Locale.GERMAN);
        calendar.addEntryClickedListener((event) -> {
            Entry entry = event.getEntry();
            long veranstaltungId = Long.parseLong(entry.getConstraint());
            getUI().ifPresent(ui -> ui.navigate("veranstaltung-detail/" + veranstaltungId));
        });

        calendar.setSizeFull();
        return calendar;
    }

    /**
     * Ändert das Datum basierend auf der aktuellen Auswahl im Kalender.
     * Inkrementiert oder dekrementiert das Datum, abhängig von der übergebenen Konfiguration.
     *
     * @param increment gibt an, ob das Datum inkrementiert oder dekrementiert werden soll
     *
     * @author Leon
     */
    private void changeDateByConfiguration(boolean increment) {
        if (increment) {
            if (kalenderAuswahlComboBox.getValue() == null) {
                fullCalendar.setValidRange(
                        currentDate = getStartOfMonth(currentDate.plusMonths(1)),
                        currentDate.plusMonths(1)
                );

            } else if (kalenderAuswahlComboBox.getValue().istMonat()) {
                fullCalendar.setValidRange(
                        currentDate = getStartOfMonth(currentDate.plusMonths(1)),
                        currentDate.plusMonths(1)
                );
            } else if (kalenderAuswahlComboBox.getValue().istTag()) {
                fullCalendar.setValidRange(
                        currentDate = currentDate.plusDays(1),
                        currentDate.plusDays(2)
                );
            } else if (kalenderAuswahlComboBox.getValue().istWoche()) {
                fullCalendar.setValidRange(
                        currentDate = getStartOfWeek(currentDate.plusWeeks(1)),
                        currentDate.plusWeeks(1)
                );
            }
        } else {
            if (kalenderAuswahlComboBox.getValue() == null) {
                fullCalendar.setValidRange(
                        currentDate = getStartOfMonth(currentDate.minusMonths(1)),
                        currentDate.plusMonths(1)
                );
                currentDate = getStartOfWeek(currentDate.minusMonths(1));
            } else if (kalenderAuswahlComboBox.getValue().istMonat()) {
                fullCalendar.setValidRange(
                        currentDate = getStartOfMonth(currentDate.minusMonths(1)),
                        currentDate.plusMonths(1)
                );
            } else if (kalenderAuswahlComboBox.getValue().istTag()) {
                fullCalendar.setValidRange(
                        currentDate = currentDate.minusDays(1), // Ein Tag wird abgezogen
                        currentDate.plusDays(1) // Eine Tag wird zum vorherigen Datum hinzugefügt
                );

            } else if (kalenderAuswahlComboBox.getValue().istWoche()) {
                fullCalendar.setValidRange(
                        currentDate = getStartOfWeek(currentDate.minusWeeks(1)), // Eine Woche wird abgezogen und sichergestellt, dass es der Montag ist
                        getStartOfWeek(currentDate.plusDays(6)) // Eine Woche wird zur vorherigen Operation hinzugefügt
                );
            }
        }
    }

    //

    /**
     * Konfiguriert die Schaltflächen für die Navigation im Kalender.
     * Definiert das Verhalten bei Klicks auf die "Zuvor" und "Nächste" Schaltflächen.
     *
     * @author Leon
     */
    private void configureButtons() {
        naechste.addClickListener(event -> changeDateByConfiguration(true));

        zuvor.addClickListener(event -> changeDateByConfiguration(false));
    }

    /**
     * Validiert den authentifizierten Benutzer.
     * Wenn der Benutzer vorhanden ist, wird er zurückgegeben. Andernfalls wird ein neuer Benutzer erstellt und zurückgegeben.
     *
     * @param maybeUser der optionale authentifizierte Benutzer
     * @return der validierte Benutzer
     *
     * @author Leon
     */
    private User validateUser(Optional<User> maybeUser) {
        return maybeUser.orElseGet(User::new);
    }

    /**
     * Erstellt die Toolbar für die Kalenderansicht.
     * Die Toolbar enthält die Überschrift, Navigationsschaltflächen und die Kalenderauswahl.
     *
     * @return das erstellte HorizontalLayout für die Toolbar
     *
     * @author Leon
     */
    private HorizontalLayout getToolbar() {
        H1 username = new H1("Kalender für " + user.getName());
        username.getStyle().set("font-size", "28px");

        if (maybeUser.isPresent()) {
            getUI().ifPresent(ui -> ui.navigate("login"));
        }

        var toolbar = new HorizontalLayout(username, zuvor, naechste, kalenderAuswahlComboBox);
        toolbar.setAlignItems(Alignment.CENTER);
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);
        toolbar.setWidthFull();
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    /**
     * Validiert die Auswahl im Kalender und ändert die Ansicht entsprechend.
     * Passt die Kalenderansicht basierend auf der Auswahl an (Tag, Woche, Monat).
     *
     * @author Leon
     */
    public void validateKalenderAuswahl() {
        if (kalenderAuswahlComboBox.getValue() == null || kalenderAuswahlComboBox.getValue().istMonat()) { // Die neuen Grenzen bilden nur einen Rahmen, daher kann hier auch
            // mit zwei Monaten gerechnet werden
            fullCalendar.setValidRange(getStartOfMonth(currentDate).minusWeeks(1), getStartOfMonth(currentDate).plusMonths(2));
            fullCalendar.changeView(CalendarViewImpl.DAY_GRID_MONTH);
        } else if (kalenderAuswahlComboBox.getValue().istTag()) {
            naechste.click();
            zuvor.click();
            fullCalendar.changeView(CalendarViewImpl.TIME_GRID_DAY);
        } else if (kalenderAuswahlComboBox.getValue().istWoche()) { // Beim Ändern auf die Wochenansicht passiert es, dass
            // bei Tag-Ansicht die Valid Range zu klein für eine ganze Woche ist, also kann sie auch einfach auf zwei Wochen gesetzt werden
            fullCalendar.setValidRange(getStartOfWeek(currentDate).minusWeeks(1), getStartOfWeek(currentDate).plusWeeks(1));
            fullCalendar.changeView(CalendarViewImpl.TIME_GRID_WEEK);
        }
        fullCalendar.setSizeFull();
    }

    private LocalDate getStartOfWeek(LocalDate localDate) {
        switch(localDate.getDayOfWeek()) {
            case DayOfWeek.MONDAY:
                return localDate;
            case DayOfWeek.TUESDAY:
                getStartOfWeek(localDate.minusDays(1));
            case DayOfWeek.WEDNESDAY:
                getStartOfWeek(localDate.minusDays(2));
            case DayOfWeek.THURSDAY:
                getStartOfWeek(localDate.minusDays(3));
            case DayOfWeek.FRIDAY:
                getStartOfWeek(localDate.minusDays(4));
            case DayOfWeek.SATURDAY:
                getStartOfWeek(localDate.minusDays(5));
            case DayOfWeek.SUNDAY:
                getStartOfWeek(localDate.minusDays(6));
            default:
                return localDate;
        }
    }


    private LocalDate getStartOfMonth(LocalDate localDate) {

        if (localDate.getDayOfMonth() == 1) {
            return localDate;
        } else {
            return getStartOfMonth(localDate.minusDays(1));
        }
    }

}

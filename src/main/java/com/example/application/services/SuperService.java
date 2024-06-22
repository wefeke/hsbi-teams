package com.example.application.services;

import com.example.application.models.*;
import com.example.application.security.AuthenticatedUser;
import com.example.application.views.auswertung.Auswertung;
import com.example.application.views.auswertung.TGGPHelper;
import org.springframework.stereotype.Service;

import java.io.Console;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SuperService {

    private final GruppeService gruppeService;
    VeranstaltungenService veranstaltungenService;
    TeilnehmerService teilnehmerService;
    GruppenarbeitService gruppenarbeitService;
    GruppenarbeitTeilnehmerService gruppenarbeitTeilnehmerService;
    UserService userService; AuthenticatedUser authenticatedUser;

    Optional<User> maybeUser;
    User user;
    public SuperService(VeranstaltungenService veranstaltungenService,
                        TeilnehmerService teilnehmerService,
                        GruppenarbeitService gruppenarbeitService,
                        GruppenarbeitTeilnehmerService gruppenarbeitTeilnehmerService,
                        UserService userService, AuthenticatedUser authenticatedUser, GruppeService gruppeService) {
        this.veranstaltungenService = veranstaltungenService;
        this.teilnehmerService = teilnehmerService;
        this.gruppenarbeitService = gruppenarbeitService;
        this.gruppenarbeitTeilnehmerService = gruppenarbeitTeilnehmerService;
        this.userService = userService;
        this.authenticatedUser = authenticatedUser;
        this.gruppeService = gruppeService;
    }

    private User validateUser(Optional<User> maybeUser) {
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            return user;
        } else {
            return new User();
        }
    }

    public List<Auswertung> findAllAuswertungenByVeranstaltung(Long id) {
        maybeUser = authenticatedUser.get();
        user = validateUser(maybeUser);
        Veranstaltung veranstaltung = veranstaltungenService.findVeranstaltungById(id, user);
        List<Teilnehmer> teilnehmern = teilnehmerService.findTeilnehmerByVeranstaltungId(id);

        // Jeder Veranstaltungstermin kann eine Gruppenarbeit haben und dann auch bewertet werden
        List<Veranstaltungstermin> veranstaltungstermine = veranstaltung.getVeranstaltungstermine();
        List<Gruppenarbeit> gruppenarbeiten = new ArrayList<>();
        // Damit die tggHelperListe bei jedem Teilnehmer gleich lang ist, muss sie jede Gruppenarbeit beinhalten

        List<TGGPHelper> tggpHelperList = new ArrayList<>();

        // Aufgrund der Randbedingung, dass aus die Liste nicht leer sein darf, beim Herausschreiben muss mindestens ein Element
        // noch vorherhinzugefügt werden
        TGGPHelper tggpHelperLastElement = new TGGPHelper();
        /*
        Veranstaltungstermin veranstaltungsterminLastElement = new Veranstaltungstermin(LocalDate.now(), LocalTime.now(), LocalTime.now(), new String("ort"), new String("titel"), new User(), new Veranstaltung());
        Gruppenarbeit gruppenarbeitLastElement = new Gruppenarbeit(new String("beschreibung"), new String("titel"), new User(), new Veranstaltungstermin());
        Gruppe gruppeLastElement = new Gruppe();
        tggpHelperLastElement.setVeranstaltungtermin(veranstaltungsterminLastElement);
        tggpHelperLastElement.setGruppenarbeit(gruppenarbeitLastElement);
        tggpHelperLastElement.setGruppe(gruppeLastElement);
        tggpHelperList.add(tggpHelperLastElement);
         */
        for (Veranstaltungstermin veranstaltungstermin : veranstaltungstermine) {
            for (Gruppenarbeit gruppenarbeit : veranstaltungstermin.getGruppenarbeiten()) {
                TGGPHelper tggpHelper = new TGGPHelper();
                tggpHelper.setVeranstaltungtermin(veranstaltungstermin);
                tggpHelper.setGruppenarbeit(gruppenarbeit);
                tggpHelperList.add(tggpHelper);
            }
        }

        List<Auswertung> auswertungen = new ArrayList<Auswertung>();
        // Für jeden Teilnehmer aus der Veranstaltung soll eine Auswertung angelegt werden
        for ( Teilnehmer teilnehmer : teilnehmern) {

            Auswertung auswertung = new Auswertung();
            // Name und Matrikelnummer werden festgelegt
            auswertung.setNameMatrikelnummer(teilnehmer.getVorname() + " " + teilnehmer.getNachname() + "("+teilnehmer.getId()+")");


            // Auf Basis der tggHelperList, welche alle Veranstaltungstermine, die eine Gruppenarbeit haben, sollen nun die Werte (Gruppe) für den
            // Teilnehmer hinzufügen
            for (TGGPHelper tggpHelper : tggpHelperList) {
                if (tggpHelper.getGruppenarbeit() != null) {
                    List<Gruppe> gruppen = tggpHelper.getGruppenarbeit().getGruppen();
                    for (Gruppe gruppe : gruppen) {
                        // if (tggpHelper.getGruppenarbeit().equals(gruppe.getGruppenarbeit())) {
                        for (Teilnehmer teilnehmerinGruppe : gruppe.getTeilnehmer()) {
                            if (teilnehmerinGruppe.equals(teilnehmer)) {
                                tggpHelper.addGruppe(gruppe);
                                auswertung.addGruppe(gruppe);

                            }
                        }

                    }
                }
                auswertung.addToGesamtPunkte(gruppenarbeitTeilnehmerService.
                        findPunkteByMatrikelNrAndGruppenarbeitId(
                                teilnehmer.getId(), tggpHelper.getGruppenarbeit().getId()
                        ));
            }
           auswertung.setTggpHelper(tggpHelperList);
            auswertungen.add(auswertung);
            }
        
        return auswertungen;
    }






}

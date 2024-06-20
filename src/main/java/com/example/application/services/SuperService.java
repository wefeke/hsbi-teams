package com.example.application.services;

import com.example.application.models.*;
import com.example.application.security.AuthenticatedUser;
import com.example.application.views.auswertung.Auswertung;
import com.example.application.views.auswertung.TGGPHelper;
import org.springframework.stereotype.Service;

import java.io.Console;
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
        for (Veranstaltungstermin veranstaltungstermin : veranstaltungstermine) {
            for (Gruppenarbeit gruppenarbeit : veranstaltungstermin.getGruppenarbeiten()) {
                TGGPHelper tggpHelper = new TGGPHelper();
                tggpHelper.setVeranstaltungtermin(veranstaltungstermin);
                tggpHelper.setGruppenarbeit(gruppenarbeit);
                tggpHelperList.add(tggpHelper);
            }
        }

        List<Auswertung> auswertungen = new ArrayList<Auswertung>();
        for ( Teilnehmer teilnehmer : teilnehmern) {
            Auswertung auswertung = new Auswertung();
            auswertung.setName(teilnehmer.getVorname() + " " + teilnehmer.getNachname());
            auswertung.setMatrikelnummer(teilnehmer.getId());
            // Punkte suchen für Teilnehmer
            // Clonen der Liste für den jeweiligen Teilnehmer
            //List<TGGPHelper> tggpHelperListSpecified = new ArrayList<TGGPHelper>(tggpHelperList);
            for (TGGPHelper tggpHelper : tggpHelperList) {
                if (tggpHelper.getGruppenarbeit() != null) {
                    List<Gruppe> gruppen = tggpHelper.getGruppenarbeit().getGruppen();
                    for (Gruppe gruppe : gruppen) {
                        for (Teilnehmer teilnehmerinGruppe : gruppe.getTeilnehmer()) {
                            if (Objects.equals(teilnehmerinGruppe.getId(), teilnehmer.getId())) {
                                tggpHelper.setGruppe(gruppe);
                            }
                        }
                    }
                }
                tggpHelper.setPunkte(
                        gruppenarbeitTeilnehmerService.
                                findPunkteByMatrikelNrAndGruppenarbeitId(
                                        teilnehmer.getId(), tggpHelper.getGruppenarbeit().getId()
                                ));
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

package com.example.application.services;

import com.example.application.models.*;
import com.example.application.security.AuthenticatedUser;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.data.provider.ListDataProvider;
import org.aspectj.weaver.loadtime.Aj;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class SuperService {

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
                        UserService userService, AuthenticatedUser authenticatedUser) {
        this.veranstaltungenService = veranstaltungenService;
        this.teilnehmerService = teilnehmerService;
        this.gruppenarbeitService = gruppenarbeitService;
        this.gruppenarbeitTeilnehmerService = gruppenarbeitTeilnehmerService;
        this.userService = userService;
        this.authenticatedUser = authenticatedUser;

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

        List<Auswertung> auswertungen = new ArrayList<Auswertung>();
        for ( Teilnehmer teilnehmer : teilnehmern) {
            Auswertung auswertung = new Auswertung();
            auswertung.setName(teilnehmer.getVorname() + " " + teilnehmer.getNachname());
            auswertung.setMatrikelnummer(teilnehmer.getId());
            auswertung.setVeranstaltung(veranstaltung.getTitel());
            // Punkte suchen für Teilnehmer
            List<Gruppenarbeit> gruppenarbeiten = teilnehmer.getGruppenarbeiten();
            List<Float> punkteGruppenarbeiten = new ArrayList<>();


            for (Gruppenarbeit gruppenarbeit : gruppenarbeiten) {
                auswertung.setGruppenarbeit(gruppenarbeit.getTitel());
                auswertung.setPunkte(
                        gruppenarbeitTeilnehmerService.findPunkteByMatrikelNrAndGruppenarbeitId(
                                teilnehmer.getId(),gruppenarbeit.getId()
                        )
                );
                auswertungen.add(auswertung);
            }
        }
        return auswertungen;
    }




}

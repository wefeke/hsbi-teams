
package com.example.application.services;

import com.example.application.models.Teilnehmer;
import com.example.application.models.User;
import com.example.application.models.Veranstaltung;
import com.example.application.repositories.VeranstaltungenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class VeranstaltungenService {

    private final VeranstaltungenRepository veranstaltungenRepository;

    public VeranstaltungenService(VeranstaltungenRepository veranstaltungenRepository) {
        this.veranstaltungenRepository = veranstaltungenRepository;
    }

    public List<Veranstaltung> findAllVeranstaltungenByUser(User user) {
        return veranstaltungenRepository.findByUser(user);
    }

    public Veranstaltung findVeranstaltungById(Long id, User user) {
        return veranstaltungenRepository.findByIdAndUser(id, user);
    }

    @Transactional
    public void saveVeranstaltung(Veranstaltung veranstaltung) {
        if (veranstaltung != null) {
            veranstaltungenRepository.save(veranstaltung);
        } else
            System.err.println("Test is null. Are you sure you have connected your form to the application?");
    }

    @Transactional
    public void deleteVeranstaltung(Veranstaltung veranstaltung) {
        if (veranstaltung != null) {
            veranstaltungenRepository.delete(veranstaltung);
        } else
            System.err.println("Test is null. Are you sure you have connected your form to the application?");
    }

    @Transactional
    public void removeTeilnehmerFromVeranstaltung(Teilnehmer teilnehmer, Long veranstaltungId, User user) {
        Veranstaltung veranstaltung = veranstaltungenRepository.findByIdAndUser(veranstaltungId, user);

        veranstaltung.getTeilnehmer().remove(teilnehmer);
        veranstaltungenRepository.save(veranstaltung);
    }

    @Transactional
    public void addTeilnehmer(Long veranstaltungId, Set<Teilnehmer> teilnehmer, User user) {
        Veranstaltung veranstaltung = veranstaltungenRepository.findByIdAndUser(veranstaltungId, user);
        if (veranstaltung != null) {
            Set<Teilnehmer> t= new HashSet<>(veranstaltung.getTeilnehmer());
            t.addAll(teilnehmer);
            veranstaltung.setTeilnehmer(t);
            veranstaltungenRepository.save(veranstaltung);
        } else {
            System.err.println("Veranstaltung nicht gefunden. Sind Sie sicher, dass die Veranstaltung existiert und der Benutzer berechtigt ist?");
        }
    }


}

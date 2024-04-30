/*
package com.example.application.services;

import com.example.application.models.Test;
import com.example.application.models.Veranstaltung;
import com.example.application.repositories.VeranstaltungenRepository;
import com.example.application.views.veranstaltungen.VeranstaltungenView;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VeranstaltungenService {

    private final VeranstaltungenRepository veranstaltungenRepository;

    public VeranstaltungenService(VeranstaltungenRepository veranstaltungenRepository, VeranstaltungenView ver) {
        this.veranstaltungenRepository = veranstaltungenRepository;
    }

    public List<Veranstaltung> findAllVeranstaltungen() {

            return veranstaltungenRepository.findAll();

    }

    public void saveVeranstaltung(Veranstaltung veranstaltung) {
        if (veranstaltung != null) {
            veranstaltungenRepository.save(veranstaltung);
        }
        else
            System.err.println("Test is null. Are you sure you have connected your form to the application?");
    }

}
*/
package com.example.application.services;

import com.example.application.models.Test;
import com.example.application.models.Veranstaltung;
import com.example.application.repositories.VeranstaltungenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VeranstaltungenService {

    private final VeranstaltungenRepository veranstaltungenRepository;

    @Autowired
    public VeranstaltungenService(VeranstaltungenRepository veranstaltungenRepository) {
        this.veranstaltungenRepository = veranstaltungenRepository;
    }

    public void addVeranstaltung(Veranstaltung veranstaltung) {
        veranstaltungenRepository.save(veranstaltung);
    }

    public void deleteVeranstaltung(Veranstaltung veranstaltung) {
        veranstaltungenRepository.delete(veranstaltung);
    }

    public void updateVeranstaltung(Veranstaltung veranstaltung) {
        veranstaltungenRepository.save(veranstaltung);
    }

    public List<Veranstaltung> findAllVeranstaltungen() {
            return veranstaltungenRepository.findAll();
    }
}
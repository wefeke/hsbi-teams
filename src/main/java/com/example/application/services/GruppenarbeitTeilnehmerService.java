package com.example.application.services;

import com.example.application.models.GruppenarbeitTeilnehmer;
import com.example.application.models.GruppenarbeitTeilnehmerId;
import com.example.application.repositories.GruppenarbeitTeilnehmerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service-Klasse für die Verwaltung von GruppenarbeitTeilnehmer-Entitäten.
 * Diese Klasse bietet Methoden zum Speichern, Abrufen und Verwalten von GruppenarbeitTeilnehmer-Daten.
 *
 * @author Leon
 */
@Service
public class GruppenarbeitTeilnehmerService {
    private final GruppenarbeitTeilnehmerRepository gruppenarbeitTeilnehmerRepository;

    public GruppenarbeitTeilnehmerService(GruppenarbeitTeilnehmerRepository gruppenarbeitTeilnehmerRepository) {
        this.gruppenarbeitTeilnehmerRepository = gruppenarbeitTeilnehmerRepository;
    }

    @Transactional
    public void save(GruppenarbeitTeilnehmer gruppenarbeitTeilnehmer) {
        gruppenarbeitTeilnehmerRepository.save(gruppenarbeitTeilnehmer);
    }

    @Transactional
    public Optional<GruppenarbeitTeilnehmer> findByID(GruppenarbeitTeilnehmerId teilnehmerGruppenarbeitId){
        return gruppenarbeitTeilnehmerRepository.findById(teilnehmerGruppenarbeitId);
    }

    @Transactional
    public Float findPunkteByMatrikelNrAndGruppenarbeitId(Long matrikelNr, Long gruppenarbeitId) {
        Float f = gruppenarbeitTeilnehmerRepository.findPunkteByMatrikelNrAndGruppenarbeitId(matrikelNr, gruppenarbeitId);
        if (f != null) return f;
        else return 0f;
    }
}

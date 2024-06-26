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
    /**
     * Konstruktor für GruppenarbeitTeilnehmerService.
     *
     * @param gruppenarbeitTeilnehmerRepository das Repository für GruppenarbeitTeilnehmer-Entitäten
     */
    public GruppenarbeitTeilnehmerService(GruppenarbeitTeilnehmerRepository gruppenarbeitTeilnehmerRepository) {
        this.gruppenarbeitTeilnehmerRepository = gruppenarbeitTeilnehmerRepository;
    }
    /**
     * Speichert eine GruppenarbeitTeilnehmer-Entität in der Datenbank.
     * Diese Methode ist transaktional, um Datenkonsistenz zu gewährleisten.
     *
     * @param gruppenarbeitTeilnehmer die zu speichernde GruppenarbeitTeilnehmer-Entität
     */
    @Transactional
    public void save(GruppenarbeitTeilnehmer gruppenarbeitTeilnehmer) {
        gruppenarbeitTeilnehmerRepository.save(gruppenarbeitTeilnehmer);
    }
    /**
     * Findet eine GruppenarbeitTeilnehmer-Entität anhand der eingebetteten ID.
     * Diese Methode ist transaktional und gibt ein Optional der gefundenen Entität zurück.
     *
     * @param teilnehmerGruppenarbeitId die eingebettete ID der GruppenarbeitTeilnehmer-Entität
     * @return ein Optional der gefundenen GruppenarbeitTeilnehmer-Entität
     */

    @Transactional
    public Optional<GruppenarbeitTeilnehmer> findByID(GruppenarbeitTeilnehmerId teilnehmerGruppenarbeitId){
        return gruppenarbeitTeilnehmerRepository.findById(teilnehmerGruppenarbeitId);
    }

    /**
     * Findet die Punkte, die ein Teilnehmer für eine bestimmte Gruppenarbeit erhalten hat.
     * Diese Methode ist transaktional und gibt die Punkte als Float zurück.
     *
     * @param matrikelNr die Matrikelnummer des Teilnehmers
     * @param gruppenarbeitId die ID der Gruppenarbeit
     * @return die Punkte als Float, oder 0f wenn keine Punkte gefunden wurden
     */
    @Transactional
    public Float findPunkteByMatrikelNrAndGruppenarbeitId(Long matrikelNr, Long gruppenarbeitId) {
        Float f = gruppenarbeitTeilnehmerRepository.findPunkteByMatrikelNrAndGruppenarbeitId(matrikelNr, gruppenarbeitId);
        if (f != null) return f;
        else return 0f;
    }
}

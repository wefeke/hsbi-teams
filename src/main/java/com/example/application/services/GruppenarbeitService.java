package com.example.application.services;

import com.example.application.models.Gruppenarbeit;
import com.example.application.repositories.GruppenarbeitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Der GruppenarbeitService bietet Methoden zur Interaktion mit Gruppenarbeit-Daten in der Datenbank.
 * Er verwendet das GruppenarbeitRepository zur Durchführung von Datenbankoperationen.
 * Er enthält Methoden zum Speichern, Löschen und Finden von Gruppenarbeiten.
 *
 * @author Lilli
 */
@Service
public class GruppenarbeitService {
    private final GruppenarbeitRepository gruppenarbeitRepository;

    public GruppenarbeitService(GruppenarbeitRepository gruppenarbeitRepository) {
        this.gruppenarbeitRepository = gruppenarbeitRepository;
    }

    @Transactional
    public void save(Gruppenarbeit gruppenarbeit) {
        gruppenarbeitRepository.save(gruppenarbeit);
    }

    @Transactional
    public void deleteGruppenarbeit(Gruppenarbeit gruppenarbeit) {
        if( gruppenarbeit != null) {
            gruppenarbeitRepository.delete(gruppenarbeit);
        } else {
            System.err.println("Gruppenarbeit is null. Are you sure you have connected your form to the application?");
        }
    }

    @Transactional
    public Gruppenarbeit findGruppenarbeitByIdWithGruppen(Long id) {
        return gruppenarbeitRepository.findByIdWithGruppen(id);
    }
}

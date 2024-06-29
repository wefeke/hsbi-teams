package com.example.application.services;

import com.example.application.models.Gruppe;
import com.example.application.repositories.GruppenRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Die GruppeService Klasse bietet Methoden zur Interaktion mit Gruppendaten in der Datenbank.
 * Sie enthält Methoden zum Speichern, Abrufen und Löschen von Gruppen.
 * Jede Methode ist als Transaktion gekennzeichnet, um die Datenintegrität zu gewährleisten.
 * Die Klasse verwendet das GruppenRepository zur Interaktion mit der Datenbank.
 *
 * @author Lilli
 */
@Service
public class GruppeService {

    private final GruppenRepository gruppenRepository;

    public GruppeService(GruppenRepository gruppenRepository) {
        this.gruppenRepository = gruppenRepository;
    }

    @Transactional
    public void save(Gruppe gruppe) {
        gruppenRepository.save(gruppe);
    }

    @Transactional
    public Gruppe findGruppeByIdWithTeilnehmer(Long id) {
        return gruppenRepository.findByIdWithTeilnehmer(id);
    }

    @Transactional
    public void deleteGruppe(Gruppe gruppe) {
        gruppenRepository.delete(gruppe);
    }
}

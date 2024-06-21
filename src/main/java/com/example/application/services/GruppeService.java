//Autor: Lilli
package com.example.application.services;

import com.example.application.models.Gruppe;
import com.example.application.repositories.GruppenRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

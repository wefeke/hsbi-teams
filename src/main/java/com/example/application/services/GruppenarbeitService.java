//Autor: Lilli
package com.example.application.services;

import com.example.application.models.Gruppenarbeit;
import com.example.application.repositories.GruppenarbeitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

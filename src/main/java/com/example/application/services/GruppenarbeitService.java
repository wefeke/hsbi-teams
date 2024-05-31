//Autor: Lilli
package com.example.application.services;

import com.example.application.models.Gruppe;
import com.example.application.models.Gruppenarbeit;
import com.example.application.repositories.GruppenarbeitRepository;
import org.springframework.stereotype.Service;

@Service
public class GruppenarbeitService {
    private final GruppenarbeitRepository gruppenarbeitRepository;

    public GruppenarbeitService(GruppenarbeitRepository gruppenarbeitRepository) {
        this.gruppenarbeitRepository = gruppenarbeitRepository;
    }

    public void save(Gruppenarbeit gruppenarbeit) {
        gruppenarbeitRepository.save(gruppenarbeit);
    }

    public void deleteGruppenarbeit(Gruppenarbeit gruppenarbeit) {
        if( gruppenarbeit != null) {
            gruppenarbeitRepository.delete(gruppenarbeit);
        } else {
            System.err.println("Gruppenarbeit is null. Are you sure you have connected your form to the application?");
        }
    }

    public Gruppenarbeit findGruppenarbeitByIdWithGruppen(Long id) {
        Gruppenarbeit gruppenarbeit = gruppenarbeitRepository.findByIdWithGruppen(id);
        return gruppenarbeit;
    }
}

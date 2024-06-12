package com.example.application.services;

import com.example.application.models.GruppenarbeitTeilnehmer;
import com.example.application.models.GruppenarbeitTeilnehmerId;
import com.example.application.repositories.GruppenarbeitTeilnehmerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GruppenarbeitTeilnehmerService {

    private final GruppenarbeitTeilnehmerRepository gruppenarbeitTeilnehmerRepository;


    public GruppenarbeitTeilnehmerService(GruppenarbeitTeilnehmerRepository gruppenarbeitTeilnehmerRepository) {
        this.gruppenarbeitTeilnehmerRepository = gruppenarbeitTeilnehmerRepository;
    }

    public void save(GruppenarbeitTeilnehmer gruppenarbeitTeilnehmer) {
        gruppenarbeitTeilnehmerRepository.save(gruppenarbeitTeilnehmer);
    }

    public void deleteGruppenarbeit(GruppenarbeitTeilnehmer teilnehmerGruppenarbeit) {
        if(teilnehmerGruppenarbeit != null) {
            gruppenarbeitTeilnehmerRepository.delete(teilnehmerGruppenarbeit);
        } else {
            System.err.println("TeilnehmerGruppenarbeit is null. Are you sure you have connected your form to the application?");
        }
    }

    public Optional<GruppenarbeitTeilnehmer> findByID(GruppenarbeitTeilnehmerId teilnehmerGruppenarbeitId){
        return gruppenarbeitTeilnehmerRepository.findById(teilnehmerGruppenarbeitId);
    }

//    public void update(GruppenarbeitTeilnehmer gruppenarbeitTeilnehmer) {
//        gruppenarbeitTeilnehmerRepository.update(gruppenarbeitTeilnehmer.getId().getTeilnehmerMatrikelNr(),gruppenarbeitTeilnehmer.getId().getGruppenarbeitId(),gruppenarbeitTeilnehmer.getPunkte());
//    }

    public boolean exists(GruppenarbeitTeilnehmer teilnehmerGruppenarbeit) {
        return gruppenarbeitTeilnehmerRepository.existsById(teilnehmerGruppenarbeit.getId());
    }

    public Float findPunkteByMatrikelNrAndGruppenarbeitId(Long matrikelNr, Long gruppenarbeitId) {
        return gruppenarbeitTeilnehmerRepository.findPunkteByMatrikelNrAndGruppenarbeitId(matrikelNr, gruppenarbeitId);
    }
}

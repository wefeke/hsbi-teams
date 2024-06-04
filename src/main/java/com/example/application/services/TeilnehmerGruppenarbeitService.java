package com.example.application.services;

import com.example.application.models.TeilnehmerGruppenarbeit;
import com.example.application.models.TeilnehmerGruppenarbeitId;
import com.example.application.repositories.GruppenarbeitRepository;
import com.example.application.repositories.TeilnehmerGruppenarbeitRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TeilnehmerGruppenarbeitService {
    private final TeilnehmerGruppenarbeitRepository teilnehmerGruppenarbeitRepository;

    public TeilnehmerGruppenarbeitService(TeilnehmerGruppenarbeitRepository teilnehmerGruppenarbeitRepository) {
        this.teilnehmerGruppenarbeitRepository = teilnehmerGruppenarbeitRepository;
    }

    public void save(TeilnehmerGruppenarbeit teilnehmerGruppenarbeit) {
        teilnehmerGruppenarbeitRepository.save(teilnehmerGruppenarbeit);
    }

    public void deleteGruppenarbeit(TeilnehmerGruppenarbeit teilnehmerGruppenarbeit) {
        if(teilnehmerGruppenarbeit != null) {
            teilnehmerGruppenarbeitRepository.delete(teilnehmerGruppenarbeit);
        } else {
            System.err.println("TeilnehmerGruppenarbeit is null. Are you sure you have connected your form to the application?");
        }
    }

    public  Optional<TeilnehmerGruppenarbeit> findByID(TeilnehmerGruppenarbeitId teilnehmerGruppenarbeitId){
        return teilnehmerGruppenarbeitRepository.findById(teilnehmerGruppenarbeitId);
    }

    public void update(TeilnehmerGruppenarbeit teilnehmerGruppenarbeit) {
        teilnehmerGruppenarbeitRepository.update(teilnehmerGruppenarbeit.getId().getTeilnehmerMatrikelNr(),teilnehmerGruppenarbeit.getId().getGruppenarbeitId(),teilnehmerGruppenarbeit.getId().getPunkte());
    }

    public boolean exists(TeilnehmerGruppenarbeit teilnehmerGruppenarbeit) {
        return teilnehmerGruppenarbeitRepository.existsById(teilnehmerGruppenarbeit.getId());
    }
}

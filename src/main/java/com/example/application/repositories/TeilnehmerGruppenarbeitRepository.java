package com.example.application.repositories;

import com.example.application.models.TeilnehmerGruppenarbeit;
import com.example.application.models.TeilnehmerGruppenarbeitId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface TeilnehmerGruppenarbeitRepository extends JpaRepository<TeilnehmerGruppenarbeit,TeilnehmerGruppenarbeitId> {

    @Override
    Optional<TeilnehmerGruppenarbeit> findById(TeilnehmerGruppenarbeitId teilnehmerGruppenarbeitId);

    @Modifying
    @Transactional
    @Query("update TeilnehmerGruppenarbeit u set u.id.punkte =:punkte where u.id.teilnehmerMatrikelNr =:teilnehmerID AND u.id.gruppenarbeitId =:gruppenarbeitID")
    void update(@Param("teilnehmerID") Long teilnehmerID, @Param("gruppenarbeitID") Long gruppenarbeitID,@Param("punkte") Float punkte);

    @Override
    boolean existsById(TeilnehmerGruppenarbeitId teilnehmerGruppenarbeitId);
}

package com.example.application.repositories;

import com.example.application.models.GruppenarbeitTeilnehmer;
import com.example.application.models.GruppenarbeitTeilnehmerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface GruppenarbeitTeilnehmerRepository extends JpaRepository<GruppenarbeitTeilnehmer, GruppenarbeitTeilnehmerId> {

//    @Override
//    Optional<GruppenarbeitTeilnehmer> findById(GruppenarbeitTeilnehmerId teilnehmerGruppenarbeitId);
//
//    @Modifying
//    @Transactional
//    @Query("update GruppenarbeitTeilnehmer u set u.punkte =:punkte where u.id.teilnehmerMatrikelNr =:teilnehmerID AND u.id.gruppenarbeitId =:gruppenarbeitID")
//    void update(@Param("teilnehmerID") Long teilnehmerID, @Param("gruppenarbeitID") Long gruppenarbeitID, @Param("punkte") Float punkte);
//
//    @Override
//    boolean existsById(GruppenarbeitTeilnehmerId teilnehmerGruppenarbeitId);
//
    @Query(value = "SELECT punkte FROM gruppenarbeit_teilnehmer WHERE teilnehmer_matrikel_nr = :matrikelNr AND gruppenarbeiten_id = :gruppenarbeitId", nativeQuery = true)
    Float findPunkteByMatrikelNrAndGruppenarbeitId(@Param("matrikelNr") Long matrikelNr, @Param("gruppenarbeitId") Long gruppenarbeitId);

}
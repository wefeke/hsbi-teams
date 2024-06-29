package com.example.application.repositories;

import com.example.application.models.GruppenarbeitTeilnehmer;
import com.example.application.models.GruppenarbeitTeilnehmerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository-Schnittstelle für die Entität GruppenarbeitTeilnehmer.
 * Diese Schnittstelle bietet Methoden zum Zugriff auf Daten der Tabelle "gruppenarbeit_teilnehmer".
 *
 * @autor Leon
 */
public interface GruppenarbeitTeilnehmerRepository extends JpaRepository<GruppenarbeitTeilnehmer, GruppenarbeitTeilnehmerId> {
    @Query(value = "SELECT punkte FROM gruppenarbeit_teilnehmer WHERE teilnehmer_matrikel_nr = :matrikelNr AND gruppenarbeiten_id = :gruppenarbeitId", nativeQuery = true)
    Float findPunkteByMatrikelNrAndGruppenarbeitId(@Param("matrikelNr") Long matrikelNr, @Param("gruppenarbeitId") Long gruppenarbeitId);

}
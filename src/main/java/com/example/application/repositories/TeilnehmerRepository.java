package com.example.application.repositories;

import com.example.application.models.Teilnehmer;
import com.example.application.models.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface TeilnehmerRepository extends JpaRepository<Teilnehmer, Long> {
    @Query("select c from Teilnehmer c " +
            "where lower(c.vorname) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(c.nachname) like lower(concat('%', :searchTerm, '%'))")
    List<Teilnehmer> search(@Param("searchTerm") String searchTerm);

    Optional<Teilnehmer> findByMatrikelNr(Long matrikelNr);


   /* @Query("select c from Teilnehmer c " +
            "where lower(c.vorname) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(c.nachname) like lower(concat('%', :searchTerm, '%')) " +
            "or c.matrikelNr like concat('%', :searchTerm, '%')")
    List<Teilnehmer> search(@Param("searchTerm") String searchTerm, Pageable pageable);

    Optional<Teilnehmer> findByMatrikelNr(Long matrikelNr);

    */

    @Query("SELECT t FROM Teilnehmer t JOIN t.veranstaltungen v WHERE v.veranstaltungsId = :veranstaltungId")
    List<Teilnehmer> findByVeranstaltungId(@Param("veranstaltungId") Long veranstaltungId);

}

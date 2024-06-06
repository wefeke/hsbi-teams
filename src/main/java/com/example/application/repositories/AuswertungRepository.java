package com.example.application.repositories;

import com.example.application.models.Auswertung;

import com.example.application.models.Gruppenarbeit;
import com.example.application.models.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuswertungRepository extends JpaRepository<Auswertung, Long> {

    @Query(value =
            "SELECT "+
            "tg.teilnehmer_matrikel_nr AS matrikelnummer, "+
            "t.vorname AS vorname, "+
            "t.nachname AS nachname, "+
            "v.titel AS veranstaltung, "+
            "g.titel AS gruppenarbeit, "+
            "tg.punkte AS punkte "+
            "FROM teilnehmer_gruppenarbeit tg "+
            "INNER JOIN gruppenarbeit g ON tg.gruppenarbeit_id = g.id "+
            "INNER JOIN teilnehmer t ON t.matrikel_nr = tg.teilnehmer_matrikel_nr "+
            "INNER JOIN veranstaltungstermin v2 ON v2.id = g.veranstaltungstermin_id "+
            "INNER JOIN veranstaltung v ON v2.veranstaltung_id = v.id "

            , nativeQuery = true)
    List<Auswertung> findAllAuswertungen();


    @Override
    boolean existsById(Long aLong);

    @Query(value =
            "SELECT "+
                    "tg.teilnehmer_matrikel_nr AS matrikelnummer, "+
                    "t.vorname AS vorname, "+
                    "t.nachname AS nachname, "+
                    "v.titel AS veranstaltung, "+
                    "g.titel AS gruppenarbeit, "+
                    "tg.punkte AS punkte "+
                    "FROM teilnehmer_gruppenarbeit tg "+
                    "INNER JOIN gruppenarbeit g ON tg.gruppenarbeit_id = g.id "+
                    "INNER JOIN teilnehmer t ON t.matrikel_nr = tg.teilnehmer_matrikel_nr "+
                    "INNER JOIN veranstaltungstermin v2 ON v2.id = g.veranstaltungstermin_id "+
                    "INNER JOIN veranstaltung v ON v2.veranstaltung_id = v.id "+
                    "WHERE v.id = :id"

            , nativeQuery = true)
    List<Auswertung> findAllAuswertungenWithID(@Param("id") Long id);
}

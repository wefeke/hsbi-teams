package com.example.application.repositories;

import com.example.application.models.Auswertung;

import com.example.application.models.Test;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AuswertungRepository extends JpaRepository<Auswertung, Long> {

    @Query(value = "select t.matrikel_nr as id, t.vorname as name, v.titel as titel_veranstaltung, g.titel as titel_Gruppenarbeit, gt.punkte from teilnehmer t \n" +
            "\tinner join teilnehmer_veranstaltungen tv \n" +
            "\t\ton t.matrikel_nr = tv.teilnehmer_matrikel_nr \n" +
            "\tinner join veranstaltung v \n" +
            "\t\ton tv.veranstaltungen_veranstaltungs_id = v.veranstaltungs_id \n" +
            "\tinner join veranstaltung_veranstaltungstermine vv\n" +
            "\t\ton v.veranstaltungs_id = vv.veranstaltung_veranstaltungs_id \n" +
            "\tinner join veranstaltungstermin v2 \n" +
            "\t\ton vv.veranstaltungstermine_id = v2.id \n" +
            "\tinner join veranstaltungstermin_gruppenarbeiten vg \n" +
            "\t\ton v2.id = vg.veranstaltungstermin_id \n" +
            "\tinner join gruppenarbeit g \n" +
            "\t\ton vg.gruppenarbeiten_id = g.id \n" +
            "\tinner join gruppenarbeit_teilnehmer gt\n" +
            "\t\ton g.id = gt.gruppenarbeit_id\n" +
            "\t\n", nativeQuery = true)
    List<Auswertung> findAllAuswertungen();



}

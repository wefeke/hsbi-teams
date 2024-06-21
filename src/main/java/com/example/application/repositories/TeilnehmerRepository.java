package com.example.application.repositories;

import com.example.application.models.Teilnehmer;

import com.example.application.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TeilnehmerRepository extends JpaRepository<Teilnehmer, Long> {
    Optional<Teilnehmer> findTeilnehmerByVornameAndNachnameAndUser (String vorname, String nachname, User user);

    Optional<Teilnehmer> findTeilnehmerByMatrikelNrAndUser (Long matrikelNr, User user);

    @Query("select c from Teilnehmer c " +
            "where c.user = :user " +
            "and (lower(c.vorname) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(c.nachname) like lower(concat('%', :searchTerm, '%')))" +
            "or cast(c.matrikelNr as string) like lower(concat('%', :searchTerm, '%'))")
    List<Teilnehmer> searchByUser(@Param("user") User user, @Param("searchTerm") String searchTerm);

    @Query("SELECT t FROM Teilnehmer t JOIN t.veranstaltungen v WHERE v.id = :id")
    List<Teilnehmer> findByVeranstaltungId(@Param("id") Long id);

    @Query("select t from Teilnehmer t where t.hinzugefuegtAm <= :datum and t.user = :user")
    List<Teilnehmer> findStudierendeVorJahren(@Param("datum") LocalDateTime datum, @Param("user") User user);

    List<Teilnehmer> findByUser(User user);

    @Query("select t from Teilnehmer t where t.veranstaltungen is empty and t.user = :user")
    List<Teilnehmer> findStudierendeOhneVeranstaltung(@Param("user") User user);

    @Query("SELECT t FROM Teilnehmer t WHERE t.matrikelNr NOT IN (SELECT teil.matrikelNr FROM Veranstaltung v JOIN v.teilnehmer teil WHERE v.id = :veranstaltungId)and t.user = :user")
    List<Teilnehmer> findAllTeilnehmerNotInVeranstaltung(Long veranstaltungId , User user);

    @Query(value = "SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Teilnehmer t JOIN t.gruppenarbeiten g WHERE t.matrikelNr = :teilnehmerId AND g.veranstaltungstermin.veranstaltung.id = :veranstaltungId")
    Boolean isTeilnehmerInGruppenarbeit(@Param("teilnehmerId") Long teilnehmerId, @Param("veranstaltungId") Long veranstaltungId);

    Optional<Teilnehmer> findByMatrikelNrAndUserId(Long matrikelNr, Long userId);
}


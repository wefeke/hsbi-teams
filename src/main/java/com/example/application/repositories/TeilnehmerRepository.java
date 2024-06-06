package com.example.application.repositories;

import com.example.application.models.Teilnehmer;
import com.example.application.models.Test;
import com.example.application.models.User;
import com.example.application.models.Veranstaltung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TeilnehmerRepository extends JpaRepository<Teilnehmer, Long> {
  /*  @Query("select c from Teilnehmer c " +
            "where lower(c.vorname) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(c.nachname) like lower(concat('%', :searchTerm, '%'))")
    List<Teilnehmer> search(@Param("searchTerm") String searchTerm);

    Optional<Teilnehmer> findByMatrikelNr(Long matrikelNr);
*/

    @Query("select c from Teilnehmer c " +
            "where c.user = :user " +
            "and (lower(c.vorname) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(c.nachname) like lower(concat('%', :searchTerm, '%')))" +
            "or cast(c.matrikelNr as string) like lower(concat('%', :searchTerm, '%'))")
    List<Teilnehmer> searchByUser(@Param("user") User user, @Param("searchTerm") String searchTerm);

    Optional<Teilnehmer> findByMatrikelNr(Long matrikelNr);

    @Query("SELECT t FROM Teilnehmer t JOIN t.veranstaltungen v WHERE v.id = :id")
    List<Teilnehmer> findByVeranstaltungId(@Param("id") Long id);

    @Query("SELECT t FROM Teilnehmer t WHERE t.hinzugefuegtAm<:vierJahreZurueck")
    List<Teilnehmer> findStudierendeVorVierJahren(@Param("vierJahreZurueck") LocalDateTime vierJahreZurueck);

   List<Teilnehmer> findByUser(User user);

   Veranstaltung findByIdAndUser(Long Id, User user);
}

package com.example.application.repositories;

import com.example.application.models.Teilnehmer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
        "where lower(c.vorname) like lower(concat('%', :searchTerm, '%')) " +
        "or lower(c.nachname) like lower(concat('%', :searchTerm, '%')) " +
        "or cast(c.matrikelNr as string) like lower(concat('%', :searchTerm, '%'))")
    List<Teilnehmer> search(@Param("searchTerm") String searchTerm);

    Optional<Teilnehmer> findByMatrikelNr(Long matrikelNr);

    @Query("SELECT t FROM Teilnehmer t JOIN t.veranstaltungen v WHERE v.id = :id")
    List<Teilnehmer> findByVeranstaltungId(@Param("id") Long id);

    @Query("SELECT t FROM Teilnehmer t WHERE t.hinzugefuegtAm<:vierJahreZurueck")
    List<Teilnehmer> findStudierendeVorVierJahren(@Param("vierJahreZurueck") LocalDateTime vierJahreZurueck);

    @Query(value = "SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Teilnehmer t JOIN t.gruppenarbeiten g WHERE t.matrikelNr = :teilnehmerId AND g.veranstaltungstermin.veranstaltung.id = :veranstaltungId")
    Boolean isTeilnehmerInGruppenarbeit(@Param("teilnehmerId") Long teilnehmerId, @Param("veranstaltungId") Long veranstaltungId);
}

package com.example.application;

import com.example.application.models.Role;
import com.example.application.models.Teilnehmer;
import com.example.application.models.User;
import com.example.application.models.Veranstaltung;
import com.example.application.repositories.*;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.antlr.v4.runtime.misc.Array2DHashSet;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * The entry point of the Spring Boot application.
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@Theme(value = "flowcrmtutorial")
public class Application implements AppShellConfigurator, CommandLineRunner {

    private AuswertungRepository auswertungRepository;
    private GruppenarbeitRepository gruppenarbeitRepository;
    private GruppenRepository gruppenRepository;
    private TeilnehmerGruppenarbeitRepository teilnehmerGruppenarbeitRepository;
    private TeilnehmerRepository teilnehmerRepository;
    private UserRepository userRepository;
    private VeranstaltungenRepository veranstaltungRepository;
    private VeranstaltungsterminRepository veranstaltungsterminRepository;

    private PasswordEncoder passwordEncoder;

    public Application(AuswertungRepository auswertungRepository, GruppenarbeitRepository gruppenarbeitRepository, GruppenRepository gruppenRepository, TeilnehmerGruppenarbeitRepository teilnehmerGruppenarbeitRepository, TeilnehmerRepository teilnehmerRepository, UserRepository userRepository, VeranstaltungenRepository veranstaltungRepository, VeranstaltungsterminRepository veranstaltungsterminRepository, PasswordEncoder passwordEncoder){
        this.auswertungRepository = auswertungRepository;
        this.gruppenarbeitRepository = gruppenarbeitRepository;
        this.gruppenRepository = gruppenRepository;
        this.teilnehmerGruppenarbeitRepository = teilnehmerGruppenarbeitRepository;
        this.teilnehmerRepository = teilnehmerRepository;
        this.userRepository = userRepository;
        this.veranstaltungRepository = veranstaltungRepository;
        this.veranstaltungsterminRepository = veranstaltungsterminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run (String ... args) {
        User testUser = new User("wefeke", "Kennet", passwordEncoder.encode("kennet"), true, Set.of(Role.ADMIN), null, new ArrayList<>());
        Veranstaltung testVeranstaltung = new Veranstaltung(LocalDate.now(), "Testveranstaltung", testUser, new ArrayList<>(), new HashSet<>());
        Teilnehmer testTeilnehmer = new Teilnehmer(1L, "Max", "Mustermann", testUser);
        Teilnehmer testTeilnehmer2 = new Teilnehmer(2L, "Marcel", "Weithöner", testUser);
        Teilnehmer testTeilnehmer3 = new Teilnehmer(3L, "Joris", "Strakeljahn", testUser);


        testVeranstaltung.addTeilnehmer(testTeilnehmer);
        testVeranstaltung.addTeilnehmer(testTeilnehmer2);
        testVeranstaltung.addTeilnehmer(testTeilnehmer3);
        testUser.addVeranstaltungen(testVeranstaltung);

        userRepository.save(testUser);
        teilnehmerRepository.save(testTeilnehmer);
        teilnehmerRepository.save(testTeilnehmer2);
        teilnehmerRepository.save(testTeilnehmer3);
        veranstaltungRepository.save(testVeranstaltung);

        testTeilnehmer.addVerastaltung(testVeranstaltung);
        testTeilnehmer2.addVerastaltung(testVeranstaltung);
        testTeilnehmer3.addVerastaltung(testVeranstaltung);

        teilnehmerRepository.save(testTeilnehmer);
        teilnehmerRepository.save(testTeilnehmer2);
        teilnehmerRepository.save(testTeilnehmer3);

    }



}

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

import java.nio.file.Path;
import java.nio.file.Paths;
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

    private GruppenarbeitRepository gruppenarbeitRepository;
    private GruppenRepository gruppenRepository;
    // private TeilnehmerGruppenarbeitRepository teilnehmerGruppenarbeitRepository;
    private TeilnehmerRepository teilnehmerRepository;
    private UserRepository userRepository;
    private VeranstaltungenRepository veranstaltungRepository;
    private VeranstaltungsterminRepository veranstaltungsterminRepository;

    private PasswordEncoder passwordEncoder;

    public Application(GruppenarbeitRepository gruppenarbeitRepository, GruppenRepository gruppenRepository, TeilnehmerRepository teilnehmerRepository, UserRepository userRepository, VeranstaltungenRepository veranstaltungRepository, VeranstaltungsterminRepository veranstaltungsterminRepository, PasswordEncoder passwordEncoder){
        this.gruppenarbeitRepository = gruppenarbeitRepository;
        this.gruppenRepository = gruppenRepository;
        //this.teilnehmerGruppenarbeitRepository = teilnehmerGruppenarbeitRepository;
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

        // Initialize two users: Admin and User
        User admin = new User("admin", "Admin", passwordEncoder.encode("admin"), false, Set.of(Role.ADMIN), null, new ArrayList<>());
        User user = new User("user", "User", passwordEncoder.encode("user"), false, Set.of(Role.USER), null, new ArrayList<>());
        userRepository.save(admin);
        userRepository.save(user);

        // Initialize a Veranstaltung for the Admin
        Veranstaltung veranstaltung = new Veranstaltung(1000001L, LocalDate.now(), "Test Veranstaltung", admin);
        veranstaltungRepository.save(veranstaltung);

        // Initialize 3 Teilnehmer for the Veranstaltung
        Teilnehmer teilnehmer1 = new Teilnehmer(1000001L, "Max", "Müller", admin);
        Teilnehmer teilnehmer2 = new Teilnehmer(1000002L, "Anna", "Schmidt", admin);
        Teilnehmer teilnehmer3 = new Teilnehmer(1000003L, "Paul", "Schneider", admin);
        teilnehmerRepository.save(teilnehmer1);
        teilnehmerRepository.save(teilnehmer2);
        teilnehmerRepository.save(teilnehmer3);

        // Add the Teilnehmer to the Veranstaltung
        veranstaltung.addTeilnehmer(teilnehmer1);
        veranstaltung.addTeilnehmer(teilnehmer2);
        veranstaltung.addTeilnehmer(teilnehmer3);
        veranstaltungRepository.save(veranstaltung);
    }



}

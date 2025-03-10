/*
    In dieser Datei sind folgende Insert-Statements enthalten:

    - INSERT INTO public.users (locked, id, hashed_password, "name", username, profile_picture)
        Anzahl: 2
    - INSERT INTO public.user_roles (user_id, roles)
        Anzahl: 2
    - INSERT INTO public.veranstaltung (id, semester, titel, user_id)
        Anzahl: 4
    - INSERT INTO public.veranstaltungstermin (id, datum, end_zeit, notizen, ort, start_zeit, veranstaltung_id, user_id)
        Anzahl: 7
    - INSERT INTO public.gruppenarbeit (id, beschreibung, titel, veranstaltungstermin_id, user_id)
        Anzahl: 3
    - INSERT INTO public.gruppe (id, nummer, gruppenarbeit_id, user_id)
        Anzahl: 12

    - INSERT INTO public.teilnehmer (matrikel_nr, nachname, vorname, "hinzugefügt am", user_id)
    - INSERT INTO public.veranstaltung_teilnehmer (teilnehmer_matrikel_nr, veranstaltungen_id)
    - INSERT INTO public.gruppe_teilnehmer (gruppe_id, teilnehmer_matrikel_nr)
    - INSERT INTO public.gruppenarbeit_teilnehmer (gruppenarbeit_id, teilnehmer_matrikel_nr)
*/

INSERT INTO public.users (locked, id, hashed_password, "name", username, profile_picture) VALUES
                                                                                              (false, 1, '$2a$10$jpLNVNeA7Ar/ZQ2DKbKCm.MuT2ESe.Qop96jipKMq7RaUgCoQedV.', 'Admin', 'admin', null);

INSERT INTO public.user_roles (user_id, roles) VALUES
                                                   (1, 'ADMIN');

/* //Ab hier einkommentieren für initialen Datenbestand

INSERT INTO public.users (locked, id, hashed_password, "name", username, profile_picture) VALUES
                                                                                              (true, 2, '$2a$10$jpLNVNeA7Ar/ZQ2DKbKCm.MuT2ESe.Qop96jipKMq7RaUgCoQedV.', 'Test User', 'testUser', null);

INSERT INTO public.user_roles (user_id, roles) VALUES
                                                   (2, 'USER');

INSERT INTO public.veranstaltung (id, semester, titel, user_id) VALUES
                                                                    (1001, '2024-06-12', 'Materialwirtschaft und Produktionsplanung/-steuerung (Sose 2024-5 WI(P) 23)', 1),
                                                                    (1002, '2024-05-01', 'Mathematik für Ökonomen (Sose 2024-5 M/S 01)', 1),
                                                                    (1003, '2024-05-26', 'Webtechnologien (Sose 2024-5 WI(P) 13)', 1),
                                                                    (1004, '2024-05-30', 'Softwareprojekt (Sose 2024-5 WI(P) 28)', 2);


INSERT INTO public.veranstaltungstermin (id, datum, end_zeit, titel, ort, start_zeit, veranstaltung_id, user_id) VALUES
                                                                                                                     (1, '2024-06-01', '12:00:00', 'Einführung in SE', 'Raum 101', '10:00:00', 1001, 1),
                                                                                                                     (2, '2024-06-02', '16:00:00', 'Fortgeschrittene Themen in SE', 'Raum 102', '14:00:00', 1001, 1),
                                                                                                                     (3, '2024-06-03', '11:00:00', 'Gruppenarbeit Präsentation', 'Raum 103', '09:00:00', 1001, 1),
                                                                                                                     (4, '2024-06-04', '13:00:00', 'Projektbesprechung', 'Raum 104', '11:00:00', 1002, 1),
                                                                                                                     (5, '2024-06-05', '15:00:00', 'Gastvortrag', 'Raum 105', '13:00:00', 1002, 1),
                                                                                                                     (6, '2024-06-06', '17:00:00', 'Abschlusspräsentation', 'Raum 106', '15:00:00', 1002, 1),
                                                                                                                     (7, '2024-06-07', '11:00:00', 'Feedback-Runde', 'Raum 107', '09:00:00', 1003, 1);

INSERT INTO public.gruppenarbeit (id, beschreibung, titel, veranstaltungstermin_id, user_id) VALUES
                                                                                                 (1, 'Analyse von Markttrends und Erstellung eines Berichts', 'Marktanalyse', 1, 1),
                                                                                                 (2, 'Entwicklung eines Prototypen für eine neue App', 'App-Entwicklung', 2, 1),
                                                                                                 (6, 'Analyse von Kundendaten und Erstellung eines Reports', 'Kundendatenanalyse', 2, 1);



INSERT INTO public.gruppe (id, nummer, gruppenarbeit_id, user_id) VALUES
                                                                      (1, 1, 1, 1),
                                                                      (2, 2, 1, 1),
                                                                      (3, 3, 1, 1),
                                                                      (4, 1, 2, 1),
                                                                      (5, 2, 2, 1),
                                                                      (6, 3, 2, 1),
                                                                      (7, 4, 2, 1),
                                                                      (8, 5, 2, 1),
                                                                      (9, 6, 2, 1),
                                                                      (16, 1, 6, 1),
                                                                      (17, 2, 6, 1),
                                                                      (18, 3, 6, 1);


INSERT INTO public.teilnehmer (matrikel_nr, nachname, vorname, "hinzugefügt am", user_id) VALUES
                                                                                              (1000001, 'Müller', 'Max', now(), 1),
                                                                                              (1000002, 'Schmidt', 'Anna', now(), 1),
                                                                                              (1000003, 'Schneider', 'Paul', now(), 1),
                                                                                              (1000004, 'Fischer', 'Marie', now(), 1),
                                                                                              (1000005, 'Weber', 'Lukas', now(), 1),
                                                                                              (1000006, 'Meyer', 'Laura', now(), 1),
                                                                                              (1000007, 'Wagner', 'Jan', now(), 1),
                                                                                              (1000008, 'Becker', 'Lisa', now(), 1),
                                                                                              (1000009, 'Schulz', 'Tim', now(), 1),
                                                                                              (1000010, 'Hoffmann', 'Sophie', now(), 1),
                                                                                              (1000011, 'Koch', 'Leon', now(), 1),
                                                                                              (1000012, 'Bauer', 'Julia', now(), 1),
                                                                                              (1000013, 'Richter', 'Tom', now(), 1),
                                                                                              (1000014, 'Klein', 'Clara', now(), 1),
                                                                                              (1000015, 'Wolf', 'Nina', now(), 1),
                                                                                              (1000016, 'Schröder', 'Ben', now(), 1),
                                                                                              (1000017, 'Neumann', 'Mia', now(), 1),
                                                                                              (1000018, 'Braun', 'Felix', now(), 1),
                                                                                              (1000019, 'Krüger', 'Sarah', now(), 1),
                                                                                              (1000020, 'Hofmann', 'Emma', now(), 1),
                                                                                              (1000021, 'Hartmann', 'Jonas', now(), 1),
                                                                                              (1000022, 'Lange', 'Lara', now(), 1),
                                                                                              (1000023, 'Schmitt', 'Maximilian', now(), 1),
                                                                                              (1000024, 'Werner', 'Johanna', now(), 1),
                                                                                              (1000025, 'Krause', 'Luis', now(), 1),
                                                                                              (1000026, 'Meier', 'Katharina', now(), 1),
                                                                                              (1000027, 'Lehmann', 'David', now(), 1),
                                                                                              (1000028, 'Schmid', 'Elena', now(), 1),
                                                                                              (1000029, 'Schulze', 'Finn', now(), 1),
                                                                                              (1000030, 'Maier', 'Lea', now(), 1),
                                                                                              (1000031, 'Köhler', 'Jannis', now(), 1),
                                                                                              (1000032, 'Herrmann', 'Amelie', now(), 1),
                                                                                              (1000033, 'König', 'Marlon', now(), 1),
                                                                                              (1000034, 'Walter', 'Hannah', now(), 1),
                                                                                              (1000035, 'Mayer', 'Lennart', now(), 1),
                                                                                              (1000036, 'Huber', 'Sofia', now(), 1),
                                                                                              (1000037, 'Kaiser', 'Liam', now(), 1),
                                                                                              (1000038, 'Fuchs', 'Emily', now(), 1),
                                                                                              (1000039, 'Peters', 'Luca', now(), 1),
                                                                                              (1000040, 'Scholz', 'Maja', now(), 1),
                                                                                              (1000041, 'Lang', 'Henry', now(), 1),
                                                                                              (1000042, 'Weiß', 'Anna-Lena', now(), 1),
                                                                                              (1000043, 'Jung', 'Noah', now(), 1),
                                                                                              (1000044, 'Hahn', 'Mila', now(), 1),
                                                                                              (1000045, 'Schubert', 'Jakob', now(), 1),
                                                                                              (1000046, 'Vogel', 'Emma-Sophie', now(), 1),

                                                                                              (1000101, 'Bauer', 'Sophie', now(), 2),
                                                                                              (1000102, 'König', 'Elias', now(), 2),
                                                                                              (1000103, 'Kaiser', 'Emilia', now(), 2),
                                                                                              (1000104, 'Schulz', 'Noah', now(), 2),
                                                                                              (1000105, 'Richter', 'Lina', now(), 2),
                                                                                              (1000106, 'Wolf', 'Oscar', now(), 2),
                                                                                              (1000107, 'Neumann', 'Mila', now(), 2),
                                                                                              (1000108, 'Schwarz', 'Theo', now(), 2),
                                                                                              (1000109, 'Braun', 'Hannah', now(), 2),
                                                                                              (1000110, 'Krüger', 'Luisa', now(), 2),
                                                                                              (1000111, 'Hofmann', 'Felix', now(), 2),
                                                                                              (1000112, 'Lange', 'Charlotte', now(), 2),
                                                                                              (1000113, 'Hartmann', 'Paul', now(), 2);

INSERT INTO public.veranstaltung_teilnehmer (teilnehmer_matrikel_nr, veranstaltungen_id) VALUES
                                                                                             (1000001, 1001),
                                                                                             (1000002, 1001),
                                                                                             (1000003, 1001),
                                                                                             (1000004, 1001),
                                                                                             (1000005, 1001),
                                                                                             (1000006, 1001),
                                                                                             (1000007, 1001),
                                                                                             (1000008, 1001),
                                                                                             (1000009, 1001),
                                                                                             (1000010, 1001),
                                                                                             (1000011, 1001),
                                                                                             (1000012, 1001),
                                                                                             (1000013, 1001),
                                                                                             (1000014, 1001),
                                                                                             (1000015, 1001),
                                                                                             (1000016, 1001),
                                                                                             (1000017, 1001),
                                                                                             (1000018, 1001),
                                                                                             (1000019, 1001),
                                                                                             (1000020, 1001),
                                                                                             (1000021, 1001),
                                                                                             (1000022, 1001),
                                                                                             (1000023, 1001),
                                                                                             (1000024, 1001),
                                                                                             (1000025, 1001),
                                                                                             (1000026, 1001),
                                                                                             (1000027, 1001),
                                                                                             (1000028, 1001),
                                                                                             (1000029, 1001),
                                                                                             (1000030, 1001),

                                                                                             (1000021, 1002),
                                                                                             (1000022, 1002),
                                                                                             (1000023, 1002),
                                                                                             (1000024, 1002),
                                                                                             (1000025, 1002),
                                                                                             (1000026, 1002),
                                                                                             (1000027, 1002),
                                                                                             (1000028, 1002),
                                                                                             (1000029, 1002),
                                                                                             (1000030, 1002),
                                                                                             (1000031, 1002),
                                                                                             (1000032, 1002),
                                                                                             (1000033, 1002),
                                                                                             (1000034, 1002),
                                                                                             (1000035, 1002),
                                                                                             (1000036, 1002),
                                                                                             (1000037, 1002),
                                                                                             (1000038, 1002),
                                                                                             (1000039, 1002),
                                                                                             (1000040, 1002),
                                                                                             (1000041, 1002),
                                                                                             (1000042, 1002),
                                                                                             (1000043, 1002),
                                                                                             (1000044, 1002),
                                                                                             (1000045, 1002),
                                                                                             (1000046, 1002),

                                                                                             (1000001, 1003),
                                                                                             (1000002, 1003),
                                                                                             (1000003, 1003),
                                                                                             (1000004, 1003),
                                                                                             (1000005, 1003),
                                                                                             (1000006, 1003),
                                                                                             (1000007, 1003),
                                                                                             (1000008, 1003),
                                                                                             (1000009, 1003),
                                                                                             (1000010, 1003),
                                                                                             (1000011, 1003),
                                                                                             (1000012, 1003),
                                                                                             (1000013, 1003),
                                                                                             (1000014, 1003),
                                                                                             (1000015, 1003),
                                                                                             (1000016, 1003),
                                                                                             (1000017, 1003),
                                                                                             (1000018, 1003),
                                                                                             (1000019, 1003),
                                                                                             (1000020, 1003),

                                                                                             (1000101, 1004),
                                                                                             (1000102, 1004),
                                                                                             (1000103, 1004),
                                                                                             (1000104, 1004),
                                                                                             (1000105, 1004),
                                                                                             (1000106, 1004),
                                                                                             (1000107, 1004),
                                                                                             (1000108, 1004),
                                                                                             (1000109, 1004),
                                                                                             (1000110, 1004),
                                                                                             (1000111, 1004);

INSERT INTO public.gruppe_teilnehmer (gruppen_id, teilnehmer_matrikel_nr) VALUES
                                                                              (1, 1000001),
                                                                              (1, 1000002),
                                                                              (1, 1000003),
                                                                              (1, 1000004),
                                                                              (1, 1000005),
                                                                              (1, 1000006),
                                                                              (1, 1000007),
                                                                              (1, 1000008),

                                                                              (2, 1000009),
                                                                              (2, 1000010),
                                                                              (2, 1000011),
                                                                              (2, 1000012),
                                                                              (2, 1000013),
                                                                              (2, 1000014),
                                                                              (2, 1000015),
                                                                              (2, 1000016),

                                                                              (3, 1000017),
                                                                              (3, 1000018),
                                                                              (3, 1000019),
                                                                              (3, 1000020),
                                                                              (3, 1000021),
                                                                              (3, 1000022),
                                                                              (3, 1000023),

                                                                              (4, 1000001),
                                                                              (4, 1000002),
                                                                              (4, 1000003),
                                                                              (4, 1000004),

                                                                              (5, 1000005),
                                                                              (5, 1000006),
                                                                              (5, 1000007),
                                                                              (5, 1000008),

                                                                              (6, 1000009),
                                                                              (6, 1000010),
                                                                              (6, 1000011),
                                                                              (6, 1000012),

                                                                              (7, 1000013),
                                                                              (7, 1000014),
                                                                              (7, 1000015),
                                                                              (7, 1000016),

                                                                              (8, 1000017),
                                                                              (8, 1000018),
                                                                              (8, 1000019),
                                                                              (8, 1000020),

                                                                              (9, 1000021),
                                                                              (9, 1000022),
                                                                              (9, 1000023),
                                                                              (9, 1000024),
                                                                              (9, 1000025),

                                                                              (16, 1000001),
                                                                              (16, 1000002),
                                                                              (16, 1000003),

                                                                              (17, 1000009),
                                                                              (17, 1000010),
                                                                              (17, 1000011),

                                                                              (18, 1000017),
                                                                              (18, 1000018),
                                                                              (18, 1000019),
                                                                              (18, 1000020);



INSERT INTO public.gruppenarbeit_teilnehmer (gruppenarbeiten_id, teilnehmer_matrikel_nr, punkte) VALUES
                                                                                                     (1, 1000001, null),
                                                                                                     (1, 1000002, null),
                                                                                                     (1, 1000003, null),
                                                                                                     (1, 1000004, 1.5),
                                                                                                     (1, 1000005, null),
                                                                                                     (1, 1000006, null),
                                                                                                     (1, 1000007, null),
                                                                                                     (1, 1000008, 2),
                                                                                                     (1, 1000009, null),
                                                                                                     (1, 1000010, null),
                                                                                                     (1, 1000011, null),
                                                                                                     (1, 1000012, null),
                                                                                                     (1, 1000013, 1.5),
                                                                                                     (1, 1000014, null),
                                                                                                     (1, 1000015, null),
                                                                                                     (1, 1000016, 1),
                                                                                                     (1, 1000017, 1),
                                                                                                     (1, 1000018, null),
                                                                                                     (1, 1000019, null),
                                                                                                     (1, 1000020, null),
                                                                                                     (1, 1000021, null),
                                                                                                     (1, 1000022, null),
                                                                                                     (1, 1000023, null),
                                                                                                     (1, 1000024, null),
                                                                                                     (1, 1000025, null),
                                                                                                     (1, 1000026, null),
                                                                                                     (1, 1000027, null),
                                                                                                     (1, 1000028, null),
                                                                                                     (1, 1000029, null),
                                                                                                     (1, 1000030, null),

                                                                                                     (2, 1000001, null),
                                                                                                     (2, 1000002, null),
                                                                                                     (2, 1000003, 1),
                                                                                                     (2, 1000004, 2),
                                                                                                     (2, 1000005, null),
                                                                                                     (2, 1000006, null),
                                                                                                     (2, 1000007, null),
                                                                                                     (2, 1000008, null),
                                                                                                     (2, 1000009, null),
                                                                                                     (2, 1000010, null),
                                                                                                     (2, 1000011, 2.5),
                                                                                                     (2, 1000012, null),
                                                                                                     (2, 1000013, null),
                                                                                                     (2, 1000014, 1.5),
                                                                                                     (2, 1000015, null),
                                                                                                     (2, 1000016, null),
                                                                                                     (2, 1000017, null),
                                                                                                     (2, 1000018, null),
                                                                                                     (2, 1000019, null),
                                                                                                     (2, 1000020, 1.5),
                                                                                                     (2, 1000021, null),
                                                                                                     (2, 1000022, null),
                                                                                                     (2, 1000023, null),
                                                                                                     (2, 1000024, null),
                                                                                                     (2, 1000025, null),
                                                                                                     (2, 1000026, null),
                                                                                                     (2, 1000027, null),
                                                                                                     (2, 1000028, null),
                                                                                                     (2, 1000029, null),
                                                                                                     (2, 1000030, null),

                                                                                                     (6, 1000001, null),
                                                                                                     (6, 1000002, null),
                                                                                                     (6, 1000003, null),
                                                                                                     (6, 1000004, null),
                                                                                                     (6, 1000005, null),
                                                                                                     (6, 1000006, null),
                                                                                                     (6, 1000007, null),
                                                                                                     (6, 1000008, null),
                                                                                                     (6, 1000009, null),
                                                                                                     (6, 1000010, null),
                                                                                                     (6, 1000011, null),
                                                                                                     (6, 1000012, null),
                                                                                                     (6, 1000013, null),
                                                                                                     (6, 1000014, null),
                                                                                                     (6, 1000015, null),
                                                                                                     (6, 1000016, null),
                                                                                                     (6, 1000017, null),
                                                                                                     (6, 1000018, null),
                                                                                                     (6, 1000019, null),
                                                                                                     (6, 1000020, null),
                                                                                                     (6, 1000021, null),
                                                                                                     (6, 1000022, null),
                                                                                                     (6, 1000023, null),
                                                                                                     (6, 1000024, null),
                                                                                                     (6, 1000025, null),
                                                                                                     (6, 1000026, null),
                                                                                                     (6, 1000027, null),
                                                                                                     (6, 1000028, null),
                                                                                                     (6, 1000029, null),
                                                                                                     (6, 1000030, null);

 */

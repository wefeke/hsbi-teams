
# Schritt zur Einrichtung von Initialen Daten

1. In den application.properties bei der Variable **spring.jpa.hibernate.ddl-auto** den Wert von update auf create setzen.
2. Auch in den application.properties **spring.sql.init.mode** non never auf always setzen.
3. Programm einmal ausführen (es treten Fehler auf)
4. **spring.jpa.hibernate.ddl-auto** wieder auf update und **spring.sql.init.mode** auf never setzen
5. Programm kann jetzt ohne Fehelr mit intialen Daten in der Datenbank ausgeführt werden.

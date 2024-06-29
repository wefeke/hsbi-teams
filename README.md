# HSBI Teambuilder

Der HSBI Teambuilder ist eine Web-Anwendung, die theoretisch durch einen Server und eine feste URL bereitgestellt werden kann. Im Rahmen dieses Projektes wird die Anwendung mitsamt der Datenbank jedoch Lokal auf dem Computer des Benutzers gehostet.

## Installationsanleitung

Um eine Reibungslose Installation der Anwendung zu gewährleisten, müssen einige Grundlegende Bedingungen erfüllt sein:


- Für MacOS bietet es sich an das Paketverwaltungstool Homebrew zur Installation der JDK als auch Maven zu verwenden.


- Betriebssystem: Es ist ein Betriebssystem (Windows, MacOS) installiert.


- Java JDK: Die aktuelle Java JDK Version (26.06.2024 – JDK 22) ist installiert.


- IntelliJ IDEA benötigt das JDK, um Java-Anwendungen entwickeln zu können.


- Maven: Die aktuelle Version (26.06.2024 – Maven 3.9.8) ist installiert.

    - Obwohl IntelliJ IDEA mit einer eingebauten Version von Maven kommt, könnten Sie in manchen Fällen eine spezifische Version von Maven benötigen, die separat installiert werden muss.


- IntelliJ: Die aktuelle Version der Entwicklungsumgebung IntelliJ ist installiert und konfiguriert.

    - Zur Konfiguration gehört das Einrichten des JDKs in den Einstellungen von IntelliJ unter dem Abschnitt Project Structure. Zudem sollte auch sichergestellt werden, dass das Maven-Plugin aktiviert ist.

    - Bei der Installation gibt es zwei Versionen: Community und Ultimate. Die Community-Version ist kostenlos und unterstützt Maven-Projekte.


## Datenbankeinrichtung & Initialdaten

- PostgreSQL: Die aktuelle Version (26.06.2024- PostgreSQL 16) ist installiert und eine erste leere Datenbank mit Namen und Credentials wurde erzeugt.

    - Bei der Installation von PostgreSQL werden die Tools PostgreSQL Server, StackBuilder und PgAdmin installiert. PostgreSQL ist der Datenbank Server, über den die Datenbank lokal gehostet wird. StackBuilder ist ein Paketmanager, über den weiteren Werkzeuge und Driver hinzugefügt werden können. PgAdmin ist ein grafisches Datenbankmanagementtool, über das eine eigene Datenbank mit Namen und Credentials erstellt werden muss.


- Konfiguration der Umgebungsvariablen: Nach der Installation des JDKs und eventuell von Maven müssen Sie sicherstellen, dass die Umgebungsvariablen richtig gesetzt sind. Dies beinhaltet normalerweise das Hinzufügen der bin-Verzeichnisse von JDK und Maven zum PATH der Umgebungsvariablen sowie das Setzen der JAVA_HOME-Variable, die auf das Verzeichnis des JDK zeigt.


- Einrichten der Datenbankverbindung: Nachdem das Projekt erstellt wurde, muss im nächsten Schritt die Datenbankverbindung eingerichtet werden.

    - Hierzu muss die URL, der Username und das Passwort der vorher selbst erstellten PostgreSQL-Datenbank in der application.properties hinterlegt werden. Diese befindet sich unter /src/main/ressources/application.properties.

            spring.datasource.url = hier die URL zur Datenbank einfügen
            
            spring.datasource.username = hier den Username einfügen
            
            spring.datasource.password = hier das Passwort einfügen
            
            spring.jpa.hibernate.ddl-auto = create/update
            
            spring.sql.init.mode= always/never

- Das Feld spring.jpa.hibernate.ddl-auto steuert, wie die Datenbank bei Neustart der Anwendung initialisiert wird und spring.sql.init.mode gibt an, wann die Daten aus der data.sql in die Datenbank geschrieben werden.


- Initiale Daten: Ist die Datenbankverbindung korrekt eingerichtet, muss sich noch um das Erstellen von Initialdaten gekümmert werden. Es wichtig zu bemerken, dass die Anwendung mindestens einen Initialen Admin-User braucht, um funktionsfähig benutzt werden zu können. Die User-Accounts werden nach dem Registrieren gesperrt und müssen nachträglich von einem Administrator freigeschaltet werden.


  - Die Schritte zur korrekten Einrichtung initialer Daten lauten wie folgt:

    - In den application.properties bei der Variable spring.jpa.hibernate.ddl-auto den Wert von update à create setzen und den spring.sql.init.mode von never à always setzen.
    - Programm einmal ausführen, um Initialdaten zu laden.
    - spring.jpa.hibernate.ddl-auto wieder von create à update und spring.sql.init.mode von always à never setzen
    - Programm kann jetzt ohne Fehler mit initialen Daten in der Datenbank nutzen



Initialdaten werden über die DataSQL gesteuert die sich unter /src/main/ressources/data.sql befindet. Die ersten Zeilen Code erstellen hier einen Admin-User mit dem Usernamen „admin“ und dem Passwort „admin“, sowie die zugehörigen Rollen. Es ist zudem möglich weitere Initialdaten, bzw. Beispieldaten zu laden, wenn der Rest der DataSQL einkommentiert wird.


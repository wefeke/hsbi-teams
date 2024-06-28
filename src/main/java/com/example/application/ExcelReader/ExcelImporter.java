package com.example.application.ExcelReader;

import com.example.application.models.Teilnehmer;
import com.example.application.models.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.TeilnehmerService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Eine Klasse zum Importieren von Teilnehmerdaten aus einer Excel-Datei.
 *
 * @author Kennet
 */
public class ExcelImporter {

    private TeilnehmerService teilnehmerService;
    private User user;

    /**
     * Konstruktor für die ExcelImporter Klasse.
     *
     * @author Kennet
     * @param teilnehmerService Ein Service, der Methoden zur Interaktion mit Teilnehmer-Objekten in der Datenbank bereitstellt.
     * @param authenticatedUser Ein AuthenticatedUser-Objekt, das Informationen über den aktuell authentifizierten Benutzer enthält.
     */
    public ExcelImporter(TeilnehmerService teilnehmerService, AuthenticatedUser authenticatedUser) {
        this.teilnehmerService = teilnehmerService;
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            this.user = maybeUser.get();
        }
    }

    /**
     * Liest Teilnehmerdaten aus einer Excel-Datei und gibt eine Liste von Teilnehmer-Objekten zurück.
     * Jede Zeile in der Excel-Datei sollte einen Teilnehmer darstellen, wobei die erste Zelle die ID,
     * die zweite Zelle den Vornamen und die dritte Zelle den Nachnamen ist.
     * Wenn ein Teilnehmer mit der gleichen ID bereits in der Datenbank existiert, wird er zur Liste hinzugefügt.
     *
     * @param inputStream der InputStream der Excel-Datei
     * @return eine Liste von Teilnehmer-Objekten, die aus der Excel-Datei gelesen wurden
     * @throws Exception wenn ein Fehler beim Lesen der Excel-Datei oder bei der Verarbeitung der Daten auftritt
     * @author Kennet
     */
    public List<Teilnehmer> readOldTeilnehmerFromExcel(InputStream inputStream) throws Exception {
        List<Teilnehmer> teilnehmerList = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rows = sheet.iterator();

        if (rows.hasNext()) { // skip the header row
            rows.next();
        }

        while (rows.hasNext()) {
            Row currentRow = rows.next();
            // Assuming the first cell is vorname and the second cell is nachname
            Cell idCell = currentRow.getCell(0);
            Cell vornameCell = currentRow.getCell(1);
            Cell nachnameCell = currentRow.getCell(2);

            Long id = ((long) idCell.getNumericCellValue());
            String vorname = vornameCell.getStringCellValue();
            String nachname = nachnameCell.getStringCellValue();

            // Check if the Teilnehmer already exists in the database
            Optional<Teilnehmer> existingTeilnehmer = (teilnehmerService.findByMatrikelNr(id, user));

            if (existingTeilnehmer.isPresent()) {
                // If the Teilnehmer does already exist, add it to the list
                Teilnehmer teilnehmer = new Teilnehmer();
                teilnehmer.setId(id);
                teilnehmer.setVorname(vorname);
                teilnehmer.setNachname(nachname);
                teilnehmerList.add(teilnehmer);
            }
        }

        workbook.close();
        return teilnehmerList;
    }

    /**
     * Liest Teilnehmerdaten aus einer Excel-Datei und gibt eine Liste von Teilnehmer-Objekten zurück.
     * Jede Zeile in der Excel-Datei sollte einen Teilnehmer darstellen, wobei die erste Zelle die ID,
     * die zweite Zelle den Vornamen und die dritte Zelle den Nachnamen ist.
     * Wenn ein Teilnehmer mit der gleichen ID bereits in der Datenbank existiert, wird er übersprungen.
     * Wenn ein Teilnehmer mit dem gleichen Vornamen und Nachnamen, aber einer anderen ID existiert, wird eine Nummer an den Nachnamen angehängt.
     *
     * @author Kennet
     * @param inputStream der InputStream der Excel-Datei
     * @return eine Liste von Teilnehmer-Objekten, die aus der Excel-Datei gelesen wurden
     * @throws Exception wenn ein Fehler beim Lesen der Excel-Datei oder bei der Verarbeitung der Daten auftritt
     */
    public List<Teilnehmer> readNewTeilnehmerFromExcel(InputStream inputStream) throws Exception {
        List<Teilnehmer> teilnehmerList = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rows = sheet.iterator();

        if (rows.hasNext()) { // skip the header row
            rows.next();
        }

        while (rows.hasNext()) {
            Row currentRow = rows.next();
            // Assuming the first cell is vorname and the second cell is nachname
            Cell idCell = currentRow.getCell(0);
            Cell vornameCell = currentRow.getCell(1);
            Cell nachnameCell = currentRow.getCell(2);

            Long id = ((long) idCell.getNumericCellValue());
            String vorname = vornameCell.getStringCellValue();
            String nachname = nachnameCell.getStringCellValue();

            // Check if the Teilnehmer already exists in the database
            Optional<Teilnehmer> existingTeilnehmer = (teilnehmerService.findByMatrikelNr(id, user));

            if (existingTeilnehmer.isPresent()) {
                //
            }
            else {
                // If the Teilnehmer does not exist, add it to the list
                Teilnehmer teilnehmer = new Teilnehmer();
                teilnehmer.setId(id);
                teilnehmer.setVorname(vorname);
                teilnehmer.setNachname(nachname);

                // Check if there is another Teilnehmer with the same Vorname and Nachname but different ID
                Optional<Teilnehmer> sameNameTeilnehmer = teilnehmerService.findTeilnehmerByVornameAndNachname(vorname, nachname, user);
                if (sameNameTeilnehmer.isPresent()) {
                    // If there is, append a number to the Vorname
                    int count = 1;
                    String newNachname = nachname + "(" + count + ")";
                    while (teilnehmerService.findTeilnehmerByVornameAndNachname(vorname, newNachname, user).isPresent()) {
                        // If the new Vorname is also taken, increment the number
                        count++;
                        newNachname = nachname + "(" + count + ")";
                    }
                    teilnehmer.setNachname(newNachname);
                }
                teilnehmerList.add(teilnehmer);
            }
        }

        workbook.close();
        return teilnehmerList;
    }

    /**
     * Liest Teilnehmerdaten aus einer Excel-Datei und gibt eine Liste von Teilnehmer-Objekten zurück.
     * Jede Zeile in der Excel-Datei sollte einen Teilnehmer darstellen, wobei die erste Zelle die ID,
     * die zweite Zelle den Vornamen und die dritte Zelle den Nachnamen ist.
     * Alle Teilnehmer, unabhängig davon, ob sie bereits in der Datenbank existieren oder nicht, werden zur Liste hinzugefügt.
     *
     * @param inputStream der InputStream der Excel-Datei
     * @return eine Liste von Teilnehmer-Objekten, die aus der Excel-Datei gelesen wurden
     * @throws Exception wenn ein Fehler beim Lesen der Excel-Datei oder bei der Verarbeitung der Daten auftritt
     * @author Kennet
     */
    public List<Teilnehmer> readAllTeilnehmerFromExcel(InputStream inputStream) throws Exception {
        List<Teilnehmer> teilnehmerList = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rows = sheet.iterator();

        if (rows.hasNext()) {
            rows.next();
        }

        while (rows.hasNext()) {
            Row currentRow = rows.next();

            Cell idCell = currentRow.getCell(0);
            Cell vornameCell = currentRow.getCell(1);
            Cell nachnameCell = currentRow.getCell(2);

            Long id = ((long) idCell.getNumericCellValue());
            String vorname = vornameCell.getStringCellValue();
            String nachname = nachnameCell.getStringCellValue();

            Teilnehmer teilnehmer = new Teilnehmer();
            teilnehmer.setId(id);
            teilnehmer.setVorname(vorname);
            teilnehmer.setNachname(nachname);
            teilnehmerList.add(teilnehmer);

        }

        workbook.close();
        return teilnehmerList;
    }
}



package com.example.application.ExcelReader;

import com.example.application.models.Teilnehmer;
import com.example.application.models.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.TeilnehmerService;
import com.vaadin.flow.component.notification.Notification;
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

public class ExcelImporter {

    private TeilnehmerService teilnehmerService;
    private List<Teilnehmer> teilnehmerList = new ArrayList<>();
    private User user;

    public ExcelImporter(TeilnehmerService teilnehmerService, AuthenticatedUser authenticatedUser) {
        this.teilnehmerService = teilnehmerService;
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            this.user = maybeUser.get();
        }
    }

    public List<Teilnehmer> readTeilnehmerFromExcel(InputStream inputStream) throws Exception {

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
            System.out.println("existingTeilnehmer: " + existingTeilnehmer.toString());

            if (existingTeilnehmer.isPresent()) {
                //nichts
            }
            else {
                // If the Teilnehmer does not exist, add it to the list
                Teilnehmer teilnehmer = new Teilnehmer();
                teilnehmer.setId(id);
                teilnehmer.setVorname(vorname);
                teilnehmer.setNachname(nachname);
                System.out.println("Added Teilnehmer: " + teilnehmer.toString());

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
                    System.out.println("Increased Nachname: " + teilnehmer.toString());
                }
                Notification.show("Teilnehmer hinzugefügt: " + teilnehmer.toString());
                teilnehmerList.add(teilnehmer);
            }
        }

        workbook.close();
        return teilnehmerList;
    }
}



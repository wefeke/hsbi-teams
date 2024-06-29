package com.example.application.views.auswertung;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

/**
 * Eine Klasse zum Exportieren von der Auswertung im Excel-Format.
 *
 * @author Leon
 */
@Service
public class AuswertungExcelExporter {
    private final XSSFWorkbook workbook;
    private final XSSFSheet sheet;
    private List<Auswertung> auswertungen;

    /**
     * Konstruktor für den AuswertungExcelExporter.
     * Initialisiert die Arbeitsmappe (Workbook) für die Excel-Datei.
     *
     * @author Leon
     */
    public AuswertungExcelExporter() {
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Auswertung" );
    }

    /**
     * Schreibt die Kopfzeile der Excel-Tabelle.
     * Erstellt ein neues Blatt, setzt den Stil für die Zellen und fügt die Kopfzeilen hinzu.
     *
     * @author Leon
     */
    private int writeHeaderLine(int startRow) {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        int columnCount = 0;

        // Fügt den Titel hinzu, hier Gruppenzuordnung
        Row titelGruppe = sheet.createRow(startRow++);
        createCell(titelGruppe, 0, "Gruppenzuordnung", style);

        // Nimmmt die nächste Zeile und fügt die Überschriften hinzu für die jeweiligen Zeilen
        Row row = sheet.createRow(startRow);

        Auswertung auswertung = auswertungen.getFirst();
        createCell(row, columnCount++, "Matrikelnummer", style);
        createCell(row, columnCount++, "Vorname", style);
        createCell(row, columnCount++, "Nachname", style);
        createCell(row, columnCount++, "Gruppenanzahl/Gesamtpunkte", style);
        startRow++;
        for (TGGPHelper tggpHelper : auswertung.getTggpHelper()) {
            createCell(row, columnCount++, tggpHelper.getTerminAndGruppenarbeit(), style);
        }


        return startRow;
    }

    /**
     * Erstellt eine Zelle in einer Zeile der Excel-Tabelle.
     * Setzt den Wert und den Stil der Zelle basierend auf dem Datentyp.
     *
     * @param row die Zeile, in der die Zelle erstellt wird
     * @param columnCount die Spaltennummer der Zelle
     * @param value der Wert, der in die Zelle geschrieben wird
     * @param style der Stil der Zelle
     *
     * @author Leon
     */
    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        switch (value) {
            case Integer i -> cell.setCellValue(i);
            case Boolean b -> cell.setCellValue(b);
            case Long l -> cell.setCellValue(l);
            case Float v -> cell.setCellValue(v);
            case null, default -> cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    /**
     * Schreibt die Datenzeilen in die Excel-Tabelle.
     * Iteriert über die Auswertungen und erstellt für jede Auswertung eine Zeile mit den entsprechenden Daten.
     *
     * @author Leon
     */
    private int writeDataLines(int option, int rowCount) { // int option gibt an,
        // ob nun die Punkte oder die Gruppen Daten geschrieben werden

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);


    if (option == 0) { // Gruppen sollen geschrieben werden
        for (Auswertung auswertung : auswertungen) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, auswertung.getMatrikelnummer(), style);
            createCell(row, columnCount++, auswertung.getNachname(), style);
            createCell(row, columnCount++, auswertung.getVorname(), style);

            createCell(row, columnCount++, auswertung.getGesamtGruppenarbeiten(), style);
            for (TGGPHelper ignored : auswertung.getTggpHelper()) {
                createCell(row, columnCount++, auswertung.getTggHelperValuesGruppe(), style);
            }
        }
        // Fügt eine Leerzeile hinzu
        Row leerzeile = sheet.createRow(rowCount++);
        createCell(leerzeile, 0, "", style);

    } else if (option == 1) { // Punkte sollen geschrieben werden
        for (Auswertung auswertung : auswertungen) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, auswertung.getMatrikelnummer(), style);
            createCell(row, columnCount++, auswertung.getNachname(), style);
            createCell(row, columnCount++, auswertung.getVorname(), style);

            createCell(row, columnCount++, auswertung.getGesamtPunkte(), style);
            for (TGGPHelper ignored : auswertung.getTggpHelper()) {
                createCell(row, columnCount++, auswertung.getTggHelperValuesPunkte(), style);
            }

        }

        // Fügt eine Leerzeile hinzu
        Row leerzeile = sheet.createRow(rowCount++);
        createCell(leerzeile, 0, "", style);
    }
        return rowCount;
    }

    /**
     * Exportiert die Auswertungen als Excel-Datei.
     * Schreibt die Kopfzeile und die Datenzeilen in die Excel-Tabelle und gibt die Datei als Byte-Array zurück.
     *
     * @param auswertungen die Liste der Auswertungen, die exportiert werden sollen
     * @return ein Byte-Array, das die Excel-Datei darstellt
     * @throws IOException wenn ein Fehler beim Schreiben der Datei auftritt
     *
     * @author Leon
     */
    public byte[] export(List<Auswertung> auswertungen) throws IOException {
        this.auswertungen = auswertungen;
        int currentRow = writeHeaderLine(0);
        currentRow = writeDataLines(0,currentRow); // Erst die Option angeben, dann die aktuelle Row
        currentRow = writeHeaderLine(currentRow); // Da Titel für den Abschnitt (Gruppenzuordnung und Spaltenüberschriften zwei Zeilen sind)
        writeDataLines(1, currentRow);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (bos) {
            workbook.write(bos);
        }
        return bos.toByteArray();
    }
}

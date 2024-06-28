package com.example.application.ExcelReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import com.example.application.models.Teilnehmer;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

/**
 * Eine Klasse zum Exportieren von Teilnehmerdaten im Excel-Format.
 *
 * @author Kennet
 */
@Service
public class TeilnehmerExcelExporter {
    private final XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Teilnehmer> teilnehmer;

    /**
     * Konstruktor für die TeilnehmerExcelExporter Klasse.
     * Initialisiert das Workbook-Objekt.
     *
     * @author Kennet
     */
    public TeilnehmerExcelExporter() {
        workbook = new XSSFWorkbook();
    }

    /**
     * Erstellt die Kopfzeile der Excel-Datei.
     *
     *  @author Kennet
     */
    private void writeHeaderLine() {
        sheet = workbook.createSheet("Teilnehmer");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "ID", style);
        createCell(row, 1, "Vorname", style);
        createCell(row, 2, "Nachname", style);
    }

     /**
     * Erstellt eine Zelle in der angegebenen Zeile und Spalte mit dem angegebenen Wert und Stil.
     *
      * @author Kennet
     * @param row Die Zeile, in der die Zelle erstellt werden soll.
     * @param columnCount Die Spalte, in der die Zelle erstellt werden soll.
     * @param value Der Wert, der in die Zelle geschrieben werden soll.
     * @param style Der Stil, der auf die Zelle angewendet werden soll.
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
     * Schreibt die Datenzeilen in die Excel-Datei.
     *
     * @author Kennet
     */
    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (Teilnehmer teilnehmer : teilnehmer) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount++, teilnehmer.getId(), style);
            createCell(row, columnCount++, teilnehmer.getVorname(), style);
            createCell(row, columnCount++, teilnehmer.getNachname(), style);
        }
    }

    /**
     * Exportiert die angegebene Liste von Teilnehmer-Objekten in eine Excel-Datei und gibt diese als byte-Array zurück.
     *
     * @author Kennet
     * @param teilnehmer Die Liste von Teilnehmer-Objekten, die in die Excel-Datei exportiert werden sollen.
     * @return Ein byte-Array, das die erstellte Excel-Datei repräsentiert.
     * @throws IOException Wenn ein Fehler beim Schreiben in das ByteArrayOutputStream auftritt.
     */
    public byte[] export(List<Teilnehmer> teilnehmer) throws IOException {
        this.teilnehmer = teilnehmer;
        writeHeaderLine();
        writeDataLines();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (bos) {
            workbook.write(bos);
        }
        return bos.toByteArray();
    }
}



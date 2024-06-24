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

// LEON
@Service
public class AuswertungExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Auswertung> auswertungen;

    /**
     * Konstruktor für den AuswertungExcelExporter.
     * Initialisiert die Arbeitsmappe (Workbook) für die Excel-Datei.
     *
     * @autor Leon
     */
    public AuswertungExcelExporter() {
        workbook = new XSSFWorkbook();
    }

    /**
     * Schreibt die Kopfzeile der Excel-Tabelle.
     * Erstellt ein neues Blatt, setzt den Stil für die Zellen und fügt die Kopfzeilen hinzu.
     *
     * @autor Leon
     */
    private void writeHeaderLine() {
        sheet = workbook.createSheet("Auswertung");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        int columnCount = 1;
        Auswertung auswertung = auswertungen.get(0);
        createCell(row, 0, "Matrikelnummer", style);
        for (TGGPHelper tggpHelper : auswertung.getTggpHelper()) {
            createCell(row, columnCount++, tggpHelper.getTerminAndGruppenarbeit(), style);
        }

        createCell(row, columnCount, "Gesamtpunkte und Anzahl Gruppenarbeiten", style);
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
     * @autor Leon
     */
    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Float) {
            cell.setCellValue((Float) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    /**
     * Schreibt die Datenzeilen in die Excel-Tabelle.
     * Iteriert über die Auswertungen und erstellt für jede Auswertung eine Zeile mit den entsprechenden Daten.
     *
     * @autor Leon
     */
    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (Auswertung auswertung : auswertungen) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, auswertung.getNameMatrikelnummer(), style);
            for (TGGPHelper tggpHelper : auswertung.getTggpHelper()) {
                createCell(row, columnCount++, auswertung.getTggHelperValues(), style);
            }
            createCell(row, columnCount++, auswertung.getGesamtPunkteAndGruppenarbeiten(), style);
        }
    }

    /**
     * Exportiert die Auswertungen als Excel-Datei.
     * Schreibt die Kopfzeile und die Datenzeilen in die Excel-Tabelle und gibt die Datei als Byte-Array zurück.
     *
     * @param auswertungen die Liste der Auswertungen, die exportiert werden sollen
     * @return ein Byte-Array, das die Excel-Datei darstellt
     * @throws IOException wenn ein Fehler beim Schreiben der Datei auftritt
     *
     * @autor Leon
     */
    public byte[] export(List<Auswertung> auswertungen) throws IOException {
        this.auswertungen = auswertungen;
        writeHeaderLine();
        writeDataLines();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
        } finally {
            bos.close();
        }
        return bos.toByteArray();
    }
}

package com.example.application.views.test;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import com.example.application.models.Auswertung;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class StudierendeExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Auswertung> auswertungen;

    public StudierendeExcelExporter() {
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Auswertung");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "Matrikelnummer", style);
        createCell(row, 1, "Name", style);
        createCell(row, 2, "Gruppenarbeit", style);
        createCell(row, 3, "Veranstaltung", style);
        createCell(row, 4, "Punkte", style);

    }

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

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (Auswertung auswertung : auswertungen) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount++, auswertung.getMatrikelnummer(), style);
            createCell(row, columnCount++, auswertung.getName(), style);
            createCell(row, columnCount++, auswertung.getGruppenarbeit(), style);
            createCell(row, columnCount++, auswertung.getVeranstaltung(), style);
            createCell(row, columnCount++, auswertung.getPunkte(), style);
        }
    }

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
        return  bos.toByteArray();
    }
}



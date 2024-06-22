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

    public AuswertungExcelExporter() {
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
        int columnCount=1;
        Auswertung auswertung = auswertungen.getFirst();
        createCell(row, 0, "Matrikelnummer", style);
        for (TGGPHelper tggpHelper : auswertung.getTggpHelper()) {
            createCell(row, columnCount++, tggpHelper.getTerminAndGruppenarbeit(), style);
        }

        createCell(row, columnCount, "Gesamtpunkte", style);

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
            // Eine einzelne Auswertung nehmen und alle Gruppenarbeiten als Columns darstellen


            createCell(row, columnCount++, auswertung.getNameMatrikelnummer(), style);
            for (TGGPHelper tggpHelper : auswertung.getTggpHelper()) {
                createCell(row, columnCount++, auswertung.getTggHelperValues(), style);
            }
            createCell(row, columnCount++, auswertung.getGesamtPunkte(), style);

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



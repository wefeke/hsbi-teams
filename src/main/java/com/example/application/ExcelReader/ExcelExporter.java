//package com.example.application.ExcelReader;
//
//import com.example.application.models.Teilnehmer;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.springframework.stereotype.Service;
//
//import java.io.ByteArrayOutputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.List;
//
//@Service
//public class ExcelExporter {
//
//    public void exportTeilnehmerListe(List<Teilnehmer> teilnehmerList, String dateipfad, String currentUserId) {
//        try (Workbook workbook = new XSSFWorkbook()) {
//            Sheet sheet = workbook.createSheet("Teilnehmer");
//
//            Row headerRow = sheet.createRow(0);
//            headerRow.createCell(0).setCellValue("ID");
//            headerRow.createCell(1).setCellValue("Vorname");
//            headerRow.createCell(2).setCellValue("Nachname");
//
//            int rowIndex = 1;
//            for (Teilnehmer teilnehmer : teilnehmerList) {
//                Row row = sheet.createRow(rowIndex++);
//                row.createCell(0).setCellValue(teilnehmer.getId());
//                row.createCell(1).setCellValue(teilnehmer.getVorname());
//                row.createCell(2).setCellValue(teilnehmer.getNachname());
//            }
//            try {
//                Path path = Paths.get(dateipfad);
//                if (!Files.exists(path.getParent())) {
//                    Files.createDirectories(path.getParent());
//                }
//                FileOutputStream outputStream = new FileOutputStream(path.toFile());
//                workbook.write(outputStream);
//
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public byte[] export( Workbook workbook) throws IOException {
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            try {
//                workbook.write(bos);
//            }
//            finally {
//                bos.close();
//            }
//            return bos.toByteArray();
//    }
//}
//

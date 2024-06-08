//package com.example.application.services;
//
//
//import com.example.application.models.Teilnehmer;
//import com.example.application.repositories.TeilnehmerRepository;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//@Service
//public class ImportService {
//
//
//    private final TeilnehmerService teilnehmerService;
//
//    public ImportService(TeilnehmerService teilnehmerService) {
//        this.teilnehmerService = teilnehmerService;
//    }
//
//    public List<Teilnehmer> loadExcelData(String filePath) {
//        List<Teilnehmer> teilnehmerList = new ArrayList<>();
//
//        try (FileInputStream fis = new FileInputStream(filePath);
//             Workbook workbook = new XSSFWorkbook(fis)) {
//
//            Sheet sheet = workbook.getSheetAt(0);
//            Iterator<Row> rows = sheet.iterator();
//
//            while (rows.hasNext()) {
//                Row row = rows.next();
//                if (row.getRowNum() == 0) { // Überspringen der Kopfzeile
//                    continue;
//                }
//
//                Teilnehmer teilnehmer = new Teilnehmer();
//                teilnehmer.setId((long) row.getCell(0).getNumericCellValue());
//                teilnehmer.setVorname(row.getCell(1).getStringCellValue());
//                teilnehmer.setNachname(row.getCell(2).getStringCellValue());
//
//                teilnehmerList.add(teilnehmer);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return teilnehmerList;
//    }
//
//    public void saveTeilnehmerList(List<Teilnehmer> teilnehmerList) {
//        for (Teilnehmer teilnehmer : teilnehmerList) {
//            teilnehmerService.saveTeilnehmer(teilnehmer, null); //hier muss noch der User übergeben werden
//        }
//    }
//        }


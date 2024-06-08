//package com.example.application.ExcelReader;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import com.vaadin.flow.component.board.Row;
//import org.apache.commons.compress.utils.FileNameUtils;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//public abstract class AbstractExcelReader <T> {
//
//    private static final String XLS_DATEIERWEITERUNG = ".xls";
//    private static final String XLSX_DATEIERWEITERUNG = ".xlsx";
//
//    protected abstract String getZulesendesExcelBlatt();
//
//    protected abstract T mappeZeileZuObjekt(Workbook workbook, Row zeile);
//
//    public List<T> ermittleExcelZeilen(String dateipfad) {
//
//        List<T> excelZeilen = new ArrayList<>();
//        Workbook workbook = oeffneDatei(dateipfad);
//        Sheet sheet = workbook.getSheet(getZulesendesExcelBlatt());
//
//        Iterator<Row> zeilenIterator = sheet.iterator();
//        //erste Seite wird ignoriert
//        Row inhaltZeilen = iterator.next();
//
//        while (iterator.hasNext()) {
//            Row zeile = iterator.next();
//            T importDaten = mappeZeileZuObjekt(workbook, inhaltZeilen);
//            if (importDaten != null) {
//                excelZeilen.add(importDaten);
//            }
//        }
//
//        return excelZeilen;
//    }
//
//    private Workbook oeffneDatei (String dateipfad){
//        File excelDatei = new File(dateipfad);
//        FileInputStream excelInput = null;
//        Workbook workbook = null;
//
//        try {
//            excelInput = new FileInputStream(excelDatei);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    String excelDateiendung = FileNameUtils.getExtension(excelDatei.getName());
//    try{
//        if(XLS_DATEIERWEITERUNG.equalsIgnoreCase(excelDateiendung)){
//            workbook = new HSSFWorkbook(excelInput);
//        } else if (XLSX_DATEIERWEITERUNG.equalsIgnoreCase(excelDateiendung)){
//            workbook = new XSSFWorkbook(excelInput);
//        } else {
//            throw new IllegalArgumentException("Datei ist kein Excel-Dokument");
//        }
//    } catch (IOException e){
//        e.printStackTrace();
//    }
//    return workbook;
//    }
//
//
//
//
////        File datei = new File(dateipfad);
////        if (!datei.exists()) {
////            throw new IllegalArgumentException("Datei existiert nicht");
////        }
////        if (datei.getName().endsWith(XLS_DATEIERWEITERUNG)) {
////            return ermittleExcelZeilenXLS(datei);
////        } else if (datei.getName().endsWith(XLSX_DATEIERWEITERUNG)) {
////            return ermittleExcelZeilenXLSX(datei);
////        } else {
////            throw new IllegalArgumentException("Datei ist kein Excel-Dokument");
////        }
//
//}

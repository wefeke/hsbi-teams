//package com.example.application.ExcelReader;
//
//import com.example.application.models.Teilnehmer;
//import com.example.application.services.ImportService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.List;
//
//@RestController
//@RequestMapping("/import")
//public class ImportController {
//
//    @Autowired
//    private ImportService importService;
//
//    @PostMapping("/preview")
//    public List<Teilnehmer> previewExcelFile(@RequestParam("file") MultipartFile file) {
//        try {
//            // Speichern der Datei lokal
//            File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
//            file.transferTo(convFile);
//
//            // Laden der Daten
//            return importService.loadExcelData(convFile.getAbsolutePath());
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    @PostMapping("/confirm")
//    public String confirmImport(@RequestBody List<Teilnehmer> teilnehmerList) {
//        importService.saveTeilnehmerList(teilnehmerList);
//        return "Daten erfolgreich importiert!";
//    }
//}

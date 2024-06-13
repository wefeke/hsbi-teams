package com.example.application.ExcelReader;

import com.example.application.models.Teilnehmer;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;

public class TeilnehmerReader extends AbstractExcelReader<Teilnehmer>{

    private static final String TEILNEHMER_SHEETNAME = "Teilnehmer";

    @Override
    protected String getZulesendesExcelBlatt() {

        return TEILNEHMER_SHEETNAME;
    }

    @Override
    protected Teilnehmer mappeZeileZuObjekt(Workbook workbook, Row zeile) {

        Teilnehmer teilnehmer = new Teilnehmer();

        Cell idZelle =zeile.getCell(Teilnehmerspalten.ID.spaltenindex);
        Cell vornameZelle = zeile.getCell(Teilnehmerspalten.VORNAME.spaltenindex);
        Cell nachnameZelle = zeile.getCell(Teilnehmerspalten.NACHNAME.spaltenindex);

        teilnehmer.setId((long) idZelle.getNumericCellValue());
        teilnehmer.setVorname(vornameZelle.getStringCellValue());
        teilnehmer.setNachname(nachnameZelle.getStringCellValue());

        return new Teilnehmer();
    }

    private enum Teilnehmerspalten{
        ID("A"),
        NACHNAME("B"),
        VORNAME("C");

        private final int spaltenindex;
        Teilnehmerspalten(String spalte){

            this.spaltenindex = CellReference.convertColStringToIndex(spalte);
        }
    }
}

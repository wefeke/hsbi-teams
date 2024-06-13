package com.example.application.views.studierende;
import com.example.application.ExcelReader.TeilnehmerReader;
import com.example.application.models.Teilnehmer;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.io.InputStream;
import java.util.List;

@Route("import")
public class ImportView extends VerticalLayout {

    private final TeilnehmerReader reader = new TeilnehmerReader();

    public ImportView() {
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.addSucceededListener(event -> {
            InputStream inputStream = buffer.getInputStream();
            List<Teilnehmer> teilnehmerList = reader.ermittleExcelZeilen(inputStream.toString());
            showDataInDialog(teilnehmerList);
        });

        add(upload);
    }

    private void showDataInDialog(List<Teilnehmer> data) {
        Dialog dialog = new Dialog();
        Grid<Teilnehmer> grid = new Grid<>(Teilnehmer.class);
        grid.setItems(data);
        dialog.add(grid);
        dialog.open();
    }
}


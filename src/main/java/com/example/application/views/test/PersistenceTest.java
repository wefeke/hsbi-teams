package com.example.application.views.test;

import com.example.application.models.Test;
import com.example.application.services.TestService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("persistenceTest")
public class PersistenceTest extends VerticalLayout {

    private TestService testService;
    TextField id = new TextField("Test ID");
    TextField name = new TextField("Test Name");

    Button save = new Button("Save");

    public PersistenceTest(TestService testService) {
        this.testService = testService;

        add(
                id,
                name,
                save
        );

        save.addClickListener(e -> saveTest());
    }

    private void saveTest() {
        String temp_id = id.getValue();
        String temp_name = name.getValue();

        if (id != null && !id.isEmpty() && name != null && !name.isEmpty()) {
            Test test = new Test();
            test.settestid(Integer.parseInt(id.getValue()));
            test.settestname(name.getValue());

            testService.saveTest(test);

            id.clear();
            name.clear();
        }
    }

}

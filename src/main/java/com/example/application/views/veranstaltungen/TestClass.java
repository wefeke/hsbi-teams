package com.example.application.views.veranstaltungen;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;

public class TestClass extends Dialog {

    public TestClass() {
        Button cancelButton = new Button("Cancel", event -> this.close());
        add(cancelButton);

        this.getFooter().add();
    }
}



package com.sm.study_manager;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;

public class CheckBoxListCell extends ListCell<String> {
    private final CheckBox checkBox = new CheckBox();

    public CheckBoxListCell() {
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        setGraphic(checkBox);
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty && item != null) {
            checkBox.setText(item);
            setGraphic(checkBox);
        } else {
            setGraphic(null);
        }
    }
}
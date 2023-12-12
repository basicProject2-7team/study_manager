package com.sm.study_manager;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;

// 그 체크박스 리스트 UI 만드느라 지피티한테 부탁했더니 일단 이렇게 ,,,,
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
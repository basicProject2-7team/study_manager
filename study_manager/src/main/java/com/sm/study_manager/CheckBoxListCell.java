package com.sm.study_manager;

//import javafx.beans.property.BooleanProperty;
//import javafx.beans.property.SimpleBooleanProperty;
//import javafx.scene.control.CheckBox;
//import javafx.scene.control.ContentDisplay;
//import javafx.scene.control.ListCell;
//
//
//public class CheckBoxListCell extends ListCell<String> {
//    private final CheckBox checkBox = new CheckBox();
//
//    public CheckBoxListCell() {
//        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
//        setGraphic(checkBox);
//    }
//
//    @Override
//    protected void updateItem(String item, boolean empty) {
//        super.updateItem(item, empty);
//        if (!empty && item != null) {
//            checkBox.setText(item);
//            setGraphic(checkBox);
//        } else {
//            setGraphic(null);
//        }
//    }
//}

import com.sm.study_manager.ListItem;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;

public class CheckBoxListCell extends ListCell<ListItem> {
    private final CheckBox checkBox = new CheckBox(); // 셀마다 CheckBox 생성

    public CheckBoxListCell() {
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        setGraphic(checkBox);
        checkBox.setOnAction(e -> getItem().setChecked(checkBox.isSelected())); // CheckBox의 상태 변경시 ListItem 업데이트
    }

    @Override
    protected void updateItem(ListItem item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty && item != null) {
            checkBox.setText(item.getText()); // CheckBox의 텍스트 설정
            checkBox.selectedProperty().bindBidirectional(item.checkedProperty()); // CheckBox의 선택 상태와 ListItem의 선택 상태 연결
            setGraphic(checkBox); // CheckBox를 셀의 그래픽으로 설정
        } else {
            setGraphic(null); // 빈 셀의 경우 CheckBox를 표시하지 않음
        }
    }
}
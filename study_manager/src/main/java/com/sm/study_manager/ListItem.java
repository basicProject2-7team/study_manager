package com.sm.study_manager;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

// 그 체크박스 리스트 UI 만드느라 지피티한테 부탁했더니 일단 이렇게 ,,,,
public class ListItem {
    private final String text; // 텍스트를 저장하는 변수
    private final BooleanProperty checked; // CheckBox의 선택 상태를 저장하는 변수

    // 생성자
    public ListItem(String text) {
        this.text = text;
        this.checked = new SimpleBooleanProperty(false); // 초기 상태는 선택되지 않음(false)
    }

    // 여기서부터는 표준 getter와 setter 메서드
    public String getText() {
        return text;
    }
    // test 주석
    public BooleanProperty checkedProperty() {
        return checked;
    }

    public boolean isChecked() {
        return checked.get();
    }

    public void setChecked(boolean checked) {
        this.checked.set(checked);
    }

    @Override
    public String toString() {
        return text; // ListItem의 텍스트 (파일 이름) 반환
    }
    
    // 뭐야
}

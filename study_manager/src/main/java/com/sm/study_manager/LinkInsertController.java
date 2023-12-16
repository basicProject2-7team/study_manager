package com.sm.study_manager;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LinkInsertController {
    private MusicController musicController; // MusicController에 대한 참조
    private DBConnector dbConnector = new DBConnector();    // db 에 제목과 유튜브 링크 넣어야함

    @FXML
    private Button insertButton;    // 추가버튼

    @FXML
    private Button cancelButton;    //닫기

    @FXML
    private TextField nameTextInput;    //제목

    @FXML
    private TextField linkTextInput;    //링크

    public void setMusicController(MusicController musicController) {
        this.musicController = musicController;
    }
    @FXML
    private void insert() {
        String name = nameTextInput.getText();
        String link = linkTextInput.getText();

        boolean insertSuccessful = dbConnector.insertLinkItem(name, link);
        if (insertSuccessful) {
            System.out.println("Name: " + name + ", Link: " + link);
            musicController.loadYoutubeLinks(); // MusicController의 메서드 호출하여 ListView 업데이트
        } else {
            System.out.println("링크 추가 실패");
        }
    }

    @FXML
    private void cancel() {
        // 현재 Stage를 찾기 위해 씬을 가져옵니다 (어느 컨트롤을 사용해도 괜찮지만, 여기서는 cancelButton을 사용합니다).
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        // 해당 Stage를 닫습니다.
        stage.close();
    }

    
}
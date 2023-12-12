package com.sm.study_manager;

// 모달창 리스트뷰를 컨트롤 ,, 버튼 누르면 창꺼지게 해놓자 일단.

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDateTime;
//import javafx.scene.control.cell.CheckBoxListCell;
// 이 체크박스 아니고
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

public class TimerStartModalController {

    // listView ??
    @FXML
    private ListView<String> listView; // FXML에서 정의한 ListView와 연결

    @FXML
    private Button selectButton;

    MediaPlayer mediaPlayer;
    // 메디아 플레이어
    @FXML
    public void initialize() {
        // ListView에 CheckBoxListCell을 사용하도록 설정
        listView.setCellFactory(lv -> new CheckBoxListCell());

        // 예시 데이터를 ListView에 추가
        listView.getItems().addAll("lol_madmovie_music.mp3", "another_song.mp3", "more_music.mp3",
                "lol_madmovie_music.mp3","lol_madmovie_music.mp3","lol_madmovie_music.mp3","lol_madmovie_music.mp3",
                "lol_madmovie_music.mp3","lol_madmovie_music.mp3","lol_madmovie_music.mp3","lol_madmovie_music.mp3",
                "lol_madmovie_music.mp3","lol_madmovie_music.mp3","lol_madmovie_music.mp3","lol_madmovie_music.mp3",
                "lol_madmovie_music.mp3");
        // 실제 애플리케이션에서는 파일 목록을 동적으로 가져오거나 사용자 입력을 받을 수 있습니다.

        // 이건 로컬에서 갖고오기 ??
    }


    @FXML
    private void selectStart(ActionEvent event) {  // 시작버튼 이벤트 핸들러
        // 음악 파일 선택한거 재생 어떻게하지,,?


        // 재생 하는 거
        String fileName = "text";

        playHitSound(fileName);


        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();

        // 종료만

    }

    private void playHitSound(String fileName){
        URL resource = getClass().ClassLoader.getResource();
        if (resource == null) {
            System.err.println("File not found: " + fileName);
            return;
        }
        String path = resource.getPath();
        Media media = new Media(new File(path).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();
    }
}
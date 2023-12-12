package com.sm.study_manager;

// 모달창 리스트뷰를 컨트롤 ,, 버튼 누르면 창꺼지게 해놓자 일단.

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import javafx.stage.Stage;

import java.io.File;
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

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    // 메디아 플레이어
    @FXML
    public void initialize() {
        // ListView에 CheckBoxListCell을 사용하도록 설정
        listView.setCellFactory(lv -> new CheckBoxListCell());  // lv 가 뭔지모르겠음 아 list view ?

        // 예시 데이터를 ListView에 추가
        listView.getItems().addAll("lol_madmovie_music.mp3", "another_song.mp3", "more_music.mp3",
                "lol_madmovie_music.mp3","lol_madmovie_music.mp3","lol_madmovie_music.mp3","lol_madmovie_music.mp3",
                "lol_madmovie_music.mp3","lol_madmovie_music.mp3","lol_madmovie_music.mp3","lol_madmovie_music.mp3",
                "lol_madmovie_music.mp3","lol_madmovie_music.mp3","lol_madmovie_music.mp3","lol_madmovie_music.mp3",
                "lol_madmovie_music.mp3");
        // 실제 애플리케이션에서는 파일 목록을 동적으로 가져오거나 사용자 입력을 받을 수 있습니다.

        // 이건 로컬에서 갖고오기 ??

        // 이코드 편집해서 resources 폴더안에 music 폴더안에 파일들의 이름을 위처럼 갖고오는 코드
    }


    @FXML
    private void selectStart(ActionEvent event) {  // 시작버튼 눌렀을때!!!
        // 음악 파일 선택한거 재생 어떻게하지,,?



        // 재생 하는 거
        String fileName = "music/infinityJourney.mp3";

        playHitSound(fileName);


        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();

        // 창닫히기

    }

    private void playHitSound(String fileName){
        URL resource = getClass().getClassLoader().getResource(fileName);
        System.out.println(resource);
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

    public void shutDown(MediaPlayer mediaPlayer){
        mediaPlayer.stop();
        mediaPlayer.dispose();
        mediaPlayer = null;
        // 메모리해제까지 해보자
    }




}
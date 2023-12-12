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

//import java.io.File;
//import java.net.URISyntaxException;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

// 파일입출력으로 폴더에있는 파일명 리스트에 추가.
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

    private void loadMusicFiles() {
        String userHome = System.getProperty("user.home"); // 현재 사용자의 홈 디렉토리 경로를 가져옵니다.
        Path musicDirectory = Paths.get(userHome, "music"); // 사용자의 홈 디렉토리 내에 있는 'music' 폴더의 경로를 생성합니다.

        try {
            List<String> fileNames = Files.list(musicDirectory) // 'music' 디렉토리 내의 모든 파일을 Stream으로 가져옵니다.
                    .filter(Files::isRegularFile) // Stream에서 일반 파일(디렉토리가 아닌 파일)만 필터링합니다.
                    .map(path -> path.getFileName().toString()) // 각 파일 경로에서 파일 이름만 추출하여 문자열로 변환합니다.
                    .filter(name -> name.toLowerCase().endsWith(".mp3")) // 파일 이름이 ".mp3"로 끝나는 파일만 필터링합니다.
                    .collect(Collectors.toList()); // 결과를 List<String>으로 수집합니다.

            listView.getItems().addAll(fileNames); // 가져온 파일 이름 목록을 ListView의 아이템으로 추가합니다.
        } catch (IOException e) {
            System.err.println("Error reading music directory: " + e.getMessage()); // 파일 읽기 중 오류가 발생하면 오류 메시지를 출력합니다.
        }
    }

    // 메디아 플레이어
    @FXML
    public void initialize()   {
        // ListView에 CheckBoxListCell을 사용하도록 설정
        listView.setCellFactory(lv -> new CheckBoxListCell());  // lv 가 뭔지모르겠음 아 list view ?
        // 이건 로컬에서 갖고오기
        // music 폴더 내의 모든 파일을 ListView에 추가합니다.
        loadMusicFiles();




    }



    @FXML
    private void selectStart(ActionEvent event) {  // 시작버튼 눌렀을때!!!
        // 음악 파일 선택한거 재생 어떻게하지,,?



        // 재생 하는 거
        String fileName = "music/infinityJourney.mp3";


        // 이걸이제 선택한걸 재생하게
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
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
import java.util.EventListener;
import java.util.List;
import java.util.stream.Collectors;

// 파일입출력으로 폴더에있는 파일명 리스트에 추가.
public class TimerStartModalController {

    // listView ??
    @FXML
    private ListView<String> listView; // FXML에서 정의한 ListView와 연결

    @FXML
    private Button selectButton;

    @FXML
    private Button fileButton;

    @FXML
    private Button linkButton;


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
    // 이거는 잘 갖고옴 근데 선택된 파일이름 리스트 잘 못갖고옴...

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
    private void selectStart(ActionEvent event) {  // 모달 창에서 시작버튼 눌렀을때!!! 인데 지금 선택된 파일 재생시키려고하는데 잘안도미.
        // 음악 파일 선택한거 재생 어떻게하지,,?

        // ListView에서 선택된 항목을 가져옵니다.
        String selectedFile = listView.getSelectionModel().getSelectedItem();
        System.out.println("ListView <- SelectedFile ::" + selectedFile);
        // 이게 null 나옴
//        System.out.println("ListView <- : " + listView);
//        System.out.println(" listView.getSelectionModel() :: " + listView.getSelectionModel()); // 여기는 에러안나네

        // 선택된 항목이 있다면, 해당 파일을 재생합니다.
        if (selectedFile != null) {
            // 사용자의 홈 디렉토리 내 music 폴더의 경로를 기준으로 파일 경로를 생성합니다.
            String userHome = System.getProperty("user.home");
            Path filePath = Paths.get(userHome, "Music", selectedFile);

            System.out.println("checkFil?" + selectedFile);

            // 파일이 실제로 존재하는지 확인하고 재생합니다.
            if (Files.exists(filePath)) {
                playHitSound(filePath.toUri().toString());

                System.out.println("file true exist? " + filePath.toUri().toString());
            } else {
                System.err.println("File not found: " + selectedFile);
            }
        }

        // 이게 근데 파일 하나만 재생되나
        // 창닫히기
        // 모달 창이 닫힐 때 선택된 파일 정보 초기화
        listView.getSelectionModel().clearSelection();

        System.out.println("list chogihwa" + listView.getSelectionModel().getSelectedItem());
        //

        // 여기서 이제 모달 창 닫힐때 모든 변수 초기화?? 하는 게 필요함 다시 쓸 때 똑같은거 불러오니까!!

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();



    }

//    private void playHitSound(String fileName){
//        URL resource = getClass().getClassLoader().getResource(fileName);
//        System.out.println(resource);
//        if (resource == null) {
//            System.err.println("File not found: " + fileName);
//            return;
//        }
//        String path = resource.getPath();
//        Media media = new Media(new File(path).toURI().toString());
//        mediaPlayer = new MediaPlayer(media);
//        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
//        mediaPlayer.play();
//    }

    private void playHitSound(String uri) {
        if(mediaPlayer != null) {
            shutDown(mediaPlayer);  // mediaPlayer 가 이미있으면 혹시 그럼 null 로만들고 새롭게 다시 생성,,
        }
        Media media = new Media(uri);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();
    }


    public MediaPlayer shutDown(MediaPlayer mediaPlayer){
        mediaPlayer.stop();
        mediaPlayer.dispose();
        return null;
    }


    @FXML 
    private  void moveFileList(ActionEvent event){
        System.out.println("파일버튼");
    }

    @FXML
    private  void moveLinkList(ActionEvent event){
        System.out.println("링크버튼");
    }


    
}
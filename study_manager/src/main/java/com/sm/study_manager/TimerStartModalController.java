package com.sm.study_manager;

// 모달창 리스트뷰를 컨트롤 ,, 버튼 누르면 창꺼지게 해놓자 일단.

import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
//import javafx.scene.control.cell.CheckBoxListCell;
// 이 체크박스 아니고
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Callback;
import javafx.util.Duration;

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
    private ListView<ListItem> listView; // 이제 ListItem // 챗지피티써서 커스텀으로 체크 + 리스트뷰 만들었는데, 그냥 체크말고 선택하나만
    // 하기로해요 ㅠㅠ

    @FXML
    private  ListView<String> linkListView;  // 이게
    @FXML
    private Button fileSelectButton;    // 파일 고르고 시작 버튼

    @FXML
    private Button linkSelectButton;    // 링크 고르고 시작버튼

    @FXML
    private Button fileButton;  // 위에 파일 뷰로 갈지

    @FXML
    private Button linkButton;  // 위에 링크 뷰로 갈지.

    @FXML
    private AnchorPane linkPane; // fileButton 눌렀을 떄 나올 패널

    @FXML
    private AnchorPane filePane; // linkButton 눌렀을 떄 나올 패널

    MediaPlayer mediaPlayer;

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    private void loadMusicFiles() {
        String userHome = System.getProperty("user.home"); // 현재 사용자의 홈 디렉토리 경로를 가져옵니다.
        Path musicDirectory = Paths.get(userHome, "music"); // 사용자의 홈 디렉토리 내에 있는 'music' 폴더의 경로를 생성합니다.

        try {
            List<ListItem> items = Files.list(musicDirectory) // 'music' 디렉토리 내의 모든 파일을 Stream으로 가져옵니다.
                    .filter(Files::isRegularFile) // 일반 파일만 필터링합니다.
                    .map(path -> path.getFileName().toString()) // 파일 이름을 문자열로 변환합니다.
                    .filter(name -> name.toLowerCase().endsWith(".mp3")) // ".mp3"로 끝나는 파일만 필터링합니다.
                    .map(ListItem::new) // 파일 이름을 이용하여 ListItem 객체를 생성합니다.
                    .collect(Collectors.toList()); // 결과를 List<ListItem>으로 수집합니다.

            listView.getItems().addAll(items); // 변환된 ListItem 객체 목록을 ListView에 추가합니다.
        } catch (IOException e) {
            System.err.println("Error reading music directory: " + e.getMessage()); // 오류 발생 시 메시지를 출력합니다.
        }
    }

    // 메디아 플레이어
    @FXML
    public void initialize() {
        // ListView에 CheckBoxListCell을 사용하도록 설정
        listView.setCellFactory(CheckBoxListCell.forListView(new Callback<ListItem, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(ListItem item) {
                return item.checkedProperty();
            }
        }));


        loadMusicFiles();


    }


    @FXML
    private void selectStart(ActionEvent event) {
        // 모달 창에서 시작버튼 눌렀을때!!! 인데 지금 선택된 파일 재생시키려고하는데 잘안도미.
        // 음악 파일 선택한거 재생 어떻게하지,,?
        // ListView에서 선택된 ListItem 객체를 가져옵니다.
        ListItem selectedItem = listView.getSelectionModel().getSelectedItem();
        // ListView에서 선택된 항목을 가져옵니다.


        // 선택된 항목이 있다면, 해당 파일을 재생합니다.
        if (selectedItem != null) {
            String selectedFileName = selectedItem.getText();
            // 사용자의 홈 디렉토리 내 music 폴더의 경로를 기준으로 파일 경로를 생성합니다.
            String userHome = System.getProperty("user.home");
            Path filePath = Paths.get(userHome, "Music", selectedFileName);

            System.out.println("checkFil?" + selectedFileName);

            // 파일이 실제로 존재하는지 확인하고 재생합니다.
            if (Files.exists(filePath)) {
                playHitSound(filePath.toUri().toString());

                System.out.println("file true exist? " + filePath.toUri().toString());
            } else {
                System.err.println("File not found: " + selectedFileName);
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
    private void playHitSound(String uri) {
        if (mediaPlayer != null) {
            shutDown(mediaPlayer);  // mediaPlayer 가 이미있으면 혹시 그럼 null 로만들고 새롭게 다시 생성,,
        }
        Media media = new Media(uri);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();
    }

    public MediaPlayer shutDown(MediaPlayer mediaPlayer) {
        mediaPlayer.stop();
        mediaPlayer.dispose();
        return null;
    }
    @FXML
    private void moveFileList(ActionEvent event) {
        // linkPane을 화면 밖으로 이동
        TranslateTransition tr1 = new TranslateTransition();
        tr1.setDuration(Duration.millis(100));
        tr1.setToX(0);
        tr1.setToY(-1000);  // linkPane을 화면 밖으로 이동
        tr1.setNode(linkPane);

        // filePane을 화면 안으로 이동
        TranslateTransition tr2 = new TranslateTransition();
        tr2.setDuration(Duration.millis(100));
        tr2.setFromY(-1000); // filePane을 화면 밖에서 시작
        tr2.setToX(0);
        tr2.setToY(0);
        tr2.setNode(filePane);

        ParallelTransition pt = new ParallelTransition(tr1, tr2);
        pt.play();  // 애니메이션 실행

        System.out.println("파일 버튼");
    }

    @FXML
    private void moveLinkList(ActionEvent event) {
        // filePane을 화면 밖으로 이동
        TranslateTransition tr1 = new TranslateTransition();
        tr1.setDuration(Duration.millis(100));
        tr1.setToX(-1000);
        tr1.setToY(0);  // filePane을 화면 밖으로 이동
        tr1.setNode(filePane);

        // linkPane을 화면 안으로 이동
        TranslateTransition tr2 = new TranslateTransition();
        tr2.setDuration(Duration.millis(100));
        tr2.setFromX(-1000); // linkPane을 화면 밖에서 시작
        tr2.setToX(0);
        tr2.setToY(0);
        tr2.setNode(linkPane);

        ParallelTransition pt = new ParallelTransition(tr1, tr2);
        pt.play();  // 애니메이션 실행

        System.out.println("링크 버튼");
    }



}
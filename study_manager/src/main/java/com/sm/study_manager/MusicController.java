package com.sm.study_manager;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MusicController extends CommonController{
//
    @FXML
    private ListView<String> musicFileList; // 음악 파일 리스트 뷰

    @FXML
    private Button insertFileButton; // 파일 추가 버튼

    @FXML
    private Button deleteFileButton;
    @FXML
    private void deleteFile() {
        String selectedFileName = musicFileList.getSelectionModel().getSelectedItem();

        if (selectedFileName != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("파일 삭제 확인");
            confirmAlert.setHeaderText("파일 삭제");
            confirmAlert.setContentText(selectedFileName + " 파일을 정말로 삭제하시겠습니까?");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // 사용자가 'OK'를 클릭한 경우
                try {
                    String userHome = System.getProperty("user.home");
                    Path filePath = Paths.get(userHome, "Music", selectedFileName);

                    if (Files.exists(filePath)) {
                        Files.delete(filePath); // 파일 시스템에서 파일 삭제
                        System.out.println("파일이 성공적으로 삭제되었습니다: " + filePath);

                        musicFileList.getItems().remove(selectedFileName); // ListView에서 항목 제거
//                        SharedData.getSharedMusicList().remove(selectedFileName); // 공유된 리스트에서도 항목 제거
                    } else {
                        System.err.println("파일을 찾을 수 없습니다: " + filePath);
                    }
                } catch (IOException e) {
                    System.err.println("파일 삭제 중 오류 발생: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                // 사용자가 'Cancel'을 클릭한 경우 또는 대화 상자를 닫은 경우
                System.out.println("파일 삭제가 취소되었습니다.");
            }
        }
    }


    @FXML
    private void insertFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("음악 파일 선택");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("음악 파일", "*.mp3"));

        // 파일 선택 대화상자를 표시하고 사용자가 선택한 파일을 가져옵니다.
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                // 사용자 홈 디렉토리 경로를 기반으로 대상 경로를 구성합니다.
                String userHome = System.getProperty("user.home");
                Path dest = Path.of(userHome, "Music", selectedFile.getName());

                // 파일을 지정된 경로로 복사
                Files.copy(selectedFile.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("파일이 성공적으로 추가되었습니다: " + dest);

                // ListView에 새로 추가된 파일 이름을 추가
                musicFileList.getItems().add(dest.getFileName().toString());
            } catch (Exception e) {
                System.err.println("파일 추가 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    public void initialize(){
//        musicFileList.setItems(SharedData.getSharedMusicList());

        musicFileList.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); // 아이템이 없거나 null인 경우 텍스트를 표시하지 않음
                } else {
                    setText(item); // 아이템이 있는 경우 해당 문자열을 텍스트로 표시
                }
            }
        });

        loadMusicFiles();
        // ... 기타 메서드 ...

    }

    private void loadMusicFiles() {
        String userHome = System.getProperty("user.home"); // 현재 사용자의 홈 디렉토리 경로를 가져옵니다.
        Path musicDirectory = Paths.get(userHome, "Music"); // 사용자의 홈 디렉토리 내에 있는 'Music' 폴더의 경로를 생성합니다.

        try {
            List<String> items = Files.list(musicDirectory) // 'Music' 디렉토리 내의 모든 파일을 Stream으로 가져옵니다.
                    .filter(Files::isRegularFile) // 일반 파일만 필터링합니다.
                    .map(path -> path.getFileName().toString()) // 파일 이름을 문자열로 변환합니다.
                    .filter(name -> name.toLowerCase().endsWith(".mp3")) // ".mp3"로 끝나는 파일만 필터링합니다.
                    .collect(Collectors.toList()); // 결과를 List<String>으로 수집합니다.

            musicFileList.getItems().addAll(items); // 변환된 문자열 목록을 ListView에 추가합니다.
            // 지피티가 공유하는 그 리스트로 관리하라고 해서.
//            ObservableList<String> sharedList = SharedData.getSharedMusicList();
//            sharedList.clear();
//            sharedList.addAll(items); // 로드된 항목을 공유 리스트에 추가
//
//            musicFileList.setItems(sharedList); // ListView에 공유된 리스트 설정
            // ㅠㅠ
        } catch (IOException e) {
            System.err.println("Error reading music directory: " + e.getMessage()); // 오류 발생 시 메시지를 출력합니다.
        }
    }







}
